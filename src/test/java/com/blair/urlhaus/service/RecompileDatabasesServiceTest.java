package com.blair.urlhaus.service;

import com.blair.urlhaus.domain.Geography;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RecompileDatabasesServiceTest {
    private static final String INPUT_FILE_NAME = "test-csv.txt";
    private static final String OUTPUT_FILE_NAME = "test-csv_ip.txt";

    private RecompileDatabasesService recompileDatabasesService;

    @Mock
    private IpGeographyService ipGeographyService;


    @BeforeEach
    public void setUp() {
        recompileDatabasesService = new RecompileDatabasesService(
                ipGeographyService,
                INPUT_FILE_NAME,
                OUTPUT_FILE_NAME
        );
    }

    @AfterEach
    public void tearDown() {
        getOutputFile().delete();
    }

    @Test
    public void recompileDatabase() throws IOException, GeoIp2Exception {
        Geography geography = buildGeography();
        Mockito.when(ipGeographyService.getGeography("222.220.208.254")).thenReturn(geography);
        Mockito.when(ipGeographyService.getGeography("27.41.199.177")).thenReturn(geography);
        recompileDatabasesService.recompileDatabase();

        File file = getOutputFile();
        assertThat(file.exists()).isTrue();
    }

    private File getOutputFile() {
        return new File("src/main/resources/" + OUTPUT_FILE_NAME);
    }

    private Geography buildGeography() {
        return new Geography(
                "country",
                "city",
                Double.MAX_VALUE,
                Double.MIN_VALUE
        );
    }
}