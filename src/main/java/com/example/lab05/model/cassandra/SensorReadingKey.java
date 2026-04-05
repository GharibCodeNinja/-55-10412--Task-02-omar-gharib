package com.example.lab05.model.cassandra;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

// TODO (Section 4 — Cassandra):
// Add the following annotations as described in the manual:
//
// @PrimaryKeyClass
// public class SensorReadingKey implements Serializable {
//
//     @PrimaryKeyColumn(name = "sensor_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//     private String sensorId;
//
//     @PrimaryKeyColumn(name = "reading_time", ordinal = 1,
//                       type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
//     private Instant readingTime;
// }

@PrimaryKeyClass
public class SensorReadingKey implements Serializable {

    @PrimaryKeyColumn(name = "sensor_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String sensorId;

    @PrimaryKeyColumn(name = "reading_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant readingTime;

    public SensorReadingKey() {}

    public SensorReadingKey(String sensorId, Instant readingTime) {
        this.sensorId = sensorId;
        this.readingTime = readingTime;
    }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public Instant getReadingTime() { return readingTime; }
    public void setReadingTime(Instant readingTime) { this.readingTime = readingTime; }
}
