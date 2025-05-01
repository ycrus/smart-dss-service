package com.spk.bansos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "criterias")
public class Criteria {
    @Id
    private Long id;

    private String title;
    private String unit;
    private String description;
}
