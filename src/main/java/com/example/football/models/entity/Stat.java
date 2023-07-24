package com.example.football.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "stat")
public class Stat extends BaseEntity{

    @Column(nullable = false)
    @Positive
    private float shooting;

    @Column(nullable = false)
    @Positive
    private float passing;

    @Column(nullable = false)
    @Positive
    private float endurance;

    public Stat() {
    }

    public double getShooting() {
        return shooting;
    }

    public void setShooting(float shooting) {
        this.shooting = shooting;
    }

    public double getPassing() {
        return passing;
    }

    public void setPassing(float passing) {
        this.passing = passing;
    }

    public double getEndurance() {
        return endurance;
    }

    public void setEndurance(float endurance) {
        this.endurance = endurance;
    }
}
