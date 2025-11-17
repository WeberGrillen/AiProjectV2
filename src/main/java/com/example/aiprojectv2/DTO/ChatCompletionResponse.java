package com.example.aiprojectv2.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private Usage Usage;
    private List<Choice> choices;


    @Getter
    @Setter
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

    }

    @Getter
    @Setter
    public static class Choice {
        private Message message;
        private String finish_reason;
        private int index;
    }

    @Getter
    @Setter
    public static class Message {
        private String role;
        private String content;
    }
}
