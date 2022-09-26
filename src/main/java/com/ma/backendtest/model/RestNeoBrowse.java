package com.ma.backendtest.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestNeoBrowse(Links links, Page page, List<NearEarthObject> near_earth_objects) {

    public record Page(int size, int total_elements, int total_pages, int number) {}

    public record Links(String next, String self) {}

    public record NearEarthObject(String id, String neo_reference_id, String name, String name_limited,
                                  EstimatedDiameter estimated_diameter,
                                  List<CloseApproachData> close_approach_data) {

        public record EstimatedDiameter(Meters meters) {

            public record Meters(double estimated_diameter_min, double estimated_diameter_max) {}
        }

        public record CloseApproachData(String close_approach_date, String close_approach_date_full,
            long epoch_date_close_approach, MissDistance miss_distance, String orbiting_body) {

            public record MissDistance(String kilometers) {}
        }
    }
}