// package com.backend.forensic_backend.controller;
// // import java.io.BufferedReader;
// // import java.io.InputStreamReader;

// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/recognize")
// public class RecognitionController {

//     @PostMapping
//     public String recognize(@RequestParam String imagePath) {

//         // call python script
//         // try {
//         //     Process p = Runtime.getRuntime().exec("python recognize.py " + imagePath);
//         //     BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

//         //     return reader.readLine();

//         // } catch (Exception e) {
//         //     return "Error in recognition";
//         // }

//         return """
//                 [
//         {"file":"img1.jpg","name":"John Doe","confidence":0.95},
//         {"file":"img2.jpg","name":"Jane Smith","confidence":0.90}
//         ]
//                 """;
//     }
// }

package com.backend.forensic_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.backend.forensic_backend.model.Result;
import com.backend.forensic_backend.repository.ResultRepository;
import com.backend.forensic_backend.model.Sketch;
import com.backend.forensic_backend.repository.SketchRepository;
import org.springframework.http.*;
import java.util.*;

@RestController
@RequestMapping("/api/recognize")
public class RecognitionController {

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private SketchRepository sketchRepo;

    @PostMapping
public List<Result> recognize(@RequestParam String imagePath,
                             @RequestParam Long sketchId) {

    // 🔥 Get sketch from DB
    Sketch sketch = sketchRepo.findById(sketchId).orElseThrow();

    // 🔥 Prepare API call
    RestTemplate restTemplate = new RestTemplate();

    String url = "http://127.0.0.1:8000/recognize";

    // Request body for Python
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("imagePath", imagePath);
    requestBody.put("topK", 5);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(requestBody, headers);

    // 🔥 Call Python API
    ResponseEntity<Map> response =
            restTemplate.postForEntity(url, request, Map.class);

    List<Map<String, Object>> pythonResults =
            (List<Map<String, Object>>) response.getBody().get("results");

    List<Result> results = new ArrayList<>();

    // 🔥 Save results to DB
    for (Map<String, Object> r : pythonResults) {

        Result result = new Result();
        result.setSketch(sketch);
        result.setMatchedPerson((String) r.get("name"));
        result.setMatchedFile((String) r.get("file"));
        result.setConfidence(
                Double.parseDouble(r.get("confidence").toString())
        );

        resultRepo.save(result);
        results.add(result);
    }

    return results;
}
}