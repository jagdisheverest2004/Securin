package org.example.weatherdata.service;

import org.example.weatherdata.model.WeatherData;
import org.example.weatherdata.repository.WeatherDataRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherDataService {

    @Autowired
    private WeatherDataRepository repository;


    @PostConstruct
    public void loadData() {
        String csvFilePath = "src/main/resources/testset.csv";
        String xlsxFilePath = "src/main/resources/testset.xlsx";

        // Load from CSV if it exists, otherwise try XLSX
        if (new File(csvFilePath).exists()) {
            try {
                readDataFromCsv(csvFilePath);
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        } else if (new File(xlsxFilePath).exists()) {
            try {
                readDataFromXlsx(xlsxFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("No data file found at specified paths. Skipping data load.");
        }
    }

    private void readDataFromCsv(String filePath) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            // Assuming the first row is a header
            if (!records.isEmpty()) {
                records.remove(0);
            }
            records.forEach(record -> {
                WeatherData data = mapToWeatherData(record);
                if (data != null) {
                    repository.save(data);
                }
            });
        }
    }

    private void readDataFromXlsx(String filePath) throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            int startRow = 1; // Assuming header is in the first row
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String[] record = new String[row.getLastCellNum()];
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    record[j] = dataFormatter.formatCellValue(cell);
                }
                WeatherData data = mapToWeatherData(record);
                if (data != null) {
                    repository.save(data);
                }
            }
        }
    }

    private WeatherData mapToWeatherData(String[] record) {
        if (record.length < 20 || record[0] == null || record[0].isEmpty() || record[11] == null || record[11].isEmpty()) {
            return null;
        }

        try {
            WeatherData data = new WeatherData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
            data.setDatetime_utc(LocalDateTime.parse(record[0], formatter));
            data.set_conds(record[1]);
            data.set_dewptm(parseSafeDouble(record[2]));
            data.set_fog(parseSafeInteger(record[3]));
            data.set_hail(parseSafeInteger(record[4]));
            data.set_heatindexm(parseSafeDouble(record[5]));
            data.set_hum(parseSafeDouble(record[6]));
            data.set_precipm(parseSafeDouble(record[7]));
            data.set_pressurem(parseSafeDouble(record[8]));
            data.set_rain(parseSafeInteger(record[9]));
            data.set_snow(parseSafeInteger(record[10]));
            data.set_tempm(parseSafeDouble(record[11]));
            data.set_thunder(parseSafeInteger(record[12]));
            data.set_tornado(parseSafeInteger(record[13]));
            data.set_vism(parseSafeDouble(record[14]));
            data.set_wdird(parseSafeDouble(record[15]));
            data.set_wdire(record[16]);
            data.set_wgustm(parseSafeDouble(record[17]));
            data.set_windchillm(parseSafeDouble(record[18]));
            data.set_wspdm(parseSafeDouble(record[19]));
            return data;
        } catch (Exception e) {
            System.err.println("Error mapping record to WeatherData: " + String.join(",", record));
            return null;
        }
    }

    private Double parseSafeDouble(String s) {
        if (s == null || s.trim().isEmpty() || s.equals("N/A") || s.equals("-9999")) {
            return null;
        }
        return Double.valueOf(s);
    }

    private Integer parseSafeInteger(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return Integer.valueOf(s);
    }

    public List<WeatherData> getWeatherByMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusDays(1).withHour(23).withMinute(59);
        return repository.findByDatetime_utcBetween(start, end);
    }

    public List<WeatherData> getWeatherByDate(int year, int month, int day) {
        LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime end = start.withHour(23).withMinute(59);
        return repository.findByDatetime_utcBetween(start, end);
    }

    public static class MonthlyStats {
        public Double highTemp;
        public Double medianTemp;
        public Double minTemp;
    }

    public MonthlyStats getMonthlyTemperatureStats(int year, int month) {
        List<WeatherData> monthlyData = repository.findByDatetime_utcYearAndDatetime_utcMonth(year, month);
        if (monthlyData.isEmpty()) {
            return null;
        }

        List<Double> temperatures = monthlyData.stream()
                .map(WeatherData::get_tempm)
                .filter(temp -> temp != null && temp > -9999)
                .sorted()
                .collect(Collectors.toList());

        if (temperatures.isEmpty()) {
            return null;
        }

        double minTemp = temperatures.get(0);
        double maxTemp = temperatures.get(temperatures.size() - 1);
        double medianTemp;

        int size = temperatures.size();
        if (size % 2 == 1) {
            medianTemp = temperatures.get(size / 2);
        } else {
            medianTemp = (temperatures.get(size / 2 - 1) + temperatures.get(size / 2)) / 2.0;
        }

        MonthlyStats stats = new MonthlyStats();
        stats.highTemp = maxTemp;
        stats.medianTemp = medianTemp;
        stats.minTemp = minTemp;
        return stats;
    }
}