package com.example.sessionsecurity.common.ddl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DdlToEntityGeneratorTest {

    @Test
    void convertsOracleCreateTableDdlToJpaEntitySource() {
        String ddl = """
                CREATE TABLE TB_MEMBER (
                    MEMBER_ID NUMBER(19) PRIMARY KEY,
                    MEMBER_NAME VARCHAR2(100) NOT NULL,
                    USE_YN CHAR(1),
                    CREATED_AT DATE
                )
                """;

        String source = new DdlToEntityGenerator().generate("com.example.domain", ddl);

        assertThat(source).contains("package com.example.domain;");
        assertThat(source).contains("@Entity");
        assertThat(source).contains("@Table(name = \"TB_MEMBER\")");
        assertThat(source).contains("public class TbMember");
        assertThat(source).contains("@Id");
        assertThat(source).contains("private Long memberId;");
        assertThat(source).contains("private String memberName;");
        assertThat(source).contains("private String useYn;");
        assertThat(source).contains("private LocalDateTime createdAt;");
    }
}
