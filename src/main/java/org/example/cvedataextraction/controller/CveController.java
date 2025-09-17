package org.example.cvedataextraction.controller;

import org.example.cvedataextraction.model.Cve;
import org.example.cvedataextraction.repository.CveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cves")
public class CveController {

    @Autowired
    private CveRepository cveRepository;

    @GetMapping("/{cveId}")
    public Optional<Cve> getCveById(@PathVariable String cveId) {
        return cveRepository.findById(cveId);
    }

    @GetMapping("/last-modified")
    public List<Cve> getCvesLastModifiedInDays(@RequestParam int days) {
        LocalDateTime daysAgo = LocalDateTime.now().minus(Period.ofDays(days));
        return cveRepository.findByLastModifiedDateGreaterThanEqual(daysAgo);
    }
}