package com.ma.backendtest.v1.model;


public record RestNeoItem(Long id, String nasaReferenceId, String name,
                          Double estimatedDiameterMeterMin, Double estimatedDiameterMeterMax,
                          RestNeoApproach neoClosestApproach) {}
