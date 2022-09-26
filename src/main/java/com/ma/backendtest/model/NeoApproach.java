package com.ma.backendtest.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "neo_approaches")
@Table(indexes = {
        @Index(columnList = "epoch_date, miss_distance_km")
})
public class NeoApproach {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "epoch_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime epochDate;

    @Column(name = "miss_distance_km")
    private Double missDistanceKm;

    @ManyToOne
    @JoinColumn(name="neo_id", nullable=false)
    private Neo neo;


    public Long getId() {
        return id;
    }

    public LocalDateTime getEpochDate() {
        return epochDate;
    }

    public void setEpochDate(LocalDateTime epochDate) {
        this.epochDate = epochDate;
    }

    public Double getMissDistanceKm() {
        return missDistanceKm;
    }

    public void setMissDistanceKm(Double missDistanceKm) {
        this.missDistanceKm = missDistanceKm;
    }

    public Neo getNeo() {
        return neo;
    }

    public void setNeo(Neo neo) {
        this.neo = neo;
    }
}
