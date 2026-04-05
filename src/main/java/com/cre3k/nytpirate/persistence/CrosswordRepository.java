package com.cre3k.nytpirate.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrosswordRepository
        extends JpaRepository<CrosswordEntity, Long> {

    Optional<CrosswordEntity> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);


    @Query("SELECT c.date FROM CrosswordEntity c")
    List<LocalDate> findAllDates();
}