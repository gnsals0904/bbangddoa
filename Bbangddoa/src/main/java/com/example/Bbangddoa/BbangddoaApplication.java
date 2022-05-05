package com.example.Bbangddoa;

import com.example.Bbangddoa.domain.LeagueEntry;
import com.example.Bbangddoa.domain.Summoner;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SpringBootApplication
public class BbangddoaApplication extends ListenerAdapter {
	static String riot_api_key = System.getenv("riot_api_key");
	static String bot_token = System.getenv("bot_token");
	public static void main(String[] args) throws LoginException {
		SpringApplication.run(BbangddoaApplication.class, args);
		JDABuilder builder = JDABuilder.createDefault(bot_token);
		builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT,"빵또아는 빵안에 아이스크림이 들어가 있다고 주장하는 중"));
		// JDA jda = JDABuilder.createDefault(bot_token).build();

		// You can also add event listeners to the already built JDA instance
		// Note that some events may not be received if the listener is added after calling build()
		// This includes events such as the ReadyEvent
		JDA jda = builder.build();
		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		jda.addEventListener(new BbangddoaApplication());

	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message msg = event.getMessage();
		if (msg.getContentRaw().equals("!ping")) {
			MessageChannel channel = event.getChannel();
			long time = System.currentTimeMillis();
			System.out.println("event :" + msg.getContentRaw());
			event.getChannel().sendMessage("pong!").queue();
		}
		else if(msg.getContentRaw().equals("!전적검색")) {
			String name = "hide on bush";
			Summoner summonerInfo = null;
			List<LeagueEntry> summonerLeagueInfo = null;
			String SummonerName = name.replaceAll(" ", "%20");
			String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
			summonerInfo = searchSummonerInfo(riot_api_key, requestURL,"vtz");
			requestURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
			summonerLeagueInfo = searchSummonerLeagueInfo(riot_api_key, requestURL, summonerInfo.getId());
			event.getChannel().sendMessage("검색한 소환사의 레벨은 "+summonerInfo.getSummonerLevel()+" 이고 랭크 티어는 "+summonerLeagueInfo.get(0).getTier()+" 입니다.").queue();

		}
		if (event.isFromType(ChannelType.PRIVATE)) {
			System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
					event.getMessage().getContentDisplay());
		}
		else {
			System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
					event.getTextChannel().getName(), event.getMember().getEffectiveName(),
					event.getMessage().getContentDisplay());
		}
	}


	//rest api 로 수정 필요
	//mvc 패턴 적용 필요
	public Summoner searchSummonerInfo(String key, String basicRequestURL, String summonerName){
		String requestURL = basicRequestURL + summonerName + "?api_key=" + key;
		Summoner summoner = null;
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
			System.out.println("searchSummonerInfo HTTP 응답 코드 : " + responseCode);
			System.out.println("HTTP body : " + response.toString());
			ObjectMapper objectMapper = new ObjectMapper();
			summoner = objectMapper.readValue(response.toString(), Summoner.class);
			System.out.println("Summoner Level :"+summoner.getSummonerLevel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return summoner;
	}

	public List<LeagueEntry> searchSummonerLeagueInfo(String key, String basicRequestURL, String summonerEncryptedId){
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
			System.out.println("Summoner Tier :"+ leagueEntry.get(0).getTier());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return leagueEntry;
	}

}
