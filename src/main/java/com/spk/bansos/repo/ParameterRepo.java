package com.spk.bansos.repo;


import com.spk.bansos.model.Parameter;
import com.spk.bansos.model.ProgramCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParameterRepo extends JpaRepository<Parameter, Long> {
    Optional<Parameter> findByTitle(String parameter);
}
