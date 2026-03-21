package com.example.lab05.model.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

// TODO (Section 4 — Cassandra):
// Add the following annotations as described in the manual:
//
// @Table("sensor_readings")
// public class SensorReading {
//
//     @PrimaryKey
//     private SensorReadingKey key;
//
//     private Double temperature;
//     private Double humidity;
//     private String location;
// }

@Table("sensor_readings")
public class SensorReading {

    @PrimaryKey
    private SensorReadingKey key;
    private Double temperature;
    private Double humidity;
    private String location;

    public SensorReading() {}

    public SensorReadingKey getKey() { return key; }
    public void setKey(SensorReadingKey key) { this.key = key; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
