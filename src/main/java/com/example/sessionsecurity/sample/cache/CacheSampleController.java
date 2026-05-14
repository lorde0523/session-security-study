package com.example.sessionsecurity.sample.cache;

import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/cache")
public class CacheSampleController {

    private final CacheSampleService cacheSampleService;

    public CacheSampleController(CacheSampleService cacheSampleService) {
        this.cacheSampleService = cacheSampleService;
    }

    @Operation(summary = "Spring 기본 cache 샘플")
    @GetMapping("/simple/{id}")
    public ApiResponse<Map<String, String>> simple(@PathVariable String id) {
        return ApiResponse.ok(Map.of("value", cacheSampleService.getSimpleCachedValue(id)));
    }

    @Operation(summary = "Caffeine cache 샘플")
    @GetMapping("/caffeine/{id}")
    public ApiResponse<Map<String, String>> caffeine(@PathVariable String id) {
        return ApiResponse.ok(Map.of("value", cacheSampleService.getCaffeineCachedValue(id)));
    }

    @Operation(summary = "샘플 cache 전체 삭제")
    @DeleteMapping
    public ApiResponse<Void> clear() {
        cacheSampleService.clear();
        return ApiResponse.ok();
    }
}
