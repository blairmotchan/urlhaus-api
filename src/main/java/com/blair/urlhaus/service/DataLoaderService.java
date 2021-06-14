package com.blair.urlhaus.service;

import com.blair.urlhaus.Row;
import com.blair.urlhaus.domain.MalwareUrl;
import com.blair.urlhaus.repository.MalwareUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.PostConstruct;
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
    private int batchSize;
    private String csvFileName;

    public DataLoaderService(
            MalwareUrlRepository malwareUrlRepository,
            @Value("${batch-size}") int batchSize,
            @Value("${input-file-name}") String csvFileName
    ) {
        this.malwareUrlRepository = malwareUrlRepository;
        this.batchSize = batchSize;
        this.csvFileName = csvFileName;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() throws IOException {
        if (malwareUrlRepository.getTotalCount() < 1) {
            populateDatabase();
        }
    }

    private void populateDatabase() throws IOException {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = getBeanReader();
            Row row;
            List<MalwareUrl> entries = new ArrayList<>();
            while ((row = beanReader.read(Row.class, HEADERS, CELL_PROCESSORS)) != null) {
                if (row.getStatus().equals("online")) {
                    entries = handleEntries(row, entries);
                }
            }
            if (!entries.isEmpty()) {
                malwareUrlRepository.insert(entries);
            }
        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }
    }

    private List<MalwareUrl> handleEntries(Row row, List<MalwareUrl> entries) {
        if (entries.size() == batchSize) {
            malwareUrlRepository.insert(entries);
            entries = new ArrayList<>();
        }

        entries.add(
                new MalwareUrl(
                        Integer.parseInt(row.getId()),
                        LocalDateTime.parse(row.getDateAdded(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        row.getUrl(),
                        row.getStatus(),
                        row.getThreat(),
                        asList(row.getTags().split(",")),
                        row.getUrlHausLink(),
                        row.getReporter()
                )
        );

        return entries;
    }

    private ICsvBeanReader getBeanReader() throws FileNotFoundException {
        File input = ResourceUtils.getFile("classpath:" + csvFileName);
        return new CsvBeanReader(new FileReader(input), CsvPreference.STANDARD_PREFERENCE);
    }

    private static final CellProcessor[] CELL_PROCESSORS =
            new CellProcessor[]{
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional()
            };

    private static final String[] HEADERS = {
            "id",
            "dateAdded",
            "url",
            "status",
            "threat",
            "tags",
            "urlHausLink",
            "reporter"
    };
}
