package com.example.sessionsecurity.sample.jpa;

import com.example.sessionsecurity.common.ddl.DdlToEntityGenerator;
import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/jpa")
public class DdlEntityGeneratorSampleController {

    private final DdlToEntityGenerator generator = new DdlToEntityGenerator();

    @Operation(summary = "Oracle DDL을 JPA Entity 소스 문자열로 변환")
    @PostMapping("/entity-source")
    public ApiResponse<Map<String, String>> generateEntitySource(
            @RequestParam(defaultValue = "com.example.domain") String packageName,
            @RequestBody String ddl
    ) {
        return ApiResponse.ok(Map.of("source", generator.generate(packageName, ddl)));
    }
}
