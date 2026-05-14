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
}
