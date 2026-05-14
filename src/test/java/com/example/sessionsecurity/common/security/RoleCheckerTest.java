package com.example.sessionsecurity.common.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.sessionsecurity.common.exception.BusinessException;
import com.example.sessionsecurity.security.SessionVo;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoleCheckerTest {

    @Test
    void allowsWhenCurrentSessionHasAnyRequiredRole() {
        CurrentSessionProvider provider = () -> new SessionVo("admin", "Admin User", List.of("ADMIN", "USER"));
        RoleChecker checker = new RoleChecker(provider);

        checker.requireAny("MANAGER", "ADMIN");
    }

    @Test
    void throwsWhenCurrentSessionDoesNotHaveRequiredRole() {
        CurrentSessionProvider provider = () -> new SessionVo("user", "Normal User", List.of("USER"));
        RoleChecker checker = new RoleChecker(provider);

        assertThatThrownBy(() -> checker.requireAny("ADMIN"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("permission");
    }
}
