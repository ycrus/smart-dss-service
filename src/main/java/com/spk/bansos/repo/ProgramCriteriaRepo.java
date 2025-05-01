package com.spk.bansos.repo;


import com.spk.bansos.model.ProgramCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramCriteriaRepo extends JpaRepository<ProgramCriteria, Long> {
    List<ProgramCriteria> findByProgramId(Long id);
    Optional<ProgramCriteria> findByProgramIdAndCriteriaId(Long id, Long criteriaId);
}
