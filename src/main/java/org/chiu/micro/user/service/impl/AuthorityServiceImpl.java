package org.chiu.micro.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.chiu.micro.user.exception.MissException;
import org.chiu.micro.user.lang.AuthMenuOperateEnum;
import org.chiu.micro.user.lang.Const;
import org.chiu.micro.user.entity.AuthorityEntity;
import org.chiu.micro.user.repository.AuthorityRepository;
import org.chiu.micro.user.repository.RoleRepository;
import org.chiu.micro.user.req.AuthorityEntityReq;
import org.chiu.micro.user.service.AuthorityService;
import org.chiu.micro.user.vo.AuthorityVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.chiu.micro.user.constant.UserAuthMenuOperateMessage;
import org.chiu.micro.user.convertor.AuthorityVoConvertor;

import java.util.List;
import java.util.Objects;

import static org.chiu.micro.user.lang.ExceptionMessage.NO_FOUND;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    private final RoleRepository roleRepository;

    @Override
    public List<AuthorityVo> findAll() {
        List<AuthorityEntity> authorityEntities = authorityRepository.findAll();
        return AuthorityVoConvertor.convert(authorityEntities);
    }

    @Override
    public AuthorityVo findById(Long id) {
        AuthorityEntity authorityEntity = authorityRepository.findById(id)
                .orElseThrow(() -> new MissException(NO_FOUND));
        return AuthorityVoConvertor.convert(authorityEntity);
    }

    @Override
    public void saveOrUpdate(AuthorityEntityReq req) {

        Long id = req.getId();
        AuthorityEntity authorityEntity;

        if (Objects.nonNull(id)) {
            authorityEntity = authorityRepository.findById(id)
                    .orElseThrow(() -> new MissException(NO_FOUND));
        } else {
            authorityEntity = new AuthorityEntity();
        }

        BeanUtils.copyProperties(req, authorityEntity);
        authorityRepository.save(authorityEntity);

        //全部权限
        List<String> allRoleCodes = roleRepository.findAllCodes();
        var data = UserAuthMenuOperateMessage.builder()
                .roles(allRoleCodes)
                .type(AuthMenuOperateEnum.AUTH.getType())
                .build();
        rabbitTemplate.convertAndSend(Const.CACHE_USER_EVICT_EXCHANGE.getInfo(), Const.CACHE_USER_EVICT_BINDING_KEY.getInfo(), data);
    }

    @Override
    public void deleteAuthorities(List<Long> ids) {

        authorityRepository.deleteAllById(ids);
        //全部权限
        List<String> allRoleCodes = roleRepository.findAllCodes();
        var data = UserAuthMenuOperateMessage.builder()
                .roles(allRoleCodes)
                .type(AuthMenuOperateEnum.AUTH.getType())
                .build();
        rabbitTemplate.convertAndSend(Const.CACHE_USER_EVICT_EXCHANGE.getInfo(), Const.CACHE_USER_EVICT_BINDING_KEY.getInfo(), data);
    }

    @SneakyThrows
    @Override
    public byte[] download() {
        List<AuthorityEntity> authorities = authorityRepository.findAll();
        return objectMapper.writeValueAsBytes(authorities);
    }
}
