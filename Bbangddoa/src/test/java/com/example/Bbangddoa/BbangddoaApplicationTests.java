package com.example.Bbangddoa;

import com.example.Bbangddoa.domain.LeagueEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.Bbangddoa.BbangddoaApplication.riot_api_key;

@SpringBootTest
class BbangddoaApplicationTests {

	public void searchSummonerLeagueInfo(String key, String basicRequestURL, String summonerEncryptedId){
		String requestURL = basicRequestURL + summonerEncryptedId + "?api_key=" + key;
		List<LeagueEntry> leagueEntry = null;
		try {
			String USER_AGENT = "Mozilla/5.0";
			URL url = new URL(requestURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET"); // optional default is GET
			con.setRequestProperty("User-Agent", USER_AGENT); // add request header
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close(); // print result
			System.out.println("LeagueInfo HTTP 응답 코드 : " + responseCode);
			System.out.println("HTTP body : " + response.toString());
			ObjectMapper objectMapper = new ObjectMapper();
			leagueEntry = objectMapper.readValue(response.toString(), new TypeReference<List<LeagueEntry>>() {});
			System.out.println("queueType :" + leagueEntry.get(0).getQueueType());
			System.out.println("Summoner Tier :"+ leagueEntry.get(0).getTier()+leagueEntry.get(1).getTier());
			if(leagueEntry.get(1).getQueueType() == "RANKED_TFT_PAIRS"){
				System.out.println("pass");
			} else{
				System.out.println("..no");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Test
	void 롤체데이터가먼저들어올까나중에들어올까() {
		searchSummonerLeagueInfo(riot_api_key,"https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/","x2XJmA-PFp55zlKdkEXszRpFmq2pa9rWqAhk7yESsC8SPg");
	}

}
