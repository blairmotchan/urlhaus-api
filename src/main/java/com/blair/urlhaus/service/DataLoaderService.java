package com.blair.urlhaus.service;

import com.blair.urlhaus.domain.CsvGeographyRow;
import com.blair.urlhaus.domain.MalwareUrl;
import com.blair.urlhaus.repository.MalwareUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Service
@Profile("live")
public class DataLoaderService {
    private MalwareUrlRepository malwareUrlRepository;
    private RecompileDatabasesService recompileDatabasesService;
    private int batchSize;
    private String csvFileName;
    private boolean rewriteUrlData;

    public DataLoaderService(
            MalwareUrlRepository malwareUrlRepository,
            RecompileDatabasesService recompileDatabasesService,
            @Value("${batch-size}") int batchSize,
            @Value("${url-data-with-geography}") String csvFileName,
            @Value("${recompile-databases}") String recompileDatabases
    ) {
        this.malwareUrlRepository = malwareUrlRepository;
        this.batchSize = batchSize;
        this.csvFileName = csvFileName;
        this.recompileDatabasesService = recompileDatabasesService;
        this.rewriteUrlData = Boolean.valueOf(recompileDatabases);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() throws Exception {
        if (rewriteUrlData) {
            recompileDatabasesService.recompileDatabase();
        }
        if (malwareUrlRepository.getTotalCount() < 1) {
            populateDatabase();
        }
    }

    private void populateDatabase() {
        try (ICsvBeanReader beanReader = getBeanReader()) {
            iterateOverData(beanReader);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void iterateOverData(ICsvBeanReader beanReader) throws IOException {
        CsvGeographyRow csvRow;
        List<MalwareUrl> entries = new ArrayList<>();
        while ((csvRow = beanReader.read(CsvGeographyRow.class, CsvGeographyRow.HEADERS, CsvGeographyRow.CELL_PROCESSORS)) != null) {
            if (csvRow.getStatus().equals("online")) {
                entries = handleEntries(csvRow, entries);
            }
        }
        if (!entries.isEmpty()) {
            malwareUrlRepository.insert(entries);
        }
    }

    private List<MalwareUrl> handleEntries(CsvGeographyRow csvRow, List<MalwareUrl> entries) {
        if (entries.size() == batchSize) {
            malwareUrlRepository.insert(entries);
            entries = new ArrayList<>();
        }

        entries.add(buildMalwareUrl(csvRow));

        return entries;
    }

    private MalwareUrl buildMalwareUrl(CsvGeographyRow row) {
        return new MalwareUrl(
                Integer.parseInt(row.getId()),
                LocalDateTime.parse(row.getDateAdded(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                row.getUrl(),
                row.getStatus(),
                row.getThreat(),
                asList(row.getTags().split(",")),
                row.getUrlHausLink(),
                row.getReporter(),
                row.getCountry(),
                row.getCity(),
                row.getLatitude() != null ? Double.valueOf(row.getLatitude()) : null,
                row.getLongitude() != null ? Double.valueOf(row.getLongitude()) : null
        );
    }

    private ICsvBeanReader getBeanReader() throws FileNotFoundException {
        File input = ResourceUtils.getFile("classpath:" + csvFileName);
        return new CsvBeanReader(new FileReader(input), CsvPreference.STANDARD_PREFERENCE);
    }
}