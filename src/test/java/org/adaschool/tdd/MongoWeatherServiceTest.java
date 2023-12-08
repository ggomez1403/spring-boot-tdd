package org.adaschool.tdd;

import com.mongodb.BasicDBObject;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class MongoWeatherServiceTest
{
    WeatherService weatherService;

    @Mock
    WeatherReportRepository repository;

    @BeforeAll()
    public void setup()
    {
        weatherService = new MongoWeatherService( repository );
    }

    @Test
    void createWeatherReportCallsSaveOnRepository()
    {

        MongoWeatherService weatherService = new MongoWeatherService(repository);
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        weatherService.report( weatherReportDto );
        verify( repository ).save( any( WeatherReport.class ) );
    }

    @Test
    void weatherReportIdFoundTest()
    {
        MongoWeatherService weatherService = new MongoWeatherService(repository);

        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( weatherReport ) );
        WeatherReport foundWeatherReport = weatherService.findById( weatherReportId );
        Assertions.assertEquals( weatherReport, foundWeatherReport );
    }

    @Test
    void weatherReportIdNotFoundTest()
    {
        MongoWeatherService weatherService = new MongoWeatherService(repository);

        String weatherReportId = "dsawe1fasdasdoooq123";
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.empty() );
        Assertions.assertThrows( WeatherReportNotFoundException.class, () -> {
            weatherService.findById( weatherReportId );
        } );
    }

    @Test
    void findNearLocation_ValidGeoLocation_ReturnsExpectedResults() {
        MongoWeatherService weatherService = new MongoWeatherService(repository);

        GeoLocation geoLocation = new GeoLocation(4.7110, 74.0721);
        float distanceRangeInMeters = 10000;
        List<WeatherReport> expectedReports = new ArrayList<>();
        when(repository.findAllBy(any(BasicDBObject.class))).thenReturn(expectedReports);

        List<WeatherReport> actualReports = weatherService.findNearLocation(geoLocation, distanceRangeInMeters);

        Assertions.assertEquals(expectedReports, actualReports);
        verify(repository).findAllBy(any(BasicDBObject.class));
    }

    @Test
    void findNearLocation_NullGeoLocation_ThrowsIllegalArgumentException() {
        MongoWeatherService weatherService = new MongoWeatherService(repository);
        Assertions.assertThrows(IllegalArgumentException.class, () -> weatherService.findNearLocation(null, 10000));
    }
}
