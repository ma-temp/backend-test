package com.ma.backendtest.service;

import com.ma.backendtest.repository.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NeoDataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoDataManager.class);
    private final VariableRepository variableRepository;
    private final NeoDataFetcher neoDataFetcher;

    public static final String DATA_STATUS_VAR_KEY = "DATA_STATUS";
    private DataStatus dataStatus;
    private final Object dataStatusLock = new Object();


    public enum DataStatus {
        READY,
        IN_PROGRESS,
        NOT_FETCHED
    }


    public NeoDataManager(NeoDataFetcher neoDataFetcher, VariableRepository variableRepository) {

        this.neoDataFetcher = neoDataFetcher;
        this.variableRepository = variableRepository;

        var dbDataStatus = variableRepository.findByName(DATA_STATUS_VAR_KEY);
        if(dbDataStatus.isEmpty()) {
            dataStatus = DataStatus.NOT_FETCHED;
            return;
        }
        dataStatus = DataStatus.valueOf(dbDataStatus.get().getVal());
    }

    public DataStatus fetchNeoData() {

        synchronized (dataStatusLock) {
            if(dataStatus == DataStatus.READY || dataStatus == DataStatus.IN_PROGRESS) {
                return dataStatus;
            }

            dataStatus = DataStatus.IN_PROGRESS;
        }

        var listener = neoDataFetcher.fetchNeoDataToDb();
        listener.addCallback(this::fetchDataSuccessCallback, this::fetchDataFailureCallback);
        return DataStatus.IN_PROGRESS;
    }

    public boolean isDataReady() {
        synchronized (dataStatusLock) {
            return dataStatus == DataStatus.READY;
        }
    }

    public void fetchDataSuccessCallback(DataStatus status) {
        synchronized (dataStatusLock) {
            dataStatus = status;
        }
    }

    public void fetchDataFailureCallback(Throwable ex) {
        LOGGER.error("iterateBrowseApi() has failed!", ex);
        synchronized (dataStatusLock) {
            dataStatus = DataStatus.NOT_FETCHED;
        }
    }
}
