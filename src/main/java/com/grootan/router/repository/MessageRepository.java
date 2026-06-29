package com.grootan.router.repository;

import com.grootan.router.entity.Message;
import com.grootan.router.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUser(User user);

}