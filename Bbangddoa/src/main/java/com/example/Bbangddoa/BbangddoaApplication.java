package com.example.Bbangddoa;

import com.example.Bbangddoa.controller.ReceiveMessageController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class BbangddoaApplication extends ListenerAdapter {
	static String bot_token = System.getenv("bot_token");
	public static void main(String[] args) throws LoginException, InterruptedException {
		SpringApplication.run(BbangddoaApplication.class, args);
		JDABuilder builder = JDABuilder.createDefault(bot_token);
		builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT,"빵또아는 빵안에 아이스크림이 들어가 있다고 주장하는 중"));
		// JDA jda = JDABuilder.createDefault(bot_token).build();

		// You can also0 add event listeners to the already built JDA instance
		// Note that some events may not be received if the listener is added after calling build()
		// This includes events such as the ReadyEvent
		JDA jda = builder.build();


		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		jda.addEventListener(new ReceiveMessageController());


	}

}
