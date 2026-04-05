package com.example.lab05.controller;

// ═══════════════════════════════════════════════════════════════
// TODO (Section 4 — Cassandra):
// Uncomment this entire controller after implementing
// SensorService, SensorReadingRepository, and CassandraQueryRepository.
// ═══════════════════════════════════════════════════════════════

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.service.SensorService;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/readings")
    public SensorReading record(@RequestBody SensorReading reading) {
        return sensorService.recordReading(reading);
    }

    @GetMapping("/{sensorId}/readings")
    public List<SensorReading> getBySensor(@PathVariable String sensorId) {
        return sensorService.getReadingsBySensorId(sensorId);
    }

    @GetMapping("/{sensorId}/readings/range")
    public List<SensorReading> getRange(
            @PathVariable String sensorId,
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return sensorService.getReadingsInRange(sensorId, from, to);
    }

    @GetMapping("/{sensorId}/readings/latest")
    public List<SensorReading> getLatest(
            @PathVariable String sensorId,
            @RequestParam(defaultValue = "10") int limit) {
        return sensorService.getLatestReadings(sensorId, limit);
    }
}
