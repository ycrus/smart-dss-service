package com.spk.bansos.repo;


import com.spk.bansos.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepo extends JpaRepository<Period, Long> {

}
