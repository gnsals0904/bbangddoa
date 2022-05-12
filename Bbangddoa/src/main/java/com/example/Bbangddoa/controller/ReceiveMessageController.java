package com.example.Bbangddoa.controller;

import com.example.Bbangddoa.domain.ChampionMasteryScore;
import com.example.Bbangddoa.domain.LeagueEntry;
import com.example.Bbangddoa.domain.Summoner;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class ReceiveMessageController extends ListenerAdapter {
    static String riot_api_key = System.getenv("riot_api_key");
    int Rank_solo_key = 0; // riot api send TFT Rank info & Solo Rank info Randomly
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        User user = event.getAuthor();
        TextChannel channel = event.getTextChannel();
        Message msg = event.getMessage();
        EmbedBuilder embedBuilder = new EmbedBuilder();
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
                String summonerName = msg_array[1];
                System.out.println("summonerName : "+ summonerName);
                Summoner summonerInfo = null;
                List<LeagueEntry> summonerLeagueInfo = null;
                String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
                summonerInfo = searchSummonerInfo(riot_api_key, requestURL,summonerName);
                requestURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
                summonerLeagueInfo = searchSummonerLeagueInfo(riot_api_key, requestURL, summonerInfo.getId());
                requestURL = "https://kr.api.riotgames.com/lol/champion-mastery/v4/scores/by-summoner/";
                int championScore = 0;
                championScore = Integer.parseInt(searchSummonerScore(riot_api_key, requestURL, summonerInfo.getId()));
                System.out.println("championScore :"+championScore );
                embedBuilder.setTitle("전적 검색");
                embedBuilder.setColor(new Color(0x44c0e9));
                embedBuilder.setDescription(summonerName+"님의 League of Legend 계정 정보입니다!");
                embedBuilder.addField("레벨",""+summonerInfo.getSummonerLevel(),true);
                embedBuilder.addField("티어",summonerLeagueInfo.get(Rank_solo_key).getTier()+"  "+summonerLeagueInfo.get(Rank_solo_key).getRank(),true);
                embedBuilder.addField("LP",""+summonerLeagueInfo.get(Rank_solo_key).getLeaguePoints(),true);
                embedBuilder.addField("승/패",""+summonerLeagueInfo.get(Rank_solo_key).getWins()+"/"+summonerLeagueInfo.get(Rank_solo_key).getLosses(),true);
                double winRate = (double) summonerLeagueInfo.get(Rank_solo_key).getWins() / (summonerLeagueInfo.get(Rank_solo_key).getWins() + summonerLeagueInfo.get(Rank_solo_key).getLosses()) * 100.0;
                winRate = Math.round(winRate * 10)/10.0;
                embedBuilder.addField("승률",""+winRate+"%",true);
                switch (summonerLeagueInfo.get(Rank_solo_key).getTier()) {
                    case "BRONZE":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/UoVCn/btqX82Nv6uf/6Wk63xJmnTVtW968iSurc0/img.png");
                        championScore = championScore + 30;
                        break;
                    case "SILVER":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/dxjmpT/btqYgGWXdq5/YyaujId4AjzIRu7SEUoP71/img.png");
                        championScore = championScore + 50;
                        break;
                    case "GOLD":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/bUXEUo/btqX812cCZG/TwooRuWWtDuxo2xfvB2KW1/img.png");
                        championScore = championScore + 80;
                        break;
                    case "PLATINUM":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/wasjB/btqYfVUjFmK/KD9Vw3T7WZ7qpv7sELuLU0/img.png");
                        championScore = championScore + 300;
                        break;
                    case "DIAMOND":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/c90MX3/btqX319ryJc/8R0TA5BsNtxMWiQINcggr0/img.png");
                        championScore = championScore + 600;
                        break;
                    case "MASTER":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/bwCoOa/btqX32N6lBg/euKVfTIZgi2oVmUHRx0XAK/img.png");
                        championScore = championScore + 900;
                        break;
                    case "IRON":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/8yeAR/btqX83lmfND/Q0vCmYQx09DLQX1dbGvw61/img.png");
                        championScore = championScore + 10;
                        break;
                    case "CHALLENGER":
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/daNR6z/btqYbJNFasE/rbH16SlWukVsCcjgefsWC1/img.png");
                        championScore = championScore + 2000;
                        break;
                    default:
                        embedBuilder.setThumbnail("https://blog.kakaocdn.net/dn/bsJT7b/btqYhyYDWap/HUTD09WchC9qZW8r1p1QB0/img.png");
                        break;
                }
                if(championScore < 100){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 삐약삐약 병아리시네요!",false);
                } else if(championScore < 200){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 병아리는 아니지만.. 아직 뉴비입니다!",false);
                }
                else if(championScore < 500){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 친구들하고 같이 할 정도는 되겠네요!",false);
                }
                else if(championScore < 800){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 친구들하고 하실때 캐리도 가끔 할 수 있겠어요!",false);
                }
                else if(championScore < 1000){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 어디가서 롤 좀 해봤다고 하실 수 있겠네요!",false);
                }
                else if(championScore < 1200){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점.. 챔피언에 대한 숙련도가 높던지, 티어가 높던지 어느쪽이시든 잘하시네요!",false);
                }
                else if(championScore < 1400){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 숙련도면 숙련도, 티어면 티어 모자란 부분이 없으세요...대단하십니다!",false);
                }
                else if(championScore < 1600){
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 거의 프로게이머 수준이에요, 솔랭돌리시면 프로와 매칭될 수 있겠어요!",false);
                }
                else {
                    embedBuilder.addField("뉴비판독기",""+championScore+"점으로 롤이 이제 숨쉬는 것만큼 쉬운 경지에요. 혹시 프로게이머?",false);
                }


                event.getChannel().sendMessage(embedBuilder.build()).queue();
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
            if(msg_array[0].equalsIgnoreCase("help")){
                embedBuilder.setTitle("빵또아 사용 설명서");
                embedBuilder.setColor(new Color(0xdb4bd4));
                embedBuilder.addField("0. !help","빵또아가 사용설명서를 보여줍니다.",false);
                embedBuilder.addField("1. !clear 삭제하고싶은 메시지 수","빵또아가 입력한 메시지 수만큼 채널에 있는 메시지를 삭제합니다.",false);
                embedBuilder.addField("2. !ping","빵또아와 핑퐁게임을 합니다.",false);
                embedBuilder.addField("3. !전적검색 닉네임","빵또아가 입력한 닉네임에 해당하는 전적을 검색해서 알려줍니다.",false);
                embedBuilder.setThumbnail("https://cdn-icons-png.flaticon.com/512/4403/4403455.png");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
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

    public String searchSummonerScore(String key, String basicRequestURL, String summonerEncryptedId){
        String requestURL = basicRequestURL + summonerEncryptedId + "?api_key=" + key;
        StringBuilder response = new StringBuilder();
        try {
            String USER_AGENT = "Mozilla/5.0";
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET"); // optional default is GET
            con.setRequestProperty("User-Agent", USER_AGENT); // add request header
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close(); // print result
            System.out.println("searchSummonerScore HTTP 응답 코드 : " + responseCode);
            System.out.println("HTTP body : " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
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
