package org.chiu.micro.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.user.convertor.RoleAuthorityEntityConvertor;
import org.chiu.micro.user.entity.AuthorityEntity;
import org.chiu.micro.user.entity.RoleAuthorityEntity;
import org.chiu.micro.user.repository.AuthorityRepository;
import org.chiu.micro.user.repository.RoleAuthorityRepository;
import org.chiu.micro.user.service.RoleAuthorityService;
import org.chiu.micro.user.vo.RoleAuthorityVo;
import org.chiu.micro.user.wrapper.RoleAuthorityWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.chiu.micro.user.lang.StatusEnum.NORMAL;


@Service
@RequiredArgsConstructor
public class RoleAuthorityServiceImpl implements RoleAuthorityService {

    private final RoleAuthorityWrapper roleAuthorityWrapper;

    private final AuthorityRepository authorityRepository;

    private final RoleAuthorityRepository roleAuthorityRepository;

    @Override
    public List<String> getAuthoritiesByRoleCodes(List<String> roleCodes) {
        List<String> allAuthorities = new ArrayList<>();
        roleCodes.forEach(role -> allAuthorities.addAll(roleAuthorityWrapper.getAuthoritiesByRoleCode(role)));
        return allAuthorities.stream()
                .distinct()
                .toList();
    }

    /**
     * @param roleId
     * @param authorityIds
     */
    @Override
    public void saveAuthority(Long roleId, List<Long> authorityIds) {
        List<RoleAuthorityEntity> roleAuthorityEntities = RoleAuthorityEntityConvertor.convert(roleId, authorityIds);
        roleAuthorityWrapper.saveAuthority(roleId, new ArrayList<>(roleAuthorityEntities));
    }

    @Override
    public List<RoleAuthorityVo> getAuthoritiesInfo(Long roleId) {
        List<AuthorityEntity> allAuthorityEntities = authorityRepository.findByStatus(NORMAL.getCode());
        List<RoleAuthorityEntity> authorityEntities = roleAuthorityRepository.findByRoleId(roleId);

        List<Long> ids = authorityEntities.stream()
                .map(RoleAuthorityEntity::getAuthorityId)
                .toList();
        List<RoleAuthorityVo> roleAuthorityVos = new ArrayList<>();

        allAuthorityEntities.forEach(item -> roleAuthorityVos
                .add(RoleAuthorityVo.builder()
                        .authorityId(item.getId())
                        .code(item.getCode())
                        .check(ids.contains(item.getId()))
                        .build()));
        return roleAuthorityVos;
    }
}
