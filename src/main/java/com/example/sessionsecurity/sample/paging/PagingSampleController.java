package com.example.sessionsecurity.sample.paging;

import com.example.sessionsecurity.common.paging.MyBatisPagingSupport;
import com.example.sessionsecurity.common.paging.PageRequestDto;
import com.example.sessionsecurity.common.paging.PageResponseDto;
import com.example.sessionsecurity.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/paging")
public class PagingSampleController {

    @Operation(summary = "JPA Pageable 변환 샘플")
    @GetMapping("/jpa")
    public ApiResponse<Map<String, Object>> jpa(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        PageRequestDto request = new PageRequestDto(page, size);
        return ApiResponse.ok(Map.of(
                "pageNumberForJpa", request.toPageable().getPageNumber(),
                "pageSize", request.toPageable().getPageSize()
        ));
    }

    @Operation(summary = "MyBatis offset/limit 샘플")
    @GetMapping("/mybatis")
    public ApiResponse<Map<String, Object>> mybatis(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        PageRequestDto request = new PageRequestDto(page, size);
        return ApiResponse.ok(Map.of(
                "parameters", MyBatisPagingSupport.parameters(request),
                "oracleClause", MyBatisPagingSupport.oracleOffsetFetchClause()
        ));
    }

    @Operation(summary = "공통 PageResponse 샘플")
    @GetMapping("/response")
    public ApiResponse<PageResponseDto<String>> response(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        PageRequestDto request = new PageRequestDto(page, size);
        return ApiResponse.ok(PageResponseDto.of(List.of("sample-a", "sample-b"), request, 42));
    }
}
