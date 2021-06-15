package com.blair.urlhaus.service;

import com.blair.urlhaus.domain.CsvRow;
import com.blair.urlhaus.domain.Geography;
import com.blair.urlhaus.domain.MalwareUrl;
import com.blair.urlhaus.repository.MalwareUrlRepository;
import com.maxmind.geoip2.exception.GeoIp2Exception;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

@Service
@Profile("live")
public class DataLoaderService {
    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b)(.*?)");

    private MalwareUrlRepository malwareUrlRepository;
    private IpGeographyService ipGeographyService;
    private int batchSize;
    private String csvFileName;

    public DataLoaderService(
            MalwareUrlRepository malwareUrlRepository,
            IpGeographyService ipGeographyService,
            @Value("${batch-size}") int batchSize,
            @Value("${input-file-name}") String csvFileName
    ) {
        this.malwareUrlRepository = malwareUrlRepository;
        this.ipGeographyService = ipGeographyService;
        this.batchSize = batchSize;
        this.csvFileName = csvFileName;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() throws IOException, GeoIp2Exception {
        if (malwareUrlRepository.getTotalCount() < 1) {
            populateDatabase();
        }
    }

    private void populateDatabase() throws IOException, GeoIp2Exception {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = getBeanReader();
            iterateOverData(beanReader);
        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }
    }

    private void iterateOverData(ICsvBeanReader beanReader) throws IOException, GeoIp2Exception {
        CsvRow csvRow;
        List<MalwareUrl> entries = new ArrayList<>();
        while ((csvRow = beanReader.read(CsvRow.class, HEADERS, CELL_PROCESSORS)) != null) {
            if (csvRow.getStatus().equals("online")) {
                entries = handleEntries(csvRow, entries);
            }
        }
        if (!entries.isEmpty()) {
            malwareUrlRepository.insert(entries);
        }
    }

    private List<MalwareUrl> handleEntries(CsvRow csvRow, List<MalwareUrl> entries) throws IOException, GeoIp2Exception {
        if (entries.size() == batchSize) {
            malwareUrlRepository.insert(entries);
            entries = new ArrayList<>();
        }

        entries.add(buildMalwareUrl(csvRow));

        return entries;
    }

    private MalwareUrl buildMalwareUrl(CsvRow csvRow) throws IOException, GeoIp2Exception {
        Matcher m = PATTERN.matcher(csvRow.getUrl());
        Geography geography = m.matches() ? ipGeographyService.getGeography(m.group(2)) : IpGeographyService.getEmptyGeography();
        return new MalwareUrl(
                Integer.parseInt(csvRow.getId()),
                LocalDateTime.parse(csvRow.getDateAdded(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                csvRow.getUrl(),
                csvRow.getStatus(),
                csvRow.getThreat(),
                asList(csvRow.getTags().split(",")),
                csvRow.getUrlHausLink(),
                csvRow.getReporter(),
                geography.getCountry(),
                geography.getCity(),
                geography.getLatitude(),
                geography.getLongitude()
        );
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
