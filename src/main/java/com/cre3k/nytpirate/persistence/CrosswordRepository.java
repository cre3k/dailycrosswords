package com.cre3k.nytpirate.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrosswordRepository
        extends JpaRepository<CrosswordEntity, Long> {

    Optional<CrosswordEntity> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);


    @Query("SELECT DISTINCT EXTRACT(YEAR FROM c.date) FROM CrosswordEntity c " +
            "ORDER BY EXTRACT(YEAR FROM c.date) DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT DISTINCT EXTRACT(MONTH FROM c.date) FROM CrosswordEntity c " +
            "WHERE EXTRACT(YEAR FROM c.date) = :year " +
            "ORDER BY EXTRACT(MONTH FROM c.date)")
    List<Integer> findDistinctMonthsByYear(@Param("year") int year);

    @Query("SELECT c.date FROM CrosswordEntity c WHERE c.date BETWEEN :start AND :end ORDER BY c.date")
    List<LocalDate> findDatesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}