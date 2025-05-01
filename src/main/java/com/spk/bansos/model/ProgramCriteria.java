package com.spk.bansos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "program_criterias")
public class ProgramCriteria {
    @Id
    private Long id;
    private Long programId;
    private Long criteriaId;
    private Long weight;
}
