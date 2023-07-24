package com.example.football.models.dto;


import com.google.gson.annotations.Expose;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class TownSeedDto {

    @Expose
    @Size(min = 2)
    private String name;

    @Expose
    @Positive
    private Integer population;

    @Expose
    @Size(min = 10)
    private String travelGuide;


    public TownSeedDto() {
    }

    public String getName() {
        return name;
    }

    public Integer getPopulation() {
        return population;
    }

    public String getTravelGuide() {
        return travelGuide;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public void setTravelGuide(String travelGuide) {
        this.travelGuide = travelGuide;
    }
}
