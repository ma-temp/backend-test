package com.ma.backendtest.model;

import javax.persistence.*;
import java.util.List;

@Entity(name = "neos")
@Table(indexes = @Index(columnList = "estimated_diameter_meter_min"))
public class Neo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nasa_reference_id")
    private String nasaReferenceId;

    private String name;

    @Column(name = "estimated_diameter_meter_min")
    private Double estimatedDiameterMeterMin;

    @Column(name = "estimated_diameter_meter_max")
    private Double estimatedDiameterMeterMax;

    @OneToMany(mappedBy="neo")
    private List<NeoApproach> neoApproaches;

    // @ToDo: Add more fields to db models if needed



    public Long getId() {
        return id;
    }

    public String getNasaReferenceId() {
        return nasaReferenceId;
    }

    public void setNasaReferenceId(String nasaReferenceId) {
        this.nasaReferenceId = nasaReferenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getEstimatedDiameterMeterMin() {
        return estimatedDiameterMeterMin;
    }

    public void setEstimatedDiameterMeterMin(Double estimatedDiameterMeterMin) {
        this.estimatedDiameterMeterMin = estimatedDiameterMeterMin;
    }

    public Double getEstimatedDiameterMeterMax() {
        return estimatedDiameterMeterMax;
    }

    public void setEstimatedDiameterMeterMax(Double estimatedDiameterMeterMax) {
        this.estimatedDiameterMeterMax = estimatedDiameterMeterMax;
    }

    public List<NeoApproach> getNeoApproaches() {
        return neoApproaches;
    }

    public void setNeoApproaches(List<NeoApproach> neoApproaches) {
        this.neoApproaches = neoApproaches;
    }
}
