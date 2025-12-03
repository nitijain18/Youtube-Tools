package com.youtubeTools.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.youtubeTools.Model.SearchVideo;
import com.youtubeTools.Model.Video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class YoutubeService {

    private final WebClient.Builder webClientBuilder;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base.url}")
    private String baseUrl;

    @Value("${youtube.api.max.related.videos:5}")
    private int maxRelatedVideos;

    public SearchVideo searchVideos(String videoTitle) {
    List<String> videoIds = searchForVideoIds(videoTitle);

    if (videoIds.isEmpty()) {
     return SearchVideo.builder()
     .primaryVideo(null)
     .relatedVideos(Collections.emptyList())
     .build();
    }

    String primaryVideoId = videoIds.get(0);
    List<String> relatedVideoIds = videoIds.size() > 1
        ? videoIds.subList(1, Math.min(videoIds.size(), 1 + maxRelatedVideos))
        : Collections.emptyList();

    Video primaryVideo = getVideoById(primaryVideoId);
    List<Video> relatedVideos = new ArrayList<>();

    for (String id : relatedVideoIds) {
        Video video = getVideoById(id);
        if (video != null) {
          relatedVideos.add(video);
        }
    }

    return SearchVideo.builder()
     .primaryVideo(primaryVideo)
     .relatedVideos(relatedVideos)
     .build();
    }

    private List<String> searchForVideoIds(String videoTitle) {
      try {
    int requestCount = 1 + Math.max(0, maxRelatedVideos);

    SearchApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
    .get()
    .uri(uriBuilder -> uriBuilder.path("/search")
    .queryParam("part", "snippet")
    .queryParam("q", videoTitle)
    .queryParam("type", "video")
    .queryParam("maxResults", requestCount)
    .queryParam("key", apiKey)
    .build())
    .retrieve()
    .bodyToMono(SearchApiResponse.class)
    .block();

    if (response == null || response.items == null) {
        return Collections.emptyList();
    }

    List<String> videoIds = new ArrayList<>();
    for (SearchItem item : response.items) {
        if (item != null && item.id != null && item.id.videoId != null) {
            videoIds.add(item.id.videoId);
        }
            }
            return videoIds;
        } catch (Exception ex) {
            log.error("Error searching for video ids", ex);
            return Collections.emptyList();
        }
    }

    private Video getVideoById(String videoId) {
      try {
        VideoApiResponse response = webClientBuilder.baseUrl(baseUrl).build()
        .get()
        .uri(uriBuilder -> uriBuilder.path("/videos")
        .queryParam("part", "snippet")
        .queryParam("id", videoId)
        .queryParam("key", apiKey)
        .build())
        .retrieve()
        .bodyToMono(VideoApiResponse.class)
        .block();

        if (response == null || response.items == null || response.items.isEmpty()) {
            return null;
        }

        Snippet snippet = response.items.get(0).snippet;
        if (snippet == null) return null;
        return Video.builder()
        .id(videoId)
        .channelTitle(snippet.getChannelTitle())
        .title(snippet.getTitle())
        .tags(snippet.getTags() == null ? Collections.emptyList() : snippet.getTags())
        .build();

        } catch (Exception ex) {
            log.error("Error fetching video details for id: {}", videoId, ex);
            return null;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SearchApiResponse {
        List<SearchItem> items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SearchItem {
        ItemId id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ItemId {
        String videoId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class VideoApiResponse {
        List<VideoItem> items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class VideoItem {
        Snippet snippet;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Snippet {
        private String title;
        private String description;
        private String channelTitle;
        private String publishedAt;
        private List<String> tags;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getChannelTitle() {
            return channelTitle;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public List<String> getTags() {
            return tags;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Thumbnails {
        @JsonProperty("maxres")
        Thumbnail maxres;
        @JsonProperty("high")
        Thumbnail high;
        @JsonProperty("medium")
        Thumbnail medium;
        @JsonProperty("default")
        Thumbnail _default;

        public String getBestThumbnailUrl() {
            if (maxres != null && maxres.url != null) return maxres.url;
            if (high != null && high.url != null) return high.url;
            if (medium != null && medium.url != null) return medium.url;
            return (_default != null && _default.url != null) ? _default.url : "";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Thumbnail {
        String url;
    }
}
