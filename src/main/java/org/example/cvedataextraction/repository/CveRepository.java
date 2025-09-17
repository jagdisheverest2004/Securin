package org.example.cvedataextraction.repository;

import org.example.cvedataextraction.model.Cve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CveRepository extends JpaRepository<Cve, String> {

    @Query("SELECT c FROM Cve c WHERE c.lastModifiedDate >= :date")
    List<Cve> findByLastModifiedDateGreaterThanEqual(LocalDateTime date);
}