package com.example.sessionsecurity.sample.exception;

import com.example.sessionsecurity.common.exception.BusinessException;
import com.example.sessionsecurity.common.exception.ErrorCode;
import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/exception")
public class ExceptionSampleController {

    @Operation(summary = "공통 예외 응답 샘플")
    @GetMapping("/business")
    public ApiResponse<Void> businessError() {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "This is a sample business exception.");
    }
}
