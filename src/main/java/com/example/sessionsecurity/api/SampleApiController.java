package com.example.sessionsecurity.api;

import com.example.sessionsecurity.security.SessionVo;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SampleApiController {

    @GetMapping("/common/me")
    public Map<String, Object> me(@AuthenticationPrincipal SessionVo sessionVo) {
        return Map.of(
                "message", "current session",
                "session", sessionVo
        );
    }

    @GetMapping("/user/profile")
    public Map<String, Object> userProfile(@AuthenticationPrincipal SessionVo sessionVo) {
        return Map.of(
                "message", "user profile",
                "session", sessionVo
        );
    }

    @GetMapping("/manager/report")
    public Map<String, Object> managerReport(@AuthenticationPrincipal SessionVo sessionVo) {
        return Map.of(
                "message", "manager report",
                "session", sessionVo
        );
    }

    @GetMapping("/admin/dashboard")
    public Map<String, Object> adminDashboard(@AuthenticationPrincipal SessionVo sessionVo) {
        return Map.of(
                "message", "admin dashboard",
                "session", sessionVo
        );
    }
}
