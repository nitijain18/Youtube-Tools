package com.youtubeTools.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.youtubeTools.Model.SearchVideo;
import com.youtubeTools.Service.YoutubeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeTagsController {

 private final YoutubeService youtubeService;

@Value("${youtube.api.key}")
private String apiKey;

private boolean isApiKeyConfigured() {
    return apiKey != null && !apiKey.isEmpty();
}

@PostMapping("/search")
public String videoTags(@RequestParam("videoTitle") String videoTitle, Model model) {

    if (!isApiKeyConfigured()) {
        model.addAttribute("error", "API not configured");
        return "Home";
    }

    if (videoTitle == null || videoTitle.trim().isEmpty()) {
        model.addAttribute("error", "Video title required");
        return "Home";
    }

    try {
        SearchVideo result = youtubeService.searchVideos(videoTitle.trim());
        model.addAttribute("primaryVideo", result.getPrimaryVideo());
        model.addAttribute("relatedVideos", result.getRelatedVideos());
        return "Home";
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage() == null ? "Unknown error" : e.getMessage());
        return "Home";
    }
    }
}
