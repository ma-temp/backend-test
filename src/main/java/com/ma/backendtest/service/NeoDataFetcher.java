package com.ma.backendtest.service;

import com.ma.backendtest.model.Neo;
import com.ma.backendtest.model.NeoApproach;
import com.ma.backendtest.model.RestNeoBrowse;
import com.ma.backendtest.model.Variable;
import com.ma.backendtest.repository.NeoApproachRepository;
import com.ma.backendtest.repository.NeoRepository;
import com.ma.backendtest.repository.VariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NeoDataFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoDataFetcher.class);
    private static final String API_URL = "https://api.nasa.gov/neo/rest/v1/neo/browse?api_key={API_KEY}";
    // @ToDo: read API_KEY from env instead of hardcoding
    private static final String API_KEY = "SF9hvlDNMe0jBoJPQ3Es4ju4OGfA680GgSLTbf2t";

    private final RestTemplate restTemplate;
    private final VariableRepository variableRepository;
    private final NeoRepository neoRepository;
    private final NeoApproachRepository neoApproachRepository;


    public NeoDataFetcher(RestTemplate restTemplate, VariableRepository variableRepository, NeoRepository neoRepository, NeoApproachRepository neoApproachRepository) {
        this.restTemplate = restTemplate;
        this.variableRepository = variableRepository;
        this.neoRepository = neoRepository;
        this.neoApproachRepository = neoApproachRepository;
    }

    @Async
    public ListenableFuture<NeoDataManager.DataStatus> fetchNeoDataToDb() {

        neoApproachRepository.deleteAll();
        neoRepository.deleteAll();
        variableRepository.deleteAll();

        var nextUrl = API_URL.replace("{API_KEY}", API_KEY);
        iterateBrowseApi(nextUrl);

        Variable dataStatusVar = new Variable();
        dataStatusVar.setName(NeoDataManager.DATA_STATUS_VAR_KEY);
        dataStatusVar.setVal(NeoDataManager.DataStatus.READY.name());
        variableRepository.save(dataStatusVar);

        return new AsyncResult<>(NeoDataManager.DataStatus.READY);
    }

    private void iterateBrowseApi(String nextUrl) {
        while (nextUrl != null) {
            LOGGER.info("url: " + nextUrl);

            var result = restTemplate.exchange(nextUrl.replace("http://", "https://"),
                    HttpMethod.GET, null, RestNeoBrowse.class).getBody();

            insertNeoDataToDb(result);

            if(result == null || result.links() == null || result.links().next() == null) {
                nextUrl = null;
                continue;
            }
            nextUrl = result.links().next();
            try {
                Thread.sleep(2500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertNeoDataToDb(RestNeoBrowse data) {
        if(data == null || data.near_earth_objects() == null) {
            return;
        }

        for (RestNeoBrowse.NearEarthObject nearEarthObject : data.near_earth_objects()) {
            LOGGER.info("id: " + nearEarthObject.neo_reference_id() + " count: " + nearEarthObject.close_approach_data().size());
            var neo = new Neo();
            neo.setNasaReferenceId(nearEarthObject.neo_reference_id());
            neo.setName(nearEarthObject.name());
            if(nearEarthObject.estimated_diameter() == null || nearEarthObject.estimated_diameter().meters() == null) {
                continue;
            }
            neo.setEstimatedDiameterMeterMax(nearEarthObject.estimated_diameter().meters().estimated_diameter_max());
            neo.setEstimatedDiameterMeterMin(nearEarthObject.estimated_diameter().meters().estimated_diameter_min());

            List<NeoApproach> neoApproaches = new ArrayList<>();
            for (RestNeoBrowse.NearEarthObject.CloseApproachData restNeoApproach : nearEarthObject.close_approach_data()) {
                var neoApproach = new NeoApproach();
                neoApproach.setNeo(neo);
                neoApproach.setMissDistanceKm(Double.parseDouble(restNeoApproach.miss_distance().kilometers()));
                neoApproach.setEpochDate(LocalDateTime.from(Instant.ofEpochMilli(restNeoApproach.epoch_date_close_approach())));
                neoApproaches.add(neoApproach);
            }
            neoRepository.save(neo);
            neoApproachRepository.saveAll(neoApproaches);
        }
    }
}
