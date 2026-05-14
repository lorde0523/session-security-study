package com.example.sessionsecurity.common.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageRequestDto(
        int page,
        int size
) {

    public PageRequestDto {
        page = Math.max(page, 1);
        size = size <= 0 ? 20 : Math.min(size, 200);
    }

    public int offset() {
        return (page - 1) * size;
    }

    public int limit() {
        return size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }
}
