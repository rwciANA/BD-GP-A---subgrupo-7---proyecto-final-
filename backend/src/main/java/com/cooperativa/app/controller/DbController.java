package com.cooperativa.app.controller;

import com.cooperativa.app.service.DbService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
@CrossOrigin(origins = "*")
public class DbController {
    private final DbService dbService;

    public DbController(DbService dbService) {
        this.dbService = dbService;
    }

    @GetMapping("/tables")
    public List<Map<String, Object>> listarTablas() {
        return dbService.listarTablas();
    }

    @GetMapping("/table/{tableName}")
    public List<Map<String, Object>> consultarTabla(@PathVariable String tableName) {
        return dbService.consultarTabla(tableName);
    }
}
