package com.spk.bansos.repo;


import com.spk.bansos.model.NilaiBobot;
import com.spk.bansos.model.NilaiUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NilaiUtilityRepo extends JpaRepository<NilaiUtility, Long> {

    Optional<NilaiUtility> findByReceiverIdAndPenilaianId(Long userId, Long penilaianId);
    List<NilaiUtility> findByPenilaianId(Long penilaianId);

}
