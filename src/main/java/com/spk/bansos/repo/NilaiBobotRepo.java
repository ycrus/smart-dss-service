package com.spk.bansos.repo;


import com.spk.bansos.model.NilaiBobot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NilaiBobotRepo extends JpaRepository<NilaiBobot, Long> {
    Optional<NilaiBobot> findByReceiverIdAndPenilaianId(Long userId, Long penilaianId);
    List<NilaiBobot> findByPenilaianId(Long penilaianId);

}
