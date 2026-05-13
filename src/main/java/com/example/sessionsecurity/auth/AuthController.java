package com.example.sessionsecurity.auth;

import com.example.sessionsecurity.security.SessionVo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login-sample")
    public SessionVo loginSample(@Valid @RequestBody LoginSampleRequest request, HttpSession httpSession) {
        SessionVo sessionVo = new SessionVo(
                request.userId(),
                request.userName(),
                rolesFor(request.userId())
        );
        httpSession.setAttribute(SessionVo.SESSION_ATTRIBUTE_NAME, sessionVo);
        return sessionVo;
    }

    @PostMapping("/logout")
    public void logout(HttpSession httpSession) {
        httpSession.invalidate();
    }

    private List<String> rolesFor(String userId) {
        if ("admin".equalsIgnoreCase(userId)) {
            return List.of("ADMIN", "USER");
        }
        if ("manager".equalsIgnoreCase(userId)) {
            return List.of("MANAGER", "USER");
        }
        return List.of("USER");
    }
}
