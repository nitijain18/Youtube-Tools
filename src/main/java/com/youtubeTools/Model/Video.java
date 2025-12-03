package com.youtubeTools.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    private String id;
    private String channelTitle;
    private String title;
    private List<String>tags;
}
