// 회원가입, 세션 로그인 처리
package com.scboard.user;

import com.scboard.user.dto.SignupForm;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean signup(SignupForm form) {
        String username = form.getUsername().trim();
        String nickname = form.getNickname().trim();
        if (userRepository.existsByUsername(username)) {
            return false;
        }
        User user = new User(username, form.getPassword(), nickname, Role.USER, null, null);
        userRepository.save(user);
        return true;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    public User getLoginUser(HttpSession session) {
        Long id = (Long) session.getAttribute("loginUserId");
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User findOrCreateOAuthUser(String provider, String providerId, String nickname) {
        if (userRepository.findByOauthProviderAndOauthProviderId(provider, providerId).isPresent()) {
            return userRepository.findByOauthProviderAndOauthProviderId(provider, providerId).get();
        }
        String username = provider + "_" + providerId;
        String name = nickname;
        if (name == null) {
            name = provider + " user";
        }
        User user = new User(username, UUID.randomUUID().toString(), name,
                Role.USER, provider, providerId);
        return userRepository.save(user);
    }
}
