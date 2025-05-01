package com.spk.bansos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "penilaians")
public class Penilaian {
    @Id
    private Long id;
    private Long periodId;
    private String status;
    private Long jumlahPenerima;
}
