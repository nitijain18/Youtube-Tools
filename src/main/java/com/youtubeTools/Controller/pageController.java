package com.youtubeTools.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class pageController {

    @GetMapping({"/" , "home"})
    public String home(){
        return "Home";
    }
    
    @GetMapping("/video-details")
    public String videoDetails(){
        return "VideoDetails";
    }
}
