package com.example.sessionsecurity.common.multiDb2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

class MultiDb2DataSourceConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MultiDb2DataSourceConfig.class)
            .withPropertyValues(
                    "app.datasource.enum-routing-enabled=true",
                    "app.datasource.driver-class-name=org.h2.Driver",
                    "app.datasource.url=jdbc:h2:mem:multiDb2;MODE=Oracle;DB_CLOSE_DELAY=-1",
                    "app.datasource.default-key=USER_A",
                    "app.datasource.users.USER_A.username=sa",
                    "app.datasource.users.USER_A.password=",
                    "app.datasource.users.USER_B.username=user_b",
                    "app.datasource.users.USER_B.password=secret"
            );

    @Test
    void createsRoutingDataSourceFromEnumNamedUsers() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DataSource.class);

            DataSource dataSource = context.getBean(DataSource.class);
            assertThat(dataSource).isInstanceOf(MultiDb2RoutingDataSource.class);

            @SuppressWarnings("unchecked")
            Map<Object, Object> resolvedDataSources =
                    (Map<Object, Object>) ReflectionTestUtils.getField(dataSource, "resolvedDataSources");
            Object resolvedDefaultDataSource =
                    ReflectionTestUtils.getField(dataSource, "resolvedDefaultDataSource");

            assertThat(resolvedDataSources)
                    .containsKeys(DataSourceKey.USER_A, DataSourceKey.USER_B);
            assertThat(resolvedDefaultDataSource)
                    .isSameAs(resolvedDataSources.get(DataSourceKey.USER_A));
        });
    }

    @Test
    void failsFastWhenUserKeyDoesNotMatchEnum() {
        new ApplicationContextRunner()
                .withUserConfiguration(MultiDb2DataSourceConfig.class)
                .withPropertyValues(
                        "app.datasource.enum-routing-enabled=true",
                        "app.datasource.driver-class-name=org.h2.Driver",
                        "app.datasource.url=jdbc:h2:mem:multiDb2Invalid;MODE=Oracle;DB_CLOSE_DELAY=-1",
                        "app.datasource.default-key=USER_A",
                        "app.datasource.users.UNKNOWN.username=sa",
                        "app.datasource.users.UNKNOWN.password="
                )
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("Invalid DataSourceKey: UNKNOWN");
                });
    }
}
