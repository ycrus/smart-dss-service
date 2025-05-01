package com.spk.bansos.repo;


import com.spk.bansos.model.Criteria;
import com.spk.bansos.model.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CriteriaRepo extends JpaRepository<Criteria, Long> {

    Optional<Criteria> findByTitle(String criteria);
}
