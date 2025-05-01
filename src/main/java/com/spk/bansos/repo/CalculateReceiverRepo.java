package com.spk.bansos.repo;


import com.spk.bansos.model.CalculateRecevier;
import com.spk.bansos.model.ProgramCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculateReceiverRepo extends JpaRepository<CalculateRecevier, Long> {
    List<CalculateRecevier> findByPenilaianId(Long id);
}
