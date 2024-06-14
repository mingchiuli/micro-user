package org.chiu.micro.user.wrapper;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.user.cache.config.Cache;
import org.chiu.micro.user.lang.Const;
import org.chiu.micro.user.entity.AuthorityEntity;
import org.chiu.micro.user.entity.RoleAuthorityEntity;
import org.chiu.micro.user.entity.RoleEntity;
import org.chiu.micro.user.repository.AuthorityRepository;
import org.chiu.micro.user.repository.RoleAuthorityRepository;
import org.chiu.micro.user.repository.RoleRepository;
import org.chiu.micro.user.cache.CacheEvict;
import org.chiu.micro.user.cache.handler.AuthorityCacheEvictHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.chiu.micro.user.lang.StatusEnum.NORMAL;

@Component
@RequiredArgsConstructor
public class RoleAuthorityWrapper {

    private final RoleAuthorityRepository roleAuthorityRepository;

    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    @Cache(prefix = Const.HOT_AUTHORITIES)
    public List<String> getAuthoritiesByRoleCode(String roleCode) {

        if ("REFRESH_TOKEN".equals(roleCode)) {
            return Collections.singletonList("token:refresh");
        }

        Optional<RoleEntity> roleEntityOptional = roleRepository.findByCodeAndStatus(roleCode, NORMAL.getCode());

        if (roleEntityOptional.isEmpty()) {
            return Collections.emptyList();
        }

        RoleEntity roleEntity = roleEntityOptional.get();

        List<Long> authorityIds = roleAuthorityRepository.findByRoleId(roleEntity.getId()).stream()
                .map(RoleAuthorityEntity::getAuthorityId)
                .toList();

        List<AuthorityEntity> authorities = authorityRepository.findAllById(authorityIds).stream()
                .filter(item -> NORMAL.getCode().equals(item.getStatus()))
                .toList();

        return authorities.stream()
                .map(AuthorityEntity::getCode)
                .toList();
    }

    @Transactional
    @CacheEvict(handler = { AuthorityCacheEvictHandler.class })
    public void saveAuthority(Long roleId, List<RoleAuthorityEntity> roleAuthorityEntities) {
        roleAuthorityRepository.deleteByRoleId(roleId);
        roleAuthorityRepository.saveAll(roleAuthorityEntities);
    }
}
