package com.spk.bansos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "periods")
public class Period {
    @Id
    private Long id;

    private String name;
    private Long programId;
    private Boolean status;
    private String description;
}
