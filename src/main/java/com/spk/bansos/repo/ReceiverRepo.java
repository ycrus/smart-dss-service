package com.spk.bansos.repo;


import com.spk.bansos.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiverRepo extends JpaRepository<Receiver, Long> {

}
