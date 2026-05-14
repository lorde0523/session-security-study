package com.example.sessionsecurity.sample.async;

import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/async")
public class AsyncSampleController {

    private final AsyncSampleService asyncSampleService;

    public AsyncSampleController(AsyncSampleService asyncSampleService) {
        this.asyncSampleService = asyncSampleService;
    }

    @Operation(summary = "@Async 공통 executor 샘플")
    @GetMapping("/message")
    public ApiResponse<Map<String, String>> asyncMessage(@RequestParam(defaultValue = "hello") String message) {
        asyncSampleService.runAsyncMessage(message);
        return ApiResponse.ok(Map.of("message", "Async task submitted."));
    }
}
