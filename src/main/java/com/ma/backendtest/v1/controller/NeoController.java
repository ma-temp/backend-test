package com.ma.backendtest.v1.controller;

import com.ma.backendtest.model.NeoApproach;
import com.ma.backendtest.repository.NeoApproachRepository;
import com.ma.backendtest.repository.NeoRepository;
import com.ma.backendtest.service.NeoDataManager;
import com.ma.backendtest.v1.model.RestNeoApproach;
import com.ma.backendtest.v1.model.RestNeoItem;
import com.ma.backendtest.v1.model.RestNeoList;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;


@RestController("neoControllerV1")
@RequestMapping("/api/v1/neo")
public class NeoController {

    private final NeoDataManager neoDataManager;
    private final NeoApproachRepository neoApproachRepository;
    private final NeoRepository neoRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);


    public NeoController(NeoDataManager neoDataManager, NeoApproachRepository neoApproachRepository,
                         NeoRepository neoRepository) {
        this.neoDataManager = neoDataManager;
        this.neoApproachRepository = neoApproachRepository;
        this.neoRepository = neoRepository;
    }

    @GetMapping("/fetch")
    public ResponseEntity<Void> fetchData() {
        var status = neoDataManager.fetchNeoData();
        if(status == NeoDataManager.DataStatus.READY) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/closest")
    public ResponseEntity<RestNeoList> getClosest(
            @RequestParam() String from,
            @RequestParam() String to) {

        if(!neoDataManager.isDataReady()) {
            return ResponseEntity.status(503).header("Retry-After", "600").build();
        }

        var fromDate = parseDate(from);
        var toDate = parseDate(to);

        var neoApproaches = neoApproachRepository.findAllByEpochDateBetween(
                fromDate.atStartOfDay(), toDate.atStartOfDay(),
                PageRequest.of(0, 10, Sort.by("missDistanceKm")));
        var neoList = new ArrayList<RestNeoItem>();
        for (NeoApproach neoApproach: neoApproaches) {
            var restNeoApproach = new RestNeoApproach(neoApproach.getId(), neoApproach.getEpochDate(),
                    neoApproach.getMissDistanceKm());
            var neo = neoApproach.getNeo();
            var restNeoItem = new RestNeoItem(neo.getId(), neo.getNasaReferenceId(), neo.getName(),
                    neo.getEstimatedDiameterMeterMin(), neo.getEstimatedDiameterMeterMax(), restNeoApproach);
            neoList.add(restNeoItem);
        }

        return ResponseEntity.ok(new RestNeoList(neoList));
    }

    @GetMapping("/largest")
    public ResponseEntity<RestNeoItem> getLargest(
            @RequestParam() String year) {

        if(!neoDataManager.isDataReady()) {
            return ResponseEntity.status(503).header("Retry-After", "600").build();
        }

        var parsedYear = parseYear(year);

        var largestNeo = neoRepository.findOneByNeoApproachesEpochDateBetween(
                LocalDate.of(parsedYear, 1, 1).atStartOfDay(),
                LocalDate.of(parsedYear + 1, 1, 1).atStartOfDay(),
                        PageRequest.of(0, 1, Sort.by("estimatedDiameterMeterMin").descending()));

        if(largestNeo.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        var neo = largestNeo.get(0);
        var restNeoItem = new RestNeoItem(neo.getId(), neo.getNasaReferenceId(), neo.getName(),
                neo.getEstimatedDiameterMeterMin(), neo.getEstimatedDiameterMeterMax(), null);
        return ResponseEntity.ok(restNeoItem);
    }

    public LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Date string!");
        }
    }

    public int parseYear(String year) {
        try {
            var parsedYear = Integer.parseInt(year);
            if(parsedYear <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Year string!");
            }
            return parsedYear;
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Year string!");
        }
    }
}
