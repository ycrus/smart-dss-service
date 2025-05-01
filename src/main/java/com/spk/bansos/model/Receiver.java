package com.spk.bansos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "receivers")
public class Receiver {
    @Id
    private Long id;

    private String nama;
    private String nik;
    private String tanggalLahir;
    private String rt;
    private String rw;
    private String kelurahan;
    private String pekerjaan;
    private String penghasilan;
    private String statusTempatTinggal;
    private String statusPerkawinan;
    private String jumlahTanggungan;
    private String keadaanRumah;
    private String disabilitas;
    private String pendidikan;
    private String fasilitasMck;
    private String bahanBakarHarian;
    private String kepemilikanKendaraan;

}
