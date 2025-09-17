package org.example.cvedataextraction.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cve {
    @Id
    private String cveId;
    private String sourceIdentifier;
    private String publishedDate;
    private LocalDateTime lastModifiedDate;
    private String vulnerabilityStatus;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double cvssV3Score;
}