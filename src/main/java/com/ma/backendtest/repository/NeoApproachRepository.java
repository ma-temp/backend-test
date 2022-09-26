package com.ma.backendtest.repository;

import com.ma.backendtest.model.NeoApproach;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NeoApproachRepository extends PagingAndSortingRepository<NeoApproach, Long> {

    List<NeoApproach> findAllByEpochDateBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
