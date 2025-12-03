package com.youtubeTools.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.youtubeTools.Service.thumbnailService;



@Controller
public class thumbnailController {

    @Autowired
    thumbnailService service;

    @GetMapping("/thumbnail")
    public String getThumbnail(){
        return "Thumbnails";
    }

    @PostMapping("/get-thumbnail")
    public String showThumbnail(@RequestParam ("videoUrlOrId") String videoUrlOrId ,Model model){
        String videoId= service.extractVideoId(videoUrlOrId);
        if(videoId ==  null){
            model.addAttribute("error" , "Invalid utl");
            return "Thumbnails";
        }
        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId
        +"/maxresdefault.jpg";
        model.addAttribute("thumbnailUrl" , thumbnailUrl);
       return "Thumbnails";
    }
}
