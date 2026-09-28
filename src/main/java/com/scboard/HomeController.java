// 처음 들어오면 글 목록으로 보냄

/*
서버 사이드 템플릿 인젝션 (SSTI & CVE-2026-40478) [WEB-SER-052] 보안조치

1. 입력값을 사전에 정의한 단어로만 통과시키는 화이트리스트식 검사를 통해 문자열 조작 불가하게 조치.
2. bundle.gradle(3.14)업그레이드로 엔진 자체의 취약점을 안전한 버전으로 교체.
*/
package com.scboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/posts";
    }

    // [보안 패치] 허용된 fragment 식별자 화이트리스트 정의
    private static final Set<String> ALLOWED_SECTIONS = Set.of("header", "nav", "footer", "content");

    @PostMapping("/fragment")
    public String getFragment(@RequestParam(value = "section", required = false) String section) {
        // [수정 전 Line 18]: return "fragments/header :: " + section;

        // 1. 파라미터가 없거나 화이트리스트에 부합하지 않으면 즉시 400 Bad Request 에러 반환
        if (section == null || !ALLOWED_SECTIONS.contains(section.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않은 Fragment 요청입니다.");
        }

        // 2. 검증된 안전한 문자열만 결합하여 반환
        return "fragments/header :: " + section.trim();
    }
}
