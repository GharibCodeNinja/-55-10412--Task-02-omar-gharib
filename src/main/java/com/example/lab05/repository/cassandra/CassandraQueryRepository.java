package com.example.lab05.repository.cassandra;

import java.util.List;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.lab05.model.cassandra.SensorReading;

// TODO (Section 4 — Cassandra):
//
// Pattern 3: CassandraTemplate — programmatic queries.
// Use this when query parameters (like limit) are dynamic at runtime.
//
// 1. Inject CassandraTemplate via constructor injection
// 2. Implement findLatestReadings(String sensorId, int limit):
//
//    Query query = Query.query(Criteria.where("sensor_id").is(sensorId))
//            .limit(limit);
//    return cassandraTemplate.select(query, SensorReading.class);

@Repository
public class CassandraQueryRepository {

    // Inject CassandraTemplate here
    private final CassandraTemplate cassandraTemplate;

    public CassandraQueryRepository(CassandraTemplate cassandraTemplate) {
        this.cassandraTemplate = cassandraTemplate;
    }

    public List<SensorReading> findLatestReadings(String sensorId, int limit) {
        Query query = Query.query(Criteria.where("sensor_id").is(sensorId))
                .limit(limit);
        List<SensorReading> results = cassandraTemplate.select(query, SensorReading.class);
        return results;
    }
}
