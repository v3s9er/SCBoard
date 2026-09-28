// 일반 로그인, 구글 로그인
/*
불충분한 이용자 인증 (OAuth Login CSRF)[WEB-SER-019] 보안조치

(103~118행) 
[취약] 
구글 인증 URL을 조립할 때 일회성 난수인 state 파라미터를 생성 및 저장하지 않음.

[수정]
1. 로그인 시작 시, 일회성 난수(state)를 뽑아 세션에 저장
2. 주소 조립

(120~151행)
[취약]
콜백 엔드포인트에서 state 파라미터 유효성 검증 없이 전달된 code 만으로 즉시 사용자 인증 및 세션 발급.

[수정]
1. 구글이 돌려준 state를 받아옴
2. 세션에 적어둔 state와 구글의 state가 같은지 확인
3. 성공 시 session을 교체.
*/
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

    @GetMapping("/oauth2/google")
    public String googleLogin(HttpSession session) {
	    // [보안 패치] CSRF 방어용 일회성 난수 state 생성 후 세션에 저장
	    String state = UUID.randomUUID().toString();
	    session.setAttribute("OAUTH2_STATE", state);

	    //Google 인증 요청 URL에 state 파라미터 추가 조립
	    String googleAuthUrl = "<https://accounts.google.com/o/oauth2/v2/auth>"
		    + "?client_id=" + googleClientId
		    + "&redirect_uri=" + googleRedirectUri
		    + "&response_type=code"
		    + "&scope=email%20profile"
		    + "&state=" + state; // 90행: state 파라미터 전달 추가

	    return "redirect:" + googleAuthUrl;
    }

    // 2. Google OAuth 콜백 처리 메서드 (94~121행 대체)
    @GetMapping("/login/oauth2/code/google")
    public String googleCallback(
		    @RequestParam("code") String code,
		    @RequestParam(value = "state", required = false) String state,
		    HttpServletRequest request) {

	    HttpSession session = request.getSession(false);

	    // [보안 패치] 세션 존재 여부 및 State 토큰 유효성 검증
	    if (session == null || state == null) {
		    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증 세션입니다.");
	    }

	    String sessionState = (String) session.getAttribute("OAUTH2_STATE");
	    session.removeAttribute("OAUTH2_STATE"); // 재사용 방지를 위해 즉시 제거

	    if (sessionState == null || !sessionState.equals(state)) {
		    // State가 불일치하거나 타인의 링크를 누른 경우 공격 시도로 간주
		    return "redirect:/login?oauthError";
	    }

	    // Google Token 교환 및 사용자 정보 조회 로직 수행
	    User user = oAuthService.processGoogleLogin(code);

	    // [보안 패치] 세션 고정 보호(Session Fixation Attack 방지): 로그인 성공 시 새로운 세션 ID 발급
	    session.invalidate(); // 기존 세션 무효화
	    HttpSession newSession = request.getSession(true);
	    newSession.setAttribute("loginUser", user);

	    return "redirect:/posts";
		    }
}
