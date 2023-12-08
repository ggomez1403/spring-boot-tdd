package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.WeatherReportController;
import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WeatherReportControllerTest {
    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherReportController weatherController;

    @Test
    public void createWeatherReport_validInput_returnsCreatedReport(){

        // Arrange
        final GeoLocation geoLocation = new GeoLocation(12.34, 56.78);
        final double temperature = 25.0;
        final double humidity = 70.0;
        final String reporter = "John Doe";
        final Date created = new Date();

        final WeatherReportDto weatherReportDto = new WeatherReportDto(geoLocation, temperature, humidity, reporter, created);
        final WeatherReport expectedReport = new WeatherReport(geoLocation, temperature, humidity, reporter, created);

        Mockito.when(weatherService.report(weatherReportDto)).thenReturn(expectedReport);

        // Act
        final WeatherReport actualReport = weatherController.create(weatherReportDto);

        // Assert
        Mockito.verify(weatherService).report(weatherReportDto);
        assertThat(actualReport).isEqualTo(expectedReport);
    }

    @Test
    public void findById_validId_returnsWeatherReport() {

        // Arrange
        final String id = "12345";
        final GeoLocation geoLocation = new GeoLocation(12.34, 56.78);
        final double temperature = 25.0;
        final double humidity = 70.0;
        final String reporter = "John Doe";
        final Date created = new Date();
        final WeatherReport expectedReport = new WeatherReport(geoLocation, temperature, humidity, reporter, created);

        Mockito.when(weatherService.findById(id)).thenReturn(expectedReport);

        // Act
        final WeatherReport actualReport = weatherController.findById(id);

        // Assert
        Mockito.verify(weatherService).findById(id);
        assertThat(actualReport).isEqualTo(expectedReport);
    }

    @Test
    public void findNearByReports_validQuery_returnsWeatherReports(){

        // Arrange
        final GeoLocation geoLocation = new GeoLocation(12.34, 56.78);
        final float distanceRangeInMeters = 1000.0f;
        final List<WeatherReport> expectedReports = new ArrayList<>();

        Mockito.when(weatherService.findNearLocation(geoLocation, distanceRangeInMeters)).thenReturn(expectedReports);

        final NearByWeatherReportsQueryDto query = new NearByWeatherReportsQueryDto(geoLocation, distanceRangeInMeters);

        // Act
        final List<WeatherReport> actualReports = weatherController.findNearByReports(query);

        // Assert
        Mockito.verify(weatherService).findNearLocation(geoLocation, distanceRangeInMeters);
        assertThat(actualReports).isEqualTo(expectedReports);
    }

    @Test
    public void findByReporterId_validName_returnsWeatherReports(){

        // Arrange
        final String reporterName = "John Doe";
        final List<WeatherReport> expectedReports = new ArrayList<>();

        Mockito.when(weatherService.findWeatherReportsByName(reporterName)).thenReturn(expectedReports);

        // Act
        final List<WeatherReport> actualReports = weatherController.findByReporterId(reporterName);

        // Assert
        Mockito.verify(weatherService).findWeatherReportsByName(reporterName);
        assertThat(actualReports).isEqualTo(expectedReports);
    }

}

