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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.backend.forensic_backend.model.Result;
import com.backend.forensic_backend.repository.ResultRepository;
import com.backend.forensic_backend.model.Sketch;
import com.backend.forensic_backend.repository.SketchRepository;

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

        // 🔥 get sketch from DB
        Sketch sketch = sketchRepo.findById(sketchId).orElseThrow();

        List<Result> results = new ArrayList<>();

        // 🔥 MOCK DATA (replace with Python later)
        Result r1 = new Result();
        r1.setSketch(sketch);
        r1.setMatchedPerson("John Doe");
        r1.setMatchedFile("img1.jpg");
        r1.setConfidence(0.95);

        Result r2 = new Result();
        r2.setSketch(sketch);
        r2.setMatchedPerson("Jane Smith");
        r2.setMatchedFile("img2.jpg");
        r2.setConfidence(0.90);

        // 🔥 SAVE TO DB
        resultRepo.save(r1);
        resultRepo.save(r2);

        results.add(r1);
        results.add(r2);

        return results;
    }
}