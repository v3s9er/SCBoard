// 일반 로그인, 구글 로그인
package com.scboard.user;

import com.scboard.user.dto.SignupForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

@Controller
public class AuthController {
    private final UserService userService;
    @Value("${google.client-id:}")
    private String googleClientId;
    @Value("${google.client-secret:}")
    private String googleClientSecret;
    @Value("${google.redirect-uri:http://localhost:8080/login/oauth2/code/google}")
    private String googleRedirectUri;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        model.addAttribute("oauthLoginAvailable", !googleClientId.isBlank());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("oauthLoginAvailable", !googleClientId.isBlank());
            return "auth/signup";
        }
        if (!userService.signup(form)) {
            result.rejectValue("username", "duplicate", "이미 사용 중인 아이디입니다.");
            model.addAttribute("oauthLoginAvailable", !googleClientId.isBlank());
            return "auth/signup";
        }
        return "redirect:/login?signup";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("oauthLoginAvailable", !googleClientId.isBlank());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        HttpSession session) {
        User user = userService.login(username, password);
        if (user == null) {
            return "redirect:/login?error";
        }
        session.setAttribute("loginUserId", user.getId());
        session.setAttribute("loginNickname", user.getNickname());
        return "redirect:/posts";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/posts";
    }

    @GetMapping("/oauth2/authorization/google")
    public String googleLogin() {
        if (googleClientId.isBlank()) {
            return "redirect:/login?oauthError";
        }
        String redirect = URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8);
        String url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId
                + "&redirect_uri=" + redirect + "&response_type=code&scope=profile%20email";
        return "redirect:" + url;
    }

    @GetMapping("/login/oauth2/code/google")
    public String googleCallback(@RequestParam(value = "code", required = false) String code, HttpSession session) {
        if (code == null) {
            return "redirect:/login?oauthError";
        }
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("code", code);
            form.add("client_id", googleClientId);
            form.add("client_secret", googleClientSecret);
            form.add("redirect_uri", googleRedirectUri);
            form.add("grant_type", "authorization_code");
            Map token = RestClient.create().post().uri("https://oauth2.googleapis.com/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(Map.class);
            String accessToken = String.valueOf(token.get("access_token"));
            Map info = RestClient.create().get().uri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).retrieve().body(Map.class);
            String googleId = String.valueOf(info.get("sub"));
            Object nameValue = info.get("name");
            String name = nameValue == null ? null : String.valueOf(nameValue);
            User user = userService.findOrCreateOAuthUser("google", googleId, name);
            session.setAttribute("loginUserId", user.getId());
            session.setAttribute("loginNickname", user.getNickname());
            return "redirect:/posts";
        } catch (Exception e) {
            return "redirect:/login?oauthError";
        }
    }
}
