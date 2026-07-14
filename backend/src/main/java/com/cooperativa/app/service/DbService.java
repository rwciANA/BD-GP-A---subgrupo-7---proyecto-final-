package com.cooperativa.app.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DbService {
    private final JdbcTemplate jdbcTemplate;

    public DbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> listarTablas() {
        String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                ORDER BY table_name
                """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> consultarTabla(String tableName) {
        String sql = "SELECT * FROM " + tableName + " LIMIT 100";
        return jdbcTemplate.queryForList(sql);
    }
}
