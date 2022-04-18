package com.example.Bbangddoa;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class BbangddoaApplication extends ListenerAdapter {

	public static void main(String[] args) throws LoginException {
		SpringApplication.run(BbangddoaApplication.class, args);
		String bot_token = "OTYyMjY2NDM3MTE4OTkyNDQ1.YlFCdA.14pfQyOuH3aqWcZi6Klzvo44akU";
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
