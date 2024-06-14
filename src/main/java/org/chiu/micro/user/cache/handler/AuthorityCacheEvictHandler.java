package org.chiu.micro.user.cache.handler;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.chiu.micro.user.cache.CacheEvictHandler;
import org.chiu.micro.user.cache.config.CacheKeyGenerator;
import org.chiu.micro.user.entity.RoleEntity;
import org.chiu.micro.user.repository.RoleRepository;
import org.chiu.micro.user.wrapper.RoleAuthorityWrapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class AuthorityCacheEvictHandler extends CacheEvictHandler {

    private final CacheKeyGenerator cacheKeyGenerator;

    private final RoleRepository roleRepository;


    @SneakyThrows
    @Override
    public Set<String> handle(Object[] args) {
        Object role = args[0];
        Object roleCode;

        switch (role) {
            case String ignored -> roleCode = role;
            case Long roleId -> {
                Optional<RoleEntity> optionalRole = roleRepository.findById(roleId);
                if (optionalRole.isPresent()) {
                    roleCode = optionalRole.get().getCode();
                } else {
                    return Collections.emptySet();
                }
            }
            case RoleEntity roleEntity -> roleCode = roleEntity.getCode();
            case null, default -> {
                return Collections.emptySet();
            }
        }

        Method method = RoleAuthorityWrapper.class.getMethod("getAuthoritiesByRoleCode", String.class);
        String key = cacheKeyGenerator.generateKey(method, roleCode);
        return Collections.singleton(key);
    }

}
