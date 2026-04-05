package com.example.lab05.repository.cassandra;

import java.time.Instant;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.cassandra.SensorReadingKey;

// TODO (Section 4 — Cassandra):
// 1. Make this interface extend CassandraRepository<SensorReading, SensorReadingKey>
// 2. Add query methods:
//
//    // Pattern 1: Derived query (auto-generated)
//    List<SensorReading> findByKeySensorId(String sensorId);
//
//    // Pattern 2: @Query with CQL
//    @Query("SELECT * FROM sensor_readings " +
//           "WHERE sensor_id = ?0 " +
//           "AND reading_time >= ?1 AND reading_time <= ?2")
//    List<SensorReading> findReadingsInRange(
//        String sensorId, Instant from, Instant to);
//
// Pattern 3 (CassandraTemplate) lives in CassandraQueryRepository.

public interface SensorReadingRepository extends CassandraRepository<SensorReading, SensorReadingKey> {
    // Replace this with the CassandraRepository extension and query methods
    List<SensorReading> findByKeySensorId(String sensorId);

    @Query("SELECT * FROM sensor_readings " +
           "WHERE sensor_id = ?0 " +
           "AND reading_time >= ?1 AND reading_time <= ?2")
    List<SensorReading> findReadingsInRange(
        String sensorId, Instant from, Instant to);
}
