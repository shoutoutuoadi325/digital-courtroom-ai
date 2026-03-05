package com.courtroom.controller;

import com.courtroom.entity.LegalCorpus;
import com.courtroom.repository.LegalCorpusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/corpus")
@CrossOrigin(origins = "http://localhost:5173")
public class LegalCorpusController {

    @Autowired
    private LegalCorpusRepository corpusRepository;

    @GetMapping
    public ResponseEntity<List<LegalCorpus>> getAllCorpus() {
        return ResponseEntity.ok(corpusRepository.findAll());
    }

    @GetMapping("/verified")
    public ResponseEntity<List<LegalCorpus>> getVerifiedCorpus() {
        return ResponseEntity.ok(corpusRepository.findByVerifiedTrue());
    }

    @PostMapping
    public ResponseEntity<LegalCorpus> addCorpus(@RequestBody LegalCorpus corpus) {
        return ResponseEntity.ok(corpusRepository.save(corpus));
    }
}
