package com.cre3k.nytpirate.persistence;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "crossword",
        uniqueConstraints = @UniqueConstraint(columnNames = "date"))
public class CrosswordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String payload;

}