package com.spk.bansos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "parameters")
public class Parameter {
    @Id
    private Long id;

    private String title;
    private String operation;
    private String start;
    private String end;
    private String unit;
    private String description;
    private Long parameterWeight;
    private Long criteriaId;
}
