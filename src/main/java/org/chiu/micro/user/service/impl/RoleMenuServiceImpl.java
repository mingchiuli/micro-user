package org.chiu.micro.user.service.impl;

import org.chiu.micro.user.exception.CommitException;
import org.chiu.micro.user.lang.AuthMenuOperateEnum;
import org.chiu.micro.user.constant.AuthMenuIndexMessage;
import org.chiu.micro.user.convertor.ButtonDtoConvertor;
import org.chiu.micro.user.convertor.MenuDisplayDtoConvertor;
import org.chiu.micro.user.convertor.MenuDisplayVoConvertor;
import org.chiu.micro.user.convertor.MenuDtoConvertor;
import org.chiu.micro.user.convertor.MenuWithChildDtoConvertor;
import org.chiu.micro.user.convertor.MenusWithChildAndButtonsVoConvertor;
import org.chiu.micro.user.convertor.RoleMenuEntityConvertor;
import org.chiu.micro.user.dto.ButtonDto;
import org.chiu.micro.user.dto.MenuDisplayDto;
import org.chiu.micro.user.dto.MenuDto;
import org.chiu.micro.user.dto.MenuWithChildDto;
import org.chiu.micro.user.dto.MenusAndButtonsDto;
import org.chiu.micro.user.dto.MenusWithChildAndButtonsDto;
import org.chiu.micro.user.entity.MenuEntity;
import org.chiu.micro.user.entity.RoleEntity;
import org.chiu.micro.user.entity.RoleMenuEntity;
import org.chiu.micro.user.event.AuthMenuOperateEvent;
import org.chiu.micro.user.repository.MenuRepository;
import org.chiu.micro.user.repository.RoleMenuRepository;
import org.chiu.micro.user.repository.RoleRepository;
import org.chiu.micro.user.service.RoleMenuService;
import org.chiu.micro.user.vo.*;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.user.wrapper.RoleMenuWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.chiu.micro.user.lang.ExceptionMessage.MENU_INVALID_OPERATE;
import static org.chiu.micro.user.convertor.MenuDisplayVoConvertor.buildTreeMenu;

import java.util.Optional;

import static org.chiu.micro.user.lang.StatusEnum.NORMAL;
import static org.chiu.micro.user.lang.TypeEnum.*;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:26 am
 */
