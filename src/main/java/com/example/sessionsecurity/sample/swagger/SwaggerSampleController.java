package com.example.sessionsecurity.sample.swagger;

import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/swagger")
public class SwaggerSampleController {

    @Operation(
            summary = "Swagger API 명세 작성 샘플",
            description = "컨트롤러 메서드에 @Operation, @Parameter, @ApiResponses를 사용하는 예시입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/documented")
    public ApiResponse<Map<String, String>> documented(
            @Parameter(description = "검색어", example = "spring")
            @RequestParam(defaultValue = "spring") String keyword,
            @Parameter(description = "화면 meta uuid", example = "swagger-test-uuid")
            @RequestHeader("X-UUID") String uuid,
            @Parameter(description = "화면 meta client", example = "swagger")
            @RequestHeader("X-CLIENT") String client
    ) {
        return ApiResponse.ok(Map.of(
                "keyword", keyword,
                "uuid", uuid,
                "client", client
        ));
    }
}
