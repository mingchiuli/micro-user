package org.chiu.micro.user.wrapper;

import lombok.RequiredArgsConstructor;

import org.chiu.micro.user.cache.CacheEvict;
import org.chiu.micro.user.cache.handler.AllAuthorityCacheEvictHandler;
import org.chiu.micro.user.entity.AuthorityEntity;
import org.chiu.micro.user.repository.AuthorityRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorityWrapper {

    private final AuthorityRepository authorityRepository;

    @CacheEvict(handler = { AllAuthorityCacheEvictHandler.class })
    public void save(AuthorityEntity authorityEntity) {
        authorityRepository.save(authorityEntity);
    }

    @CacheEvict(handler = { AllAuthorityCacheEvictHandler.class })
    public void deleteAllById(List<Long> ids) {
        authorityRepository.deleteAllById(ids);
    }
}
