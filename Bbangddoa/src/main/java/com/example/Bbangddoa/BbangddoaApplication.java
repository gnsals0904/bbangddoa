package com.example.Bbangddoa;

import com.example.Bbangddoa.domain.Summoner;
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
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okio.ByteString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class BbangddoaApplication extends ListenerAdapter {

	public static void main(String[] args) throws LoginException {
		SpringApplication.run(BbangddoaApplication.class, args);

		ObjectMapper objectMapper = new ObjectMapper();

		Summoner summoner = null;	// DTO
		String riot_api_key = System.getenv("riot_api_key");
		// 공백 처리
		String name = "hide on bush";
		String SummonerName = name.replaceAll(" ", "%20");
		String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+ SummonerName + "?api_key=" + riot_api_key;

		try {
			HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
			HttpGet getRequest = new HttpGet(requestURL); //GET 메소드 URL 생성
			HttpResponse response = client.execute(getRequest);

			//Response 출력
			if (response.getStatusLine().getStatusCode() == 200) {
				ResponseHandler<String> handler = new BasicResponseHandler();
				String body = handler.handleResponse(response);
				summoner = objectMapper.readValue(body, Summoner.class);   // String to Object로 변환

			}




		String bot_token = System.getenv("bot_token");
		JDA jda = JDABuilder.createDefault(bot_token).build();
		// You can also add event listeners to the already built JDA instance
		// Note that some events may not be received if the listener is added after calling build()
		// This includes events such as the ReadyEvent
		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		jda.addEventListener(new BbangddoaApplication());

	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message msg = event.getMessage();
		if (msg.getContentRaw().equals("!ping"))
		{
			MessageChannel channel = event.getChannel();
			long time = System.currentTimeMillis();
			System.out.println("event :" + msg.getContentRaw());
			event.getChannel().sendMessage("pong!").queue();
		}

		if (event.isFromType(ChannelType.PRIVATE))
		{
			System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
					event.getMessage().getContentDisplay());
		}
		else
		{
			System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
					event.getTextChannel().getName(), event.getMember().getEffectiveName(),
					event.getMessage().getContentDisplay());
		}
	}


}
