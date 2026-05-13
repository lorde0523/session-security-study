package com.example.sessionsecurity.security;

import java.io.Serializable;
import java.util.List;

public record SessionVo(
        String userId,
        String userName,
        List<String> roles
) implements Serializable {

    public static final String SESSION_ATTRIBUTE_NAME = "SESSION_VO";

    public SessionVo {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
