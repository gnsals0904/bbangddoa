package com.example.Bbangddoa.controller;

import org.springframework.web.bind.annotation.*;

@RestController // 컨트롤러라고 알려주는 어노테이션
@RequestMapping("/api") // 여기로 들어올 때 사용할 path를 지정하는 어노테이션. 여기 있는 api들을 사용하기 위해 localhost:8080/api로 들어와야 한다.
public class GetController {

    String riot_api_key = System.getenv("riot_api_key");
    // 공백 처리
    String name = "hide on bush";
    String SummonerName = name.replaceAll(" ", "%20");
    String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+ SummonerName + "?api_key=" + riot_api_key;
    String testrequestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";

    @RequestMapping(method = RequestMethod.GET, path = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/") // localhost:8080/api/getRequestApi 로 들어오면 해당 getMethod api를 사용할 수 있다.
    public String getRequestApi(){
        return "getRequestApi";
    }



    @GetMapping("path-variable/{name}")
        public String pathVariable (@PathVariable String name){
        // 일반적으로 34번,35번,38번 line의 변수명(name)이 같아야한다.
        System.out.println("PathVariable : " + name);
        return name;
    }

}
