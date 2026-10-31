package com.mechita.mechita_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class ConexionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public Map<String, Object> db() {
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
        String db = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        String user = jdbcTemplate.queryForObject("SELECT current_user", String.class);

        return Map.of(
                "status", "OK",
                "database", db,
                "user", user,
                "version", version
        );
    }

    @GetMapping("/tablas")
    public List<String> tablas() {
        return jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' ORDER BY table_name",
                String.class
        );
    }
}