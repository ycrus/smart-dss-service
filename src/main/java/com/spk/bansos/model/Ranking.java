package com.spk.bansos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "rangkings")
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    private Long receiverId;
    private Long penilaianId;
    private Integer rangking;
    private BigDecimal total;
    private Boolean isRanked = false;
    private String status;

}
