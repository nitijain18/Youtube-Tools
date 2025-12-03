package com.youtubeTools.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVideo {

    private Video primaryVideo;
    private List<Video> relatedVideos;
}