@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl implements RoleMenuService {

    private final MenuRepository menuRepository;

    private final RoleMenuRepository roleMenuRepository;

    private final RoleMenuWrapper roleMenuWrapper;

    private final RoleRepository roleRepository;

    private final ApplicationContext applicationContext;

    private List<RoleMenuVo> setCheckMenusInfo(List<MenuDisplayVo> menusInfo, List<Long> menuIdsByRole, List<RoleMenuVo> parentChildren) {
        menusInfo.forEach(item -> {
            RoleMenuVo.RoleMenuVoBuilder builder = RoleMenuVo.builder()
                    .title(item.getTitle())
                    .menuId(item.getMenuId());

            if (menuIdsByRole.contains(item.getMenuId())) {
                builder.check(true);
            }

            if (Boolean.FALSE.equals(item.getChildren().isEmpty())) {
                List<RoleMenuVo> children = new ArrayList<>();
                builder.children(children);
                setCheckMenusInfo(item.getChildren(), menuIdsByRole, children);
            }
            parentChildren.add(builder.build());
        });

        return parentChildren;
    }

    @Override
    public MenusAndButtonsVo getCurrentUserNav(String role) {
    
        MenusAndButtonsDto menusAndButtonsDto = getCurrentRoleNav(role);
        List<MenuDto> menus = menusAndButtonsDto.getMenus();
        List<ButtonDto> buttons = menusAndButtonsDto.getButtons();

        List<MenuDisplayDto> menuEntities = MenuDisplayDtoConvertor.convert(menus, true);
        List<MenuDisplayDto> displayDtos = MenuDisplayDtoConvertor.buildTreeMenu(menuEntities);
        List<MenuWithChildDto> menuDtos = MenuWithChildDtoConvertor.convert(displayDtos);
        List<ButtonDto> buttonDtos = ButtonDtoConvertor.convert(buttons, true);

        return MenusWithChildAndButtonsVoConvertor.convert(MenusWithChildAndButtonsDto.builder()
                .buttons(buttonDtos)
                .menus(menuDtos)
                .build());
    }

    public List<RoleMenuVo> getMenusInfo(Long roleId) {
        List<MenuDisplayVo> menusInfo = getNormalMenusInfo();
        List<Long> menuIdsByRole = roleMenuRepository.findMenuIdsByRoleId(roleId);
        return setCheckMenusInfo(menusInfo, menuIdsByRole, new ArrayList<>());
    }

    @Override
    public void saveMenu(Long roleId, List<Long> menuIds) {
        List<RoleMenuEntity> roleMenuEntities = RoleMenuEntityConvertor.convert(roleId, menuIds);

        roleMenuWrapper.saveMenu(roleId, new ArrayList<>(roleMenuEntities));
        // 按钮
        roleRepository.findById(roleId)
                .map(RoleEntity::getCode)
                .ifPresent(role -> {
                    var authMenuIndexMessage = new AuthMenuIndexMessage(Collections.singletonList(role), AuthMenuOperateEnum.MENU.getType());
                    applicationContext.publishEvent(new AuthMenuOperateEvent(this, authMenuIndexMessage));
                });

    }

    public List<MenuDisplayVo> getNormalMenusInfo() {
        List<Long> menuIds = menuRepository.findAllIds();
        return buildMenu(menuIds, true);
    }

    private List<MenuDisplayVo> buildMenu(List<Long> menuIds, Boolean statusCheck) {
        List<MenuEntity> menus = menuRepository.findAllById(menuIds);
        List<MenuDisplayVo> menuEntities = MenuDisplayVoConvertor.convert(menus, statusCheck);
        // 转树状结构
        return buildTreeMenu(menuEntities);
    }

    @Override
    public void delete(Long id) {
        List<MenuEntity> menus = menuRepository.findByParentId(id);
        if (Boolean.FALSE.equals(menus.isEmpty())) {
            throw new CommitException(MENU_INVALID_OPERATE);
        }
        roleMenuWrapper.deleteMenu(id);
        //全部按钮
        List<String> allRoleCodes = roleRepository.findAllCodes();
        var authMenuIndexMessage = new AuthMenuIndexMessage(allRoleCodes, AuthMenuOperateEnum.MENU.getType());
        applicationContext.publishEvent(new AuthMenuOperateEvent(this, authMenuIndexMessage));
    }

    private MenusAndButtonsDto getCurrentRoleNav(String role) {

        Optional<RoleEntity> roleEntityOptional = roleRepository.findByCodeAndStatus(role, NORMAL.getCode());

        if (roleEntityOptional.isEmpty()) {
            return MenusAndButtonsDto.builder()
                    .menus(Collections.emptyList())
                    .buttons(Collections.emptyList())
                    .build();
        }

        RoleEntity roleEntity = roleEntityOptional.get();

        List<Long> menuIds = roleMenuRepository.findMenuIdsByRoleId(roleEntity.getId());

        List<MenuEntity> allKindsInfo = menuRepository.findAllById(menuIds);

        List<MenuEntity> menus = allKindsInfo
                .stream()
                .filter(menu -> CATALOGUE.getCode().equals(menu.getType()) || MENU.getCode().equals(menu.getType()))
                .toList();

        List<MenuEntity> buttons = allKindsInfo
                .stream()
                .filter(menu -> BUTTON.getCode().equals(menu.getType()))
                .toList();

        List<MenuDto> menuDtos = MenuDtoConvertor.convert(menus);
        List<ButtonDto> buttonDtos = ButtonDtoConvertor.convert(buttons);

        return MenusAndButtonsDto.builder()
                .buttons(buttonDtos)
                .menus(menuDtos)
                .build();
    }
}
