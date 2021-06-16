package com.blair.urlhaus.service;

import com.blair.urlhaus.repository.MalwareUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderServiceTest {
    @Mock
    private MalwareUrlRepository malwareUrlRepository;

    @Mock
    private RecompileDatabasesService recompileDatabasesService;

    private static final int BATCH_SIZE = 3;
    private static final String CSV_FILE_NAME = "test-csv_ip.txt";
    private static final String RECOMPILE_DATA = "false";

    private DataLoaderService dataLoaderService;

    @BeforeEach
    public void setUp() {
        dataLoaderService = new DataLoaderService(
                malwareUrlRepository,
                recompileDatabasesService,
                BATCH_SIZE,
                CSV_FILE_NAME,
                RECOMPILE_DATA
        );
    }

    @Test
    public void loadData_whenRecompileDatabaseTrue_expectDatabasesRecompiled() throws Exception {
        dataLoaderService = new DataLoaderService(
                malwareUrlRepository,
                recompileDatabasesService,
                BATCH_SIZE,
                CSV_FILE_NAME,
                "true"
        );
        doNothing().when(recompileDatabasesService).recompileDatabase();
        when(malwareUrlRepository.getTotalCount()).thenReturn(1);

        dataLoaderService.loadData();

        verify(recompileDatabasesService, times(1)).recompileDatabase();
    }

    @Test
    public void loadData_whenDataAlreadyPresent_expectNoDataLoaded() throws Exception {
        when(malwareUrlRepository.getTotalCount()).thenReturn(1);

        dataLoaderService.loadData();

        verify(malwareUrlRepository, times(0)).insert(Mockito.anyList());
    }

    @Test
    public void loadData_whenNoDataPresent_expectDataLoaded() throws Exception {
        when(malwareUrlRepository.getTotalCount()).thenReturn(0);

        dataLoaderService.loadData();

        verify(malwareUrlRepository, times(2)).insert(Mockito.anyList());
    }
}