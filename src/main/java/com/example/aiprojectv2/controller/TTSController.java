package com.example.aiprojectv2.controller;

import com.example.aiprojectv2.service.TTSService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/tts")
public class TTSController {

    private final TTSService ttsService;

    public TTSController(TTSService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping
    public ResponseEntity<byte[]> generateTTS (@RequestBody Map<String, String> request) {
        String storyText = request.get("storyText");
        if (storyText == null || storyText.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        byte[] audioBytes = ttsService.generateAudio(storyText);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .header("Content-Disposition", "inline; filename=\"story.mp3\"")
                .body(audioBytes);
    }

    @PostMapping("/video") // fx /api/tts/video
    public ResponseEntity<byte[]> addTtsToVideo(@RequestBody Map<String, String> request) throws IOException {
        String videoName = request.get("videoName");
        String storyText = request.get("storyText");

        if (videoName == null || videoName.isEmpty() || storyText == null || storyText.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 1. Paths til input/output videoer
        Path inputVideo = Path.of("src/main/resources/static/video/MinecraftParkour.mp4").toAbsolutePath();
        Path outputVideo = Path.of("src/main/resources/static/videos/output-MinecraftParkour.mp4").toAbsolutePath();
        // 2. Læg TTS på video
        Path finalVideo = ttsService.addTtsToVideo(storyText, inputVideo, outputVideo);

        // 3. Returnér video som download
        try {
            byte[] videoBytes = Files.readAllBytes(finalVideo);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + finalVideo.getFileName() + "\"")
                    .body(videoBytes);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse video med TTS", e);
        }
    }
}
