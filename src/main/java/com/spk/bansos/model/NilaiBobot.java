package com.spk.bansos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "nilai_bobots")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NilaiBobot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    private Long receiverId;
    private Long penilaianId;
    private BigDecimal umur;
    private BigDecimal pekerjaan;
    private BigDecimal penghasilan;
    private BigDecimal statusTempatTinggal;
    private BigDecimal statusPerkawinan;
    private BigDecimal jumlahTanggungan;
    private BigDecimal keadaanRumah;
    private BigDecimal disabilitas;
    private BigDecimal pendidikan;
    private BigDecimal fasilitasMck;
    private BigDecimal bahanBakarHarian;
    private BigDecimal kepemilikanKendaraan;
}
