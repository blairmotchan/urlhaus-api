package com.blair.urlhaus.service;

import com.blair.urlhaus.domain.CsvGeographyRow;
import com.blair.urlhaus.domain.CsvRow;
import com.blair.urlhaus.domain.Geography;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecompileDatabasesService {
    private String inputFileName;
    private String outputFileName;
    private IpGeographyService ipGeographyService;

    public RecompileDatabasesService(
            IpGeographyService ipGeographyService,
            @Value("${url-data-only-file-name}") String inputFileName,
            @Value("${url-data-with-geography}") String outputFileName
    ) {
        this.ipGeographyService = ipGeographyService;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    public void recompileDatabase() throws IOException, GeoIp2Exception {
        ICsvBeanReader beanReader = null;
        ICsvBeanWriter beanWriter = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(ResourceUtils.getFile("classpath:" + inputFileName)),
                    CsvPreference.STANDARD_PREFERENCE);
            beanWriter = new CsvBeanWriter(new FileWriter(OUTPUT_FILE_PATH + outputFileName),
                    CsvPreference.STANDARD_PREFERENCE);
            CsvRow row;
            while ((row = beanReader.read(CsvRow.class, CsvRow.HEADERS, CsvRow.CELL_PROCESSORS)) != null) {
                handleCsvRow(row, beanWriter);
            }
        } finally {
            closeResources(beanWriter, beanReader);
        }
    }

    private void handleCsvRow(CsvRow csvRow, ICsvBeanWriter beanWriter) throws IOException, GeoIp2Exception {
        if (csvRow.getStatus().equals("online")) {
            Matcher m = PATTERN.matcher(csvRow.getUrl());
            Geography geography = m.matches() ? ipGeographyService.getGeography(m.group(2)) : IpGeographyService.getEmptyGeography();
            CsvGeographyRow csvGeographyRow = new CsvGeographyRow(
                    csvRow.getId(),
                    csvRow.getDateAdded(),
                    csvRow.getUrl(),
                    csvRow.getStatus(),
                    csvRow.getThreat(),
                    csvRow.getTags(),
                    csvRow.getUrlHausLink(),
                    csvRow.getReporter(),
                    geography.getCountry(),
                    geography.getCity(),
                    geography.getLongitude() != null ? geography.getLongitude().toString() : null,
                    geography.getLatitude() != null ? geography.getLatitude().toString() : null
            );
            beanWriter.write(csvGeographyRow, CsvGeographyRow.HEADERS, CsvGeographyRow.CELL_PROCESSORS);
        }
    }

    private void closeResources(ICsvBeanWriter beanWriter, ICsvBeanReader beanReader) throws IOException {
        if (beanReader != null) {
            beanReader.close();
        }
        if (beanWriter != null) {
            beanWriter.close();
        }
    }

    private static final String OUTPUT_FILE_PATH = "src/main/resources/";

    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b)(.*?)");
}
