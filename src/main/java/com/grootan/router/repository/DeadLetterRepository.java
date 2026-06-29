package com.grootan.router.repository;

import com.grootan.router.entity.DeadLetterMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadLetterRepository extends JpaRepository<DeadLetterMessage, Long> {

}