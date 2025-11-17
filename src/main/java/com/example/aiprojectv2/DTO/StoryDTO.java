package com.example.aiprojectv2.DTO;

public class StoryDTO {
    private String title;
    private String story;
    private String tldr;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTldr() {
        return tldr;
    }

    public void setTldr(String tldr) {
        this.tldr = tldr;
    }

    @Override
    public String toString() {
        return "StoryDTO{" +
                "title='" + title + '\'' +
                ", story='" + story + '\'' +
                ", tldr='" + tldr + '\'' +
                '}';
    }
}
