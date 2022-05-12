package com.example.Bbangddoa.controller;

import com.example.Bbangddoa.domain.ChampionMastery;
import com.example.Bbangddoa.domain.LeagueEntry;
import com.example.Bbangddoa.domain.Summoner;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import java.util.ArrayList;
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
                List<ChampionMastery> championMasteries = null;
                String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
                summonerInfo = searchSummonerInfo(riot_api_key, requestURL,summonerName);
                requestURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";
                summonerLeagueInfo = searchSummonerLeagueInfo(riot_api_key, requestURL, summonerInfo.getId());
                requestURL = "https://kr.api.riotgames.com/lol/champion-mastery/v4/scores/by-summoner/";
                int championScore = 0;
                championScore = Integer.parseInt(searchSummonerScore(riot_api_key, requestURL, summonerInfo.getId()));
                requestURL = "https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/";
                championMasteries = searchChampionMastery(riot_api_key, requestURL, summonerInfo.getId());
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
                List<String> masteryChampionName = new ArrayList<>();
                System.out.println("championName :" +getChampionName((championMasteries.get(0).getChampionId()))+getChampionName((championMasteries.get(1).getChampionId()))+getChampionName((championMasteries.get(2).getChampionId())));
                masteryChampionName.add(getChampionName((championMasteries.get(0).getChampionId())).toString());
                masteryChampionName.add(getChampionName((championMasteries.get(1).getChampionId())).toString());
                masteryChampionName.add(getChampionName((championMasteries.get(2).getChampionId())).toString());
                embedBuilder.addField("아래의 챔피언들에 능숙하시네요!",masteryChampionName.get(0)+"\t"+championMasteries.get(0).getChampionPoints()+"점\n"+masteryChampionName.get(1)+"\t"+championMasteries.get(1).getChampionPoints()+"점\n"+masteryChampionName.get(2)+"\t"+championMasteries.get(2).getChampionPoints()+"점",true);
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

    public List<ChampionMastery> searchChampionMastery(String key, String basicRequestURL, String summonerEncryptedId){
        String requestURL = basicRequestURL + summonerEncryptedId + "?api_key=" + key;
        List<ChampionMastery> championMasteries = null;
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
            System.out.println("searchChampionMastery HTTP 응답 코드 : " + responseCode);
            System.out.println("HTTP body : " + response.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            championMasteries = objectMapper.readValue(response.toString(), new TypeReference<List<ChampionMastery>>() {});
            System.out.println("champion id list :" + championMasteries.get(0).getChampionId()+", "+championMasteries.get(1).getChampionId()+", "+championMasteries.get(2).getChampionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return championMasteries;
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

    public String getChampionName(long id){
        switch((int) id) {
            case 266:
                return "Aatrox";
            case 412:
                return "Thresh";
            case 23:
                return "Tryndamere";
            case 79:
                return "Gragas";
            case 69:
                return "Cassiopeia";
            case 136:
                return "Aurelion Sol";
            case 13:
                return "Ryze";
            case 78:
                return "Poppy";
            case 14:
                return "Sion";
            case 1:
                return "Annie";
            case 202:
                return "Jhin";
            case 43:
                return "Karma";
            case 111:
                return "Nautilus";
            case 240:
                return "Kled";
            case 99:
                return "Lux";
            case 103:
                return "Ahri";
            case 2:
                return "Olaf";
            case 112:
                return "Viktor";
            case 34:
                return "Anivia";
            case 27:
                return "Singed";
            case 86:
                return "Garen";
            case 127:
                return "Lissandra";
            case 57:
                return "Maokai";
            case 25:
                return "Morgana";
            case 28:
                return "Evelynn";
            case 105:
                return "Fizz";
            case 74:
                return "Heimerdinger";
            case 238:
                return "Zed";
            case 68:
                return "Rumble";
            case 82:
                return "Mordekaiser";
            case 37:
                return "Sona";
            case 96:
                return "Kog'Maw";
            case 55:
                return "Katarina";
            case 117:
                return "Lulu";
            case 22:
                return "Ashe";
            case 30:
                return "Karthus";
            case 12:
                return "Alistar";
            case 122:
                return "Darius";
            case 67:
                return "Vayne";
            case 110:
                return "Varus";
            case 77:
                return "Udyr";
            case 89:
                return "Leona";
            case 126:
                return "Jayce";
            case 134:
                return "Syndra";
            case 80:
                return "Pantheon";
            case 92:
                return "Riven";
            case 121:
                return "Kha'Zix";
            case 42:
                return "Corki";
            case 268:
                return "Azir";
            case 51:
                return "Caitlyn";
            case 76:
                return "Nidalee";
            case 85:
                return "Kennen";
            case 3:
                return "Galio";
            case 45:
                return "Veigar";
            case 432:
                return "Bard";
            case 150:
                return "Gnar";
            case 90:
                return "Malzahar";
            case 104:
                return "Graves";
            case 254:
                return "Vi";
            case 10:
                return "Kayle";
            case 39:
                return "Irelia";
            case 64:
                return "Lee Sin";
            case 420:
                return "Illaoi";
            case 60:
                return "Elise";
            case 106:
                return "Volibear";
            case 20:
                return "Nunu";
            case 4:
                return "Twisted Fate";
            case 24:
                return "Jax";
            case 102:
                return "Shyvana";
            case 429:
                return "Kalista";
            case 36:
                return "Dr. Mundo";
            case 427:
                return "Ivern";
            case 131:
                return "Diana";
            case 223:
                return "Tahm Kench";
            case 63:
                return "Brand";
            case 113:
                return "Sejuani";
            case 8:
                return "Vladimir";
            case 154:
                return "Zac";
            case 421:
                return "Rek'Sai";
            case 133:
                return "Quinn";
            case 84:
                return "Akali";
            case 163:
                return "Taliyah";
            case 18:
                return "Tristana";
            case 120:
                return "Hecarim";
            case 15:
                return "Sivir";
            case 236:
                return "Lucian";
            case 107:
                return "Rengar";
            case 19:
                return "Warwick";
            case 72:
                return "Skarner";
            case 54:
                return "Malphite";
            case 157:
                return "Yasuo";
            case 101:
                return "Xerath";
            case 17:
                return "Teemo";
            case 75:
                return "Nasus";
            case 58:
                return "Renekton";
            case 119:
                return "Draven";
            case 35:
                return "Shaco";
            case 50:
                return "Swain";
            case 91:
                return "Talon";
            case 40:
                return "Janna";
            case 115:
                return "Ziggs";
            case 245:
                return "Ekko";
            case 61:
                return "Orianna";
            case 114:
                return "Fiora";
            case 9:
                return "Fiddlesticks";
            case 31:
                return "Cho'Gath";
            case 33:
                return "Rammus";
            case 7:
                return "LeBlanc";
            case 16:
                return "Soraka";
            case 26:
                return "Zilean";
            case 56:
                return "Nocturne";
            case 222:
                return "Jinx";
            case 83:
                return "Yorick";
            case 6:
                return "Urgot";
            case 203:
                return "Kindred";
            case 21:
                return "Miss Fortune";
            case 62:
                return "Wukong";
            case 53:
                return "Blitzcrank";
            case 98:
                return "Shen";
            case 201:
                return "Braum";
            case 5:
                return "Xin Zhao";
            case 29:
                return "Twitch";
            case 11:
                return "Master Yi";
            case 44:
                return "Taric";
            case 32:
                return "Amumu";
            case 41:
                return "Gangplank";
            case 48:
                return "Trundle";
            case 38:
                return "Kassadin";
            case 161:
                return "Vel'Koz";
            case 143:
                return "Zyra";
            case 267:
                return "Nami";
            case 59:
                return "Jarvan IV";
            case 81:
                return "Ezreal";
            case 164:
                return "Camile";
            case 498:
                return "Xayah";
            case 497:
                return "Rakan";
            case 141:
                return "Kayn";
            case 516:
                return "Ornn";
            case 142:
                return "Zoe";
            case 145:
                return "Kaisa";
            case 555:
                return "Pyke";
            case 518:
                return "Neeko";
            case 517:
                return "Sylas";
            case 350:
                return "Yuumi";
            case 246:
                return "Qiyana";
            case 235:
                return "Senna";
            case 523:
                return "Aphelios";
            case 876:
                return "Lillia";
            case 875:
                return "Sett";
            case 777:
                return "Yone";
            case 360:
                return "Samira";
            case 147:
                return "Seraphine";
            case 526:
                return "Rell";
            case 234:
                return "Viego";
            case 887:
                return "Gwen";
            case 166:
                return "Akshan";
            case 711:
                return "Vex";
            case 221:
                return "Zeri";
            case 888:
                return "Renata Glasc";
            default:
                return "error";
        }
    }
}
