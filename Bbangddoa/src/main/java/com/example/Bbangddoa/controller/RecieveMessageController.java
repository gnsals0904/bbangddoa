package com.example.Bbangddoa.controller;

import com.example.Bbangddoa.domain.LeagueEntry;
import com.example.Bbangddoa.domain.Summoner;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class RecieveMessageController extends ListenerAdapter {
    static String riot_api_key = System.getenv("riot_api_key");
    int Rank_solo_key = 0; // riot api send TFT Rank info & Solo Rank info Randomly
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        User user = event.getAuthor();
        TextChannel channel = event.getTextChannel();
        Message msg = event.getMessage();
        if(user.isBot()) return;
        if(msg.getContentRaw().charAt(0) == '!'){
            String[] msg_array = msg.getContentRaw().substring(1).split(" ");
            if(msg_array.length <= 0) return;
            if (msg_array[0].equalsIgnoreCase("ping")) {
                long time = System.currentTimeMillis();
                System.out.println("event :" + msg.getContentRaw());
                event.getChannel().sendMessage("pong!").queue();
            }
            if(msg_array[0].equalsIgnoreCase("전적검색")) {
                if (msg_array.length == 1){
                    channel.sendMessage("전적검색이라고 입력한 후 띄어쓴다음 닉네임을 입력해주세요!").queue();
                    return;
                }
                else if (msg_array.length != 2) {
                    channel.sendMessage("띄어쓰기 없이 입력해야해요!").queue();
                    return;
                }
                // String name = "hide on bush";
                // String SummonerName = name.replaceAll(" ", "%20");
                String summonerName = msg_array[1];
                System.out.println("summonerName : "+ summonerName);
                Summoner summonerInfo = null;
                List<LeagueEntry> summonerLeagueInfo = null;
                String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
                summonerInfo = searchSummonerInfo(riot_api_key, requestURL,summonerName);
                requestURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
                summonerLeagueInfo = searchSummonerLeagueInfo(riot_api_key, requestURL, summonerInfo.getId());
                event.getChannel().sendMessage("검색한 소환사의 레벨은 "+summonerInfo.getSummonerLevel()+" 이고 랭크 티어는 "+summonerLeagueInfo.get(Rank_solo_key).getTier()+" 입니다.").queue();
            }
            if(msg_array[0].equalsIgnoreCase("clear")) {
                msg.delete().queue();
                if (msg_array.length != 2) return;
                int count = 1;
                try {
                    count = Integer.parseInt(msg_array[1]);
                } catch(Exception e){
                    channel.sendMessage("정수를 입력해야합니다!").queue();
                    return;
                }
                if(count < 1 | count > 100){
                    channel.sendMessage("1에서 100사이의 정수를 입력해야합니다!").queue();
                    return;
                }
                MessageHistory mh = new MessageHistory(channel);
                List<Message> msgs = mh.retrievePast(count).complete();
                channel.deleteMessages(msgs).complete();
                channel.sendMessage(count + " 개의 메시지를 제거했어요!").queue();


            }
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
            StringBuilder response = new StringBuilder();
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
            StringBuilder response = new StringBuilder();
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
            if(Objects.equals(leagueEntry.get(0).getQueueType(), "RANKED_TFT_PAIRS")){
                Rank_solo_key = 1;
            } else{
                Rank_solo_key = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leagueEntry;
    }
}
