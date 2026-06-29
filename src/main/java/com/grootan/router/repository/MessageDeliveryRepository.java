package com.grootan.router.repository;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.MessageDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery, Long> {

    List<MessageDelivery> findByMessage(Message message);

}