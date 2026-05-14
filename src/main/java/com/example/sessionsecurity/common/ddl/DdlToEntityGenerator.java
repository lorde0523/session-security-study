package com.example.sessionsecurity.common.ddl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DdlToEntityGenerator {

    private static final Pattern CREATE_TABLE_PATTERN =
            Pattern.compile("CREATE\\s+TABLE\\s+([A-Z0-9_]+)\\s*\\((.*)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public String generate(String packageName, String ddl) {
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(ddl.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Only simple CREATE TABLE ddl is supported.");
        }

        String tableName = matcher.group(1).trim().toUpperCase(Locale.ROOT);
        List<ColumnSpec> columns = parseColumns(matcher.group(2));
        String className = toPascalCase(tableName);

        StringBuilder source = new StringBuilder();
        source.append("package ").append(packageName).append(";\n\n")
                .append("import jakarta.persistence.Column;\n")
                .append("import jakarta.persistence.Entity;\n")
                .append("import jakarta.persistence.Id;\n")
                .append("import jakarta.persistence.Table;\n")
                .append("import java.math.BigDecimal;\n")
                .append("import java.time.LocalDateTime;\n\n")
                .append("@Entity\n")
                .append("@Table(name = \"").append(tableName).append("\")\n")
                .append("public class ").append(className).append(" {\n\n");

        for (ColumnSpec column : columns) {
            if (column.primaryKey()) {
                source.append("    @Id\n");
            }
            source.append("    @Column(name = \"").append(column.name()).append("\")\n")
                    .append("    private ").append(column.javaType()).append(" ")
                    .append(toCamelCase(column.name())).append(";\n\n");
        }

        source.append("}\n");
        return source.toString();
    }

    private List<ColumnSpec> parseColumns(String body) {
        List<ColumnSpec> columns = new ArrayList<>();
        for (String rawLine : body.split(",")) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.toUpperCase(Locale.ROOT).startsWith("CONSTRAINT")) {
                continue;
            }

            String[] parts = line.split("\\s+");
            if (parts.length < 2) {
                continue;
            }

            String name = parts[0].replace("\"", "").toUpperCase(Locale.ROOT);
            String dbType = parts[1].toUpperCase(Locale.ROOT);
            boolean primaryKey = line.toUpperCase(Locale.ROOT).contains("PRIMARY KEY");
            columns.add(new ColumnSpec(name, toJavaType(dbType), primaryKey));
        }
        return columns;
    }

    private String toJavaType(String dbType) {
        if (dbType.startsWith("VARCHAR") || dbType.startsWith("CHAR") || dbType.startsWith("CLOB")) {
            return "String";
        }
        if (dbType.startsWith("NUMBER(") && !dbType.contains(",")) {
            return "Long";
        }
        if (dbType.startsWith("NUMBER")) {
            return "BigDecimal";
        }
        if (dbType.startsWith("DATE") || dbType.startsWith("TIMESTAMP")) {
            return "LocalDateTime";
        }
        return "String";
    }

    private String toPascalCase(String value) {
        StringBuilder result = new StringBuilder();
        for (String part : value.toLowerCase(Locale.ROOT).split("_")) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return result.toString();
    }

    private String toCamelCase(String value) {
        String pascal = toPascalCase(value);
        return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
    }

    private record ColumnSpec(String name, String javaType, boolean primaryKey) {
    }
}
