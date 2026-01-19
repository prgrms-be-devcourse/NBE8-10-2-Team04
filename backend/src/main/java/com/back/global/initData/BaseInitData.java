package com.back.global.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.createDefaultCategory();
        };
    }

    // 서버 실행 시 카테고리 생성
    @Transactional
    public void createDefaultCategory() {
        // TODO: 저장된 카테고리가 없으면 생성하는 코드 추가
    }
}
