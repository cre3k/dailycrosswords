package com.cre3k.nytpirate.persistence;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "crossword",
        uniqueConstraints = @UniqueConstraint(columnNames = "date"))
public class CrosswordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String payload;

}