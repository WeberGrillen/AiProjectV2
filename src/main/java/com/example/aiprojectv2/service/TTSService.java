package com.example.aiprojectv2.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class TTSService {

    @Value("${app.api-key}")
    private String apiKey;

    @Value("${app.tts-url}") // fx https://api.openai.com/v1/audio/speech
    private String ttsUrl;

    private final WebClient client;

    public TTSService() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10*1024*1024))
                .build();

        this.client = WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }


    public byte[] generateAudio(String text) {
        try {

            // 1. Split story into 4096-character chunks
            List<String> chunks = splitIntoChunks(text, 2000);
            List<byte[]> audioChunks = new ArrayList<>();

            for (String chunk : chunks) {

                Mono<byte[]> audioMono = client.post()
                        .uri(ttsUrl)
                        .headers(h -> h.setBearerAuth(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.valueOf("audio/mpeg"))
                        .bodyValue(
                                """
                                {
                                    "model": "gpt-4o-mini-tts",
                                    "voice": "onyx"
                                    "input": "%s"
                                }
                                """.formatted(chunk)
                        )
                        .retrieve()
                        .bodyToMono(byte[].class);

                audioChunks.add(audioMono.block());
            }
            // 3. Merge all MP3 chunks into a single byte array
            return mergeMp3Files(audioChunks);

        } catch (Exception e) {
            throw new RuntimeException("StoryCraft was not able to generate TTS with OpenAI", e);
        }

    }

    // Kombiner video + TTS-lyd
    public Path addTtsToVideo(String text, Path inputVideo, Path outputVideo) throws IOException {
        Path tempDir = Path.of("src/main/resources/temp");
        if (!Files.exists(tempDir)) Files.createDirectories(tempDir);

        Path audioFile = tempDir.resolve("tts_audio.mp3");
        Files.write(audioFile, generateAudio(text));

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", inputVideo.toAbsolutePath().toString().replace("\\","/"),
                "-i", audioFile.toAbsolutePath().toString().replace("\\","/"),
                "-c:v", "copy",
                "-c:a", "aac",
                "-map", "0:v:0",
                "-map", "1:a:0",
                "-shortest",
                outputVideo.toAbsolutePath().toString().replace("\\","/")
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return outputVideo;
    }

    private List<String> splitIntoChunks(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            // cut at last space for cleaner sentences
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) end = lastSpace;
            }
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }

    // Helper: Merge multiple MP3 byte arrays
    private byte[] mergeMp3Files(List<byte[]> mp3Chunks) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (byte[] chunk : mp3Chunks) {
                out.write(chunk);
            }
            return out.toByteArray();
        }
    }
}
