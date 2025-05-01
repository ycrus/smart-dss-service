package com.spk.bansos.repo;


import com.spk.bansos.model.NilaiAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NilaiAkhirRepo extends JpaRepository<NilaiAkhir, Long> {

    Optional<NilaiAkhir> findByReceiverIdAndPenilaianId(Long userId, Long penilaianId);
}
