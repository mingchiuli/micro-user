package org.chiu.micro.user.wrapper;

import lombok.RequiredArgsConstructor;
import org.chiu.micro.user.cache.CacheEvict;
import org.chiu.micro.user.cache.handler.AuthorityCacheEvictHandler;
import org.chiu.micro.user.cache.handler.MenuAndButtonCacheEvictHandler;
import org.chiu.micro.user.entity.RoleEntity;
import org.chiu.micro.user.repository.RoleRepository;
import org.springframework.stereotype.Component;

/**
 * @Author limingjiu
 * @Date 2024/5/29 19:24
 **/
@Component
@RequiredArgsConstructor
public class RoleWrapper {

    private final RoleRepository roleRepository;

    @CacheEvict(handler = { AuthorityCacheEvictHandler.class, MenuAndButtonCacheEvictHandler.class })
    public void save(RoleEntity roleEntity) {
        roleRepository.save(roleEntity);
    }
}
