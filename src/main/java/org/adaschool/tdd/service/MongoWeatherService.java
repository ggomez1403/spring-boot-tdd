package org.adaschool.tdd.service;

import com.mongodb.BasicDBObject;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

@Service
public class MongoWeatherService
    implements WeatherService
{

    private final WeatherReportRepository repository;

    public MongoWeatherService( @Autowired WeatherReportRepository repository )
    {
        this.repository = repository;
    }

    @Override
    public WeatherReport report( WeatherReportDto weatherReportDto )
    {
        GeoLocation location = new GeoLocation(weatherReportDto.getGeoLocation().getLat(), weatherReportDto.getGeoLocation().getLng());
        WeatherReport weatherReport = new WeatherReport(location, weatherReportDto.getTemperature(),
                weatherReportDto.getHumidity(), weatherReportDto.getReporter(), new Date());
        return repository.save(weatherReport);
    }

    @Override
    public WeatherReport findById( String id )
    {
        Optional<WeatherReport> optionalWeatherReport = repository.findById(id);
        if (optionalWeatherReport.isPresent()) {
            return optionalWeatherReport.get();
        } else {
            throw new WeatherReportNotFoundException();
        }
    }

    @Override
    public List<WeatherReport> findNearLocation(GeoLocation geoLocation, float distanceRangeInMeters) {
        if (geoLocation == null) {
            throw new IllegalArgumentException("GeoLocation cannot be null");
        }

        double latitude = geoLocation.getLat();
        double longitude = geoLocation.getLng();

        GeoJsonPoint point = new GeoJsonPoint(latitude, longitude);
        BasicDBObject filter = new BasicDBObject("$geoWithin", new BasicDBObject("$centerSphere", Arrays.asList(point, distanceRangeInMeters / 6371000)));

        return repository.findAllBy(filter);
    }

    @Override
    public List<WeatherReport> findWeatherReportsByName(String reporter) {
        if (reporter == null || reporter.isEmpty()) {
            throw new IllegalArgumentException("Reporter name cannot be null or empty");
        }

        BasicDBObject query = new BasicDBObject();
        query.put("reporter", reporter);

        return repository.findAllBy(query);
    }
}
