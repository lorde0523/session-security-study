package com.example.sessionsecurity.common.paging;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponseDto<T>(
        List<T> contents,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public static <T> PageResponseDto<T> of(List<T> contents, PageRequestDto request, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / request.size());
        return new PageResponseDto<>(contents, request.page(), request.size(), totalElements, totalPages);
    }
}
