package com.example.lab05.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.cassandra.SensorReadingKey;
import com.example.lab05.repository.cassandra.CassandraQueryRepository;
import com.example.lab05.repository.cassandra.SensorReadingRepository;

@Service
public class SensorService {

    // TODO: Inject SensorReadingRepository via constructor injection
    // TODO: Inject CassandraQueryRepository via constructor injection
    private final SensorReadingRepository sensorReadingRepository;
    private final CassandraQueryRepository cassandraQueryRepository;
    public SensorService(SensorReadingRepository sensorReadingRepository,
                         CassandraQueryRepository cassandraQueryRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.cassandraQueryRepository = cassandraQueryRepository;
    }

    // TODO: Implement recordReading(SensorReading reading)
    //   - Set the key with a new SensorReadingKey(sensorId, Instant.now())
    //   - Save and return the reading (delegate to SensorReadingRepository)
    public SensorReading recordReading(SensorReading reading) {
        SensorReadingKey key = new SensorReadingKey(
            reading.getKey().getSensorId(),
            Instant.now()
        );
        reading.setKey(key);
        return sensorReadingRepository.save(reading);
    }

    // TODO: Implement getReadingsBySensorId(String sensorId)
    //   - Delegate to SensorReadingRepository (Pattern 1: derived query)
    public List<SensorReading> getReadingsBySensorId(String sensorId) {
        return sensorReadingRepository.findByKeySensorId(sensorId);
    }

    // TODO: Implement getReadingsInRange(String sensorId, Instant from, Instant to)
    //   - Delegate to SensorReadingRepository (Pattern 2: @Query CQL)
    public List<SensorReading> getReadingsInRange(String sensorId, Instant from, Instant to) {
        return sensorReadingRepository.findReadingsInRange(sensorId, from, to);
    }

    // TODO: Implement getLatestReadings(String sensorId, int limit)
    //   - Delegate to CassandraQueryRepository (Pattern 3: CassandraTemplate)
    public List<SensorReading> getLatestReadings(String sensorId, int limit) {
        return cassandraQueryRepository.findLatestReadings(sensorId, limit);
    }
}
