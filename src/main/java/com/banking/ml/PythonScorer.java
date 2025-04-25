// src/main/java/com/example/sample/ml/PythonScorer.java
package com.banking.ml;

import com.banking.dto.LoanFeatures;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PythonScorer {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL = "http://localhost:5000/predict";

    public String score(LoanFeatures f) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoanFeatures> entity = new HttpEntity<>(f, headers);

        try {
            ResponseEntity<MlDecision> res =
                    restTemplate.exchange(URL, HttpMethod.POST, entity, MlDecision.class);

            MlDecision body = res.getBody();
            return body != null ? body.getApproved() : "error_no_key";

        } catch (Exception ex) {
            return "error_service_down";
        }
    }
}
