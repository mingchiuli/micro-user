package org.chiu.micro.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.chiu.micro.user.exception.MissException;
import org.chiu.micro.user.entity.AuthorityEntity;
import org.chiu.micro.user.repository.AuthorityRepository;
import org.chiu.micro.user.req.AuthorityEntityReq;
import org.chiu.micro.user.service.AuthorityService;
import org.chiu.micro.user.vo.AuthorityVo;
import org.chiu.micro.user.wrapper.AuthorityWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.chiu.micro.user.convertor.AuthorityVoConvertor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.chiu.micro.user.lang.ExceptionMessage.NO_FOUND;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    private final AuthorityWrapper authorityWrapper;

    private final ObjectMapper objectMapper;

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
        authorityWrapper.save(authorityEntity);
    }

    @Override
    public void deleteAuthorities(List<Long> ids) {
        authorityWrapper.deleteAllById(ids);
    }

    @SneakyThrows
    @Override
    public void download(HttpServletResponse response) {
        ServletOutputStream outputStream = response.getOutputStream();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        List<AuthorityEntity> authorities = authorityRepository.findAll();
        byte[] bytes = objectMapper.writeValueAsBytes(authorities);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
