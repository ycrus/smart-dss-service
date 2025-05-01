package com.spk.bansos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "calculate__receivers")
public class CalculateRecevier {
    @Id
    private Long id;
    private Long penilaianId;
    private Long receiverId;
}
