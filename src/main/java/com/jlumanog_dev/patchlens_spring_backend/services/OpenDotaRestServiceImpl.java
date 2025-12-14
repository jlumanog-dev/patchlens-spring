package com.jlumanog_dev.patchlens_spring_backend.services;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.jlumanog_dev.patchlens_spring_backend.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

//This service class is dedicated to retrieving game data from OpenDota API
// and persisting it into all relevant database table
@Service
public class OpenDotaRestServiceImpl implements OpenDotaRestService {
    private AnthropicClient anthropicClient;
    private RestTemplate dotaRestTemplate;
    private CacheManager cacheManager;
    private ObjectMapper objectMapper;
    //these API endpoints don't need keys at the moment
    private String[] api = {
            "https://api.opendota.com/api/heroStats",
            "https://api.opendota.com/api/players/"
    };
    private String[] apiQueryParams = { "/recentMatches?game_mode=22", "/matches?game_mode=22&limit=20" };


    @Autowired
    public OpenDotaRestServiceImpl(RestTemplate dotaRestTemplate,
                                   CacheManager cacheManager,
                                   ModelMapper modelMapper,
                                   AnthropicClient anthropicClient,
                                   ObjectMapper objectMapper){
        this.dotaRestTemplate = dotaRestTemplate;
        this.cacheManager = cacheManager;
        this.anthropicClient = anthropicClient;
        this.objectMapper = objectMapper;
    }
    @Override
    public List<HeroDataDTO> retrieveAllHeroes(){
        List<HeroDataDTO> heroesList;
        try{
            HeroDataDTO[] heroes = this.dotaRestTemplate.getForObject(this.api[0], HeroDataDTO[].class);
            assert heroes != null;
            heroesList = Arrays.asList(heroes);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return heroesList;
    }

    @Override
    public Map<String, Object> retrieveHeroesPlayed(BigInteger steamId){
        List<MatchRankedDTO> matchesList;
        Map<Integer, Long> frequencyMatchObjects; // this Map contains the frequency of heroId <hero ID, frequency>
        /*List<MatchRankedDTO> sortedMatches;*/
        Map<String, Object> responseObject = new HashMap<>();
        try{
            MatchRankedDTO[] heroesPlayedArray = this.dotaRestTemplate.getForObject(this.api[1] + steamId.toString() + this.apiQueryParams[1], MatchRankedDTO[].class);
            assert heroesPlayedArray != null;
            matchesList = Arrays.asList(heroesPlayedArray);
            //need to create a separate map containing <hero_id, frequency of objects with this hero_id>
            //then sort the list using the map object
            frequencyMatchObjects = matchesList.stream().collect(Collectors.groupingBy(MatchRankedDTO::getHero_id, Collectors.counting()));
            responseObject.put("frequencyHeroes", frequencyMatchObjects);
            responseObject.put("fixedSetMatches", matchesList);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseObject;
    }

    @Override
    public HeroDataDTO retrieveHero(int heroId){
        CaffeineCache allHeroesCache = (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        assert allHeroesCache != null;

/*        allHeroesCache.getNativeCache().asMap().forEach((key, value) ->{
            Optional<HeroDataDTO> temp = ((List<HeroDataDTO>) value)
                    .stream().filter(hero -> hero.getId() == heroId).findFirst();
            assert temp.isPresent();
            heroRetrievedDTO = temp;
        });*/
        List<HeroDataDTO> allHeroesList = (List<HeroDataDTO>) allHeroesCache.getNativeCache().asMap().entrySet().iterator().next().getValue();
        return allHeroesList.stream().filter(element -> element.getId() == heroId).findFirst().get();
    }
    @Cacheable(value="recentMatchDataCache")
    public RecentMatchesDTO[] fetchRecentMatchWithCache(BigInteger steamId){
        RecentMatchesDTO[] recentMatchesDTOList = this.dotaRestTemplate.getForObject(this.api[1] + steamId.toString() + this.apiQueryParams[0], RecentMatchesDTO[].class);
        System.out.println("new cache");
        return recentMatchesDTOList;
    }

    @Override
    @Cacheable(value="recentMatchesResultCache")
    public Map<String, Object> retrieveRecentMatches(BigInteger steamId, RecentMatchesDTO[] recentMatchMap){
        CaffeineCache allHeroesCache = (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        assert allHeroesCache != null;

        List<HeroDataDTO> allHeroes =  (List<HeroDataDTO>) allHeroesCache.getNativeCache().asMap().entrySet().iterator().next().getValue();

        System.out.println("new cache - computed fields for set of matches and anthropic response");
        //aggregate fields
        float winRate;
        float cumulativeKDA;
        float avgGPM;
        float avgXPM;
        float avgHeroDamage;
        float avgTowerDamage;
        float avgLastHit;
        float avgLastHitPerMinute;
        int totalWins = 0;
        int sumKills = 0;
        int sumDeaths = 0;
        int sumAssists = 0;
        int sumGPM = 0;
        int sumXPM = 0;
        int sumHeroDamage = 0;
        int sumTowerDamage = 0;
        int sumLastHits = 0;
        int sumDuration = 0;

        for(RecentMatchesDTO element : recentMatchMap){
            element.setKdaRatio(element.getKills(), element.getDeaths(), element.getAssists());
            element.setGpmXpmEfficiency(element.getGold_per_min(), element.getXp_per_min());
            element.setCsPerMinEfficiency(element.getLast_hits(), element.getDuration());
            element.setHeroDmgEfficiency(element.getHero_damage(), element.getDuration());
            element.setTowerDmgEfficiency(element.getTower_damage(), element.getDuration());

            HeroDataDTO tempHero = allHeroes.stream().filter(hero -> hero.getId() == element.hero_id).findFirst().get();
            element.setLocalized_name(tempHero.getLocalized_name());
            if(element.radiant_win && element.getPlayer_slot() <= 127){
                totalWins += 1;
            }
            else if(!element.radiant_win && element.getPlayer_slot() >= 128){
                //might implement something like radiant-dire win ratio or something similar in the future
                totalWins += 1;
            }
            sumKills += element.getKills();
            sumDeaths += element.getDeaths();
            sumAssists += element.getAssists();
            sumGPM += element.getGold_per_min();
            sumXPM += element.getXp_per_min();
            sumHeroDamage += element.getHero_damage();
            sumTowerDamage += element.getTower_damage();
            sumLastHits += element.getLast_hits();
            sumDuration += element.getDuration();
        }
        winRate = 100 * ((float) totalWins / recentMatchMap.length);
        cumulativeKDA = (float) (sumKills + sumAssists) / Math.max(1, sumDeaths);
        avgGPM = (float) sumGPM / recentMatchMap.length;
        avgXPM = (float) sumXPM / recentMatchMap.length;
        avgHeroDamage = (float) sumHeroDamage / recentMatchMap.length;
        avgTowerDamage = (float) sumTowerDamage / recentMatchMap.length;
        //farm stats
        avgLastHit = (float) sumLastHits  / recentMatchMap.length;
        avgLastHitPerMinute = (float) sumLastHits / ((float)sumDuration / 60);

        RecentMatchAggregateDTO temp = new RecentMatchAggregateDTO(recentMatchMap.length, winRate, cumulativeKDA, avgGPM, avgXPM, avgHeroDamage, avgTowerDamage, avgLastHit, avgLastHitPerMinute);
        Map<String, Object> heroesPlayedMap = this.heroesPlayedByUser(steamId);
        List<MatchRankedDTO> matchList = (List<MatchRankedDTO>) heroesPlayedMap.get("fixedSetMatches");
        Map<Integer, Long> heroFrequencyPerMatch = (Map<Integer, Long>) heroesPlayedMap.get("frequencyHeroes");
        Map<Integer, Long> topThreeHeroesByFrequency = heroFrequencyPerMatch.entrySet().stream().sorted(Map.Entry.<Integer, Long>comparingByValue().reversed()).limit(3).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        //too lazy to create a dedicated POJO. Using generic object here instead.
        List<TopHeroComputedDTO> topHeroesWithComputedFields = new ArrayList<>();

        topThreeHeroesByFrequency.forEach((key, value) ->{
            String localized_name = "";
            float averageKills = 0;
            float averageDeaths = 0;
            float averageAssists = 0;
            int numberOfMatches = 0;
            String[] roles = {};
            for(MatchRankedDTO item : matchList){
                if(item.getHero_id() == key){
                    averageKills += item.kills;
                    averageDeaths += item.deaths;
                    averageAssists += item.assists;
                    numberOfMatches++;
                    localized_name = item.getLocalized_name();
                    roles = item.getRoles();
                }
            }
            averageKills /= numberOfMatches;
            averageDeaths /= numberOfMatches;
            averageAssists /= numberOfMatches;
            topHeroesWithComputedFields.add(new TopHeroComputedDTO(localized_name, averageKills, averageDeaths, averageAssists, numberOfMatches, roles));
        });

        String recentMatchAggregateJson;
        String topHeroesDataJson;
        try{
            recentMatchAggregateJson = this.objectMapper.writeValueAsString(temp);
            topHeroesDataJson = this.objectMapper.writeValueAsString(topHeroesWithComputedFields);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String systemPrompt = "You are a professional Dota 2 Player and Coach that understands the importance of a player's stats to assess gameplay performance, but also the stats of each hero only if provided, like how they perform on pubs to determine which heroes are trending, and whether you recommend playing these heroes or not based on the data provided to you over time. Your answers should be very short because players may be impatient to read long text or response, but straight to the point and you're able to convey an overall assessment, insights, and recommendations in a way that is intuitive for casual players and maybe encouraging, just like how a Coach wants players to improve and get better at the game. Do not use any outline or list in your response.";


        MessageCreateParams params = MessageCreateParams.
                builder().maxTokens(2000).system(systemPrompt).
                addUserMessage("Player's computed data based on recent matches:" + recentMatchAggregateJson + "Top 3 most played heroes in recent match: " + topHeroesDataJson).
                model(Model.CLAUDE_SONNET_4_5_20250929).build();
        Message message = this.anthropicClient.messages().create(params);
        System.out.println(message);
        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put("match_aggregate",  new RecentMatchAggregateDTO(recentMatchMap.length, winRate, cumulativeKDA, avgGPM, avgXPM, avgHeroDamage, avgTowerDamage, avgLastHit, avgLastHitPerMinute));
        matchMap.put("match_list", recentMatchMap);
        matchMap.put("insight", message.content());
        return matchMap;
    }

    public Map<String, Object> heroesPlayedByUser(BigInteger steamId){
        //there might be race condition issue here. fix later if possible
        //need to get the cache for all hero details from allHeroesStatsCache to get the img and localized_name
        CaffeineCache allHeroes = (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        Map<String, Object> heroesPlayedList = this.retrieveHeroesPlayed(steamId);
        assert allHeroes != null;
        Cache<Object, Object> allHeroesNativeCache = allHeroes.getNativeCache();

        /* Since I'm not using Entity relationships and JPA advance mapping:
        Need to retrieve the allHeroes cache and filter out which item matches heroes' ID from
        heroesPlayedList so that I can assign the correct localized_name & img, and probably a few more*/
        for (MatchRankedDTO element : (List<MatchRankedDTO>) heroesPlayedList.get("fixedSetMatches")){
            /*Reminder that the 'value' is a list of type HeroesPlayedByUserDTO itself, check the sout output and see.
            The allHeroesNativeCache  is converted to a Map collection that contains only 1 value (allHeroesStatsCache)
            to use the forEach method and access the actual value needed through 'value' object parameter.*/
            allHeroesNativeCache.asMap().forEach((key, value) -> {
                //The value should be a list of type HeroDataDTO.
                Optional<HeroDataDTO> heroItem = ((List<HeroDataDTO>) value).stream().filter(hero ->
                        hero.getId() == element.getHero_id()).findFirst();
                assert heroItem.isPresent();
                element.setImg(heroItem.get().getImg());
                element.setRoles(heroItem.get().getRoles());
                element.setLocalized_name(heroItem.get().getLocalized_name());
            });
        }
        return heroesPlayedList;
    }


}
