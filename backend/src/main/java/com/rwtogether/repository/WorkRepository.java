package com.rwtogether.repository;

import com.rwtogether.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {

    List<Work> findByType(Work.WorkType type);

    boolean existsByApiSourceAndApiId(String apiSource, String apiId);

    Optional<Work> findByApiSourceAndApiId(String apiSource, String apiId);
}
