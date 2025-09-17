package org.example.weatherdata.repository;

import org.example.weatherdata.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, LocalDateTime> {

    @Query("SELECT d FROM WeatherData d WHERE d.datetime_utc BETWEEN ?1 AND ?2")
    List<WeatherData> findByDatetime_utcBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT d FROM WeatherData d WHERE YEAR(d.datetime_utc) = :year AND MONTH(d.datetime_utc) = :month")
    List<WeatherData> findByDatetime_utcYearAndDatetime_utcMonth(@Param("year") int year, @Param("month") int month);
}