package com.example.sessionsecurity.common.paging;

import java.util.Map;

public class MyBatisPagingSupport {

    private MyBatisPagingSupport() {
    }

    public static Map<String, Integer> parameters(PageRequestDto request) {
        return Map.of(
                "offset", request.offset(),
                "limit", request.limit()
        );
    }

    public static String oracleOffsetFetchClause() {
        return "OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY";
    }
}
