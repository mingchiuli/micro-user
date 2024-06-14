package org.chiu.micro.user.cache.handler;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.chiu.micro.user.cache.CacheEvictHandler;
import org.chiu.micro.user.cache.config.CacheKeyGenerator;
import org.chiu.micro.user.repository.RoleRepository;
import org.chiu.micro.user.wrapper.RoleAuthorityWrapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class AllAuthorityCacheEvictHandler extends CacheEvictHandler {

    private final RoleRepository roleRepository;

    private final CacheKeyGenerator cacheKeyGenerator;


    @SneakyThrows
    @Override
    public Set<String> handle(Object[] args) {
        List<String> roleList = roleRepository.findAllCodes();

        Method method = RoleAuthorityWrapper.class.getMethod("getAuthoritiesByRoleCode", String.class);
        Set<String> set = new HashSet<>();
        roleList.forEach(role -> set.add(cacheKeyGenerator.generateKey(method, role)));
        return set;
    }

}
