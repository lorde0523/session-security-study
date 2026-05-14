package com.example.sessionsecurity.common.paging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class PagingSupportTest {

    @Test
    void calculatesOffsetForMyBatisAndConvertsToJpaPageable() {
        PageRequestDto request = new PageRequestDto(3, 20);

        assertThat(request.offset()).isEqualTo(40);
        assertThat(request.limit()).isEqualTo(20);

        Pageable pageable = request.toPageable();

        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(20);
    }
}
