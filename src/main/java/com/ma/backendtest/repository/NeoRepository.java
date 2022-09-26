package com.ma.backendtest.repository;

import com.ma.backendtest.model.Neo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NeoRepository extends PagingAndSortingRepository<Neo, Long> {

    List<Neo> findOneByNeoApproachesEpochDateBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
