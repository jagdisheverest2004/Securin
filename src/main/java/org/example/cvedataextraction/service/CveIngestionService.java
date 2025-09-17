package org.example.cvedataextraction.service;

import org.example.cvedataextraction.model.Cve;
import org.example.cvedataextraction.repository.CveRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CveIngestionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CveRepository cveRepository;

    private static final String NVD_API_URL = "https://services.nvd.nist.gov/rest/json/cves/2.0?resultsPerPage=2000&startIndex=";
    private static final int RESULTS_PER_PAGE = 2000;

    public void fetchAndStoreAllCves() {
        int startIndex = 0;
        long totalResults = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        do {
            try {
                String url = NVD_API_URL + startIndex;
                String response = restTemplate.getForObject(url, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response);

                if (totalResults == 0) {
                    totalResults = root.path("totalResults").asLong();
                }

                JsonNode vulnerabilitiesNode = root.path("vulnerabilities");
                List<Cve> cves = new ArrayList<>();
                for (JsonNode vulnNode : vulnerabilitiesNode) {
                    JsonNode cveNode = vulnNode.path("cve");
                    Cve cve = new Cve();
                    cve.setCveId(cveNode.path("id").asText());
                    cve.setPublishedDate(cveNode.path("published").asText());
                    String lastModifiedDateStr = cveNode.path("lastModified").asText();
                    cve.setLastModifiedDate(LocalDateTime.parse(lastModifiedDateStr, formatter));
                    cve.setVulnerabilityStatus(cveNode.path("vulnStatus").asText());

                    // Get description
                    JsonNode descriptions = cveNode.path("descriptions");
                    for (JsonNode descNode : descriptions) {
                        if ("en".equals(descNode.path("lang").asText())) {
                            cve.setDescription(descNode.path("value").asText());
                            break;
                        }
                    }

                    // Get CVSS v3 score (simplified)
                    JsonNode metrics = cveNode.path("metrics");
                    JsonNode cvssV3 = metrics.path("cvssMetricV31");
                    if (cvssV3.isArray() && !cvssV3.isEmpty()) {
                        cve.setCvssV3Score(cvssV3.get(0).path("cvssData").path("baseScore").asDouble());
                    }

                    cves.add(cve);
                }

                cveRepository.saveAll(cves);
                startIndex += RESULTS_PER_PAGE;
                System.out.println("Fetched and stored " + cves.size() + " CVEs. Total processed: " + startIndex);

                // Add a delay to respect rate limits
                Thread.sleep(6000); // 6-second delay
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (startIndex < totalResults);
    }
}