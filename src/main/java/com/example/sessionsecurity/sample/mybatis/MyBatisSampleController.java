package com.example.sessionsecurity.sample.mybatis;

import com.example.sessionsecurity.common.datasource.DbUserContext;
import com.example.sessionsecurity.common.datasource.UseDbUser;
import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/mybatis")
public class MyBatisSampleController {

    @Operation(summary = "MyBatis 동적 DB 사용자 선택 샘플")
    @UseDbUser("report")
    @GetMapping("/routing-user")
    public ApiResponse<Map<String, String>> routingUser() {
        return ApiResponse.ok(Map.of(
                "selectedDbUser", String.valueOf(DbUserContext.get()),
                "usage", "Put @UseDbUser on a service method that calls MyBatis mapper."
        ));
    }

    @Operation(summary = "URL 규칙 기반 DB 사용자 선택 샘플")
    @GetMapping("/routing-user/by-url")
    public ApiResponse<Map<String, String>> routingUserByUrl() {
        return ApiResponse.ok(Map.of(
                "selectedDbUser", String.valueOf(DbUserContext.get()),
                "usage", "Configure app.datasource.url-user-mappings to route by request URI."
        ));
    }

    @Operation(summary = "URL 규칙 + @UseDbUser 우선순위 샘플")
    @UseDbUser("report")
    @GetMapping("/routing-user/by-url-with-annotation")
    public ApiResponse<Map<String, String>> routingUserByUrlWithAnnotation() {
        return ApiResponse.ok(Map.of(
                "selectedDbUser", String.valueOf(DbUserContext.get()),
                "usage", "@UseDbUser has higher priority within the annotated method scope."
        ));
    }

    @Operation(summary = "URL 규칙 미매핑 시 기본 사용자 폴백 샘플")
    @GetMapping("/routing-user/default")
    public ApiResponse<Map<String, String>> routingUserDefault() {
        return ApiResponse.ok(Map.of(
                "selectedDbUser", String.valueOf(DbUserContext.get()),
                "usage", "When no URL rule matches, default datasource user is applied."
        ));
    }
}
