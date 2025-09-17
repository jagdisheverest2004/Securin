package org.example.weatherdata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {
    @Id
    private LocalDateTime datetime_utc;
    private String _conds;
    private Double _dewptm;
    private Integer _fog;
    private Integer _hail;
    private Double _heatindexm;
    private Double _hum;
    private Double _precipm;
    private Double _pressurem;
    private Integer _rain;
    private Integer _snow;
    private Double _tempm;
    private Integer _thunder;
    private Integer _tornado;
    private Double _vism;
    private Double _wdird;
    private String _wdire;
    private Double _wgustm;
    private Double _windchillm;
    private Double _wspdm;
}