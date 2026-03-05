package com.courtroom.controller;

import com.courtroom.dto.TrialCreateRequest;
import com.courtroom.dto.TrialMessageRequest;
import com.courtroom.entity.MockTrial;
import com.courtroom.entity.TrialMessage;
import com.courtroom.repository.MockTrialRepository;
import com.courtroom.repository.TrialMessageRepository;
import com.courtroom.service.MockTrialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trials")
@CrossOrigin(origins = "http://localhost:5173")
public class MockTrialController {

    @Autowired
    private MockTrialRepository trialRepository;

    @Autowired
    private TrialMessageRepository messageRepository;

    @Autowired
    private MockTrialService trialService;

    @PostMapping
    public ResponseEntity<MockTrial> createTrial(@RequestBody TrialCreateRequest request) {
        MockTrial trial = new MockTrial();
        trial.setTitle(request.getTitle());
        trial.setCaseDescription(request.getCaseDescription());
        trial.setUserRole(MockTrial.UserRole.valueOf(request.getUserRole()));
        trial = trialRepository.save(trial);

        // Generate opening statement from judge
        trialService.processUserMessage(trial, "Trial begins", "STATEMENT");

        return ResponseEntity.ok(trial);
    }

    @GetMapping
    public ResponseEntity<List<MockTrial>> getAllTrials() {
        return ResponseEntity.ok(trialRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTrial(@PathVariable Long id) {
        MockTrial trial = trialRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trial not found: " + id));
        List<TrialMessage> messages = messageRepository.findByTrialIdOrderByCreatedAtAsc(id);

        Map<String, Object> result = new HashMap<>();
        result.put("trial", trial);
        result.put("messages", messages);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable Long id,
            @RequestBody TrialMessageRequest request) {
        MockTrial trial = trialRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trial not found: " + id));

        // Save user message
        TrialMessage userMsg = new TrialMessage();
        userMsg.setTrial(trial);
        userMsg.setSenderRole("USER");
        userMsg.setContent(request.getContent());
        userMsg.setMessageType(TrialMessage.MessageType.valueOf(
            request.getMessageType() != null ? request.getMessageType() : "STATEMENT"));
        userMsg = messageRepository.save(userMsg);

        // Generate AI agent responses
        List<TrialMessage> agentResponses = trialService.processUserMessage(
            trial, request.getContent(), request.getMessageType());

        Map<String, Object> result = new HashMap<>();
        result.put("userMessage", userMsg);
        result.put("agentResponses", agentResponses);
        result.put("currentStage", trial.getCurrentStage());
        result.put("trialStatus", trial.getStatus());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/stage")
    public ResponseEntity<MockTrial> advanceStage(@PathVariable Long id) {
        MockTrial trial = trialRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trial not found: " + id));

        String nextStage = switch (trial.getCurrentStage()) {
            case "OPENING" -> "EVIDENCE";
            case "EVIDENCE" -> "CROSS_EXAMINATION";
            case "CROSS_EXAMINATION" -> "CLOSING";
            case "CLOSING" -> "VERDICT";
            default -> trial.getCurrentStage();
        };

        trial.setCurrentStage(nextStage);
        if ("VERDICT".equals(nextStage)) {
            trial.setStatus(MockTrial.TrialStatus.COMPLETED);
        }

        return ResponseEntity.ok(trialRepository.save(trial));
    }
}
