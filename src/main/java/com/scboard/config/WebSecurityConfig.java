/*
통신구간 암호화 미적용 (TLS/HTTPS) [WEB-SER-051] 조치
SSL Stripping을 막기 위한 HSTS 설정.
*/
package com.scboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // [보안 패치] 브라우저에 HTTPS 접속 강제 (HSTS 1년 설정)
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
