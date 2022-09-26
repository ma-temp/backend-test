package com.ma.backendtest.service;

import com.ma.backendtest.model.Variable;
import com.ma.backendtest.repository.VariableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NeoDataManagerTest {

    @Mock
    private NeoDataFetcher neoDataFetcher;

    @Mock
    private VariableRepository variableRepository;

    private NeoDataManager neoDataManager;

    @BeforeEach
    void setUp() {
        var variable = new Variable();
        variable.setName(NeoDataManager.DATA_STATUS_VAR_KEY);
        variable.setVal(NeoDataManager.DataStatus.READY.toString());
        Mockito.when(variableRepository.findByName(ArgumentMatchers.anyString())).thenReturn(Optional.of(variable));
        neoDataManager = new NeoDataManager(neoDataFetcher, variableRepository);
    }

    @Test
    void fetchNeoData_returnsWithoutRunningJob() {
        Mockito.when(variableRepository.save(ArgumentMatchers.any())).thenReturn(null);
        var dataStatus = neoDataManager.fetchNeoData();
        assertEquals(NeoDataManager.DataStatus.READY, dataStatus);
    }

    // @ToDo: Write more unitTests to test exception handling
}