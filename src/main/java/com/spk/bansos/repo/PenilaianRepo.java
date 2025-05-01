package com.spk.bansos.repo;


import com.spk.bansos.model.Penilaian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PenilaianRepo extends JpaRepository<Penilaian, Long> {

}
