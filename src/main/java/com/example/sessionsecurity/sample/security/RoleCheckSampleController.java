package com.example.sessionsecurity.sample.security;

import com.example.sessionsecurity.common.response.ApiResponse;
import com.example.sessionsecurity.common.security.CurrentSessionProvider;
import com.example.sessionsecurity.common.security.RequiredRoles;
import com.example.sessionsecurity.common.security.RoleChecker;
import com.example.sessionsecurity.security.SessionVo;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/roles")
public class RoleCheckSampleController {

    private final CurrentSessionProvider currentSessionProvider;
    private final RoleChecker roleChecker;

    public RoleCheckSampleController(CurrentSessionProvider currentSessionProvider, RoleChecker roleChecker) {
        this.currentSessionProvider = currentSessionProvider;
        this.roleChecker = roleChecker;
    }

    @Operation(summary = "현재 SessionVo 조회 샘플")
    @GetMapping("/me")
    public ApiResponse<SessionVo> me() {
        return ApiResponse.ok(currentSessionProvider.current());
    }

    @Operation(summary = "RoleChecker 직접 호출 샘플")
    @GetMapping("/admin-direct")
    public ApiResponse<Map<String, String>> adminDirect() {
        roleChecker.requireAny("ADMIN");
        return ApiResponse.ok(Map.of("message", "ADMIN role accepted by RoleChecker."));
    }

    @Operation(summary = "@RequiredRoles 어노테이션 샘플")
    @RequiredRoles("ADMIN")
    @GetMapping("/admin-annotation")
    public ApiResponse<Map<String, String>> adminAnnotation() {
        return ApiResponse.ok(Map.of("message", "ADMIN role accepted by annotation."));
    }

    @Operation(summary = "MANAGER 또는 ADMIN 권한 샘플")
    @RequiredRoles({"MANAGER", "ADMIN"})
    @GetMapping("/manager-or-admin")
    public ApiResponse<Map<String, String>> managerOrAdmin() {
        return ApiResponse.ok(Map.of("message", "MANAGER or ADMIN role accepted."));
    }
}
