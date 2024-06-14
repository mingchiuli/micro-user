package org.chiu.micro.user.wrapper;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.user.cache.CacheEvict;
import org.chiu.micro.user.cache.handler.BatchAuthorityCacheEvictHandler;
import org.chiu.micro.user.cache.handler.BatchMenuAndButtonCacheEvictHandler;
import org.chiu.micro.user.repository.RoleAuthorityRepository;
import org.chiu.micro.user.repository.RoleMenuRepository;
import org.chiu.micro.user.repository.RoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleMenuAuthorityWrapper {

    private final RoleRepository roleRepository;

    private final RoleAuthorityRepository roleAuthorityRepository;

    private final RoleMenuRepository roleMenuRepository;


    @Transactional
    @CacheEvict(handler = { BatchAuthorityCacheEvictHandler.class, BatchMenuAndButtonCacheEvictHandler.class })
    public void delete(List<Long> ids) {
        roleRepository.deleteAllById(ids);
        roleMenuRepository.deleteAllByRoleId(ids);
        roleAuthorityRepository.deleteAllByRoleId(ids);
    }
}
