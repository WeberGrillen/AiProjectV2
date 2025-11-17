package com.example.aiprojectv2.controller;

import com.example.aiprojectv2.DTO.StoryDTO;
import com.example.aiprojectv2.service.OpenAiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/story")
@CrossOrigin(origins = "*") // Make so frontend can call backend
public class StoryController {

    private final OpenAiService openAiService;

    public StoryController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping
    public ResponseEntity<?> generateStory(@RequestBody String prompt) {
        try {
            return ResponseEntity.ok().body(openAiService.generateStory(prompt));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
