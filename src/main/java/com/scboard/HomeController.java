// 처음 들어오면 글 목록으로 보냄
package com.scboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/posts";
    }

    @PostMapping("/fragment")
    public String fragment(@RequestParam String section) {
        return "fragments/header :: " + section;
    }
}
