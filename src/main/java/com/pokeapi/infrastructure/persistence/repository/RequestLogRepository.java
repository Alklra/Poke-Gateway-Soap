package com.pokeapi.infrastructure.persistence.repository;

import com.pokeapi.infrastructure.persistence.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
}