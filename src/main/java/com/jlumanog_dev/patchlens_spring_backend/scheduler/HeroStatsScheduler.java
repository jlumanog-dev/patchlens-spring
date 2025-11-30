package com.jlumanog_dev.patchlens_spring_backend.scheduler;

import com.github.benmanes.caffeine.cache.Cache;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.RecentMatchesDTO;
import com.jlumanog_dev.patchlens_spring_backend.services.OpenDotaRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
public class HeroStatsScheduler {
    private OpenDotaRestService openDotaRestService;
    private CacheManager cacheManager;

    @Autowired
    public HeroStatsScheduler(OpenDotaRestService openDotaRestService, CacheManager cacheManager){
        this.openDotaRestService = openDotaRestService;
        this.cacheManager = cacheManager;
    }

/*    @Cacheable(value = "heroStatCache")
    public HeroDataDTO heroStatRefresh(int id){
        CaffeineCache allHeroesCache =  (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        if(allHeroesCache != null){
            HeroDataDTO[] allHeroes = allHeroesCache.get
        }
    }*/

    //omitted the schedule annotation because caffeine refreshes now every 'n' minutes - check Cache config
    @Scheduled(fixedRate = 30 * 60 * 1000) //30 minutes
    /*@Scheduled(fixedRate = 2 * 60 * 1000)*/ //2 minutes
    @Cacheable(value = "allHeroesStatsCache") // this annotation caches the return value and specifies the cache name
    public List<HeroDataDTO> allHeroesStatsRefresh() {
        List<HeroDataDTO> heroesList = this.openDotaRestService.retrieveAllHeroes();
        System.out.println("allHeroesStatsCache");
        for (HeroDataDTO element : heroesList) {
            int totalPubWins = IntStream.of(element.getPub_win_trend()).sum();
            int totalPubPicks = IntStream.of(element.getPub_pick_trend()).sum();
            float pubWinRate = this.winRateMethod(totalPubWins, totalPubPicks);
            float proWinRate = 100 * ((float) element.getPro_win() / element.getPro_pick());
            float disparityProToPub = this.disparityScore(proWinRate, pubWinRate);

            int latestWinPubPoint = this.latestPubPickOrWinChecker(element.getPub_win_trend());
            int oldestWinPubPoint = this.oldestPubPickOrWinChecker(element.getPub_win_trend());
            float winPubGrowthRate = this.growthRateMethod(latestWinPubPoint, oldestWinPubPoint);

            int latestPickPubPoint = this.latestPubPickOrWinChecker(element.getPub_pick_trend());
            int oldestPickPubPoint = this.oldestPubPickOrWinChecker(element.getPub_pick_trend());
            float pickPubGrowthRate = this.growthRateMethod(latestPickPubPoint, oldestPickPubPoint);


            double averageWin = this.averageMethod(element.getPub_win_trend());
            BigDecimal trendStability = this.standardDeviationMethod(element.getPub_win_trend(), averageWin);
            element.setWinRate(pubWinRate);
            element.setDisparityScore(disparityProToPub);
            element.setWinGrowthRateChange(winPubGrowthRate);
            element.setPickGrowthRateChange(pickPubGrowthRate);
            element.setTrendStdDev(trendStability);
            System.out.println(element.getImg());
        }
        System.out.println("CALLED allHeroesStatsRefresh with images");
        return heroesList;
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) //30 minutes
    /*@Scheduled(fixedRate = 2 * 60 * 1000)*/ //2 minutes
    @Cacheable(value = "topHeroesStatsCache")
    public List<HeroDataDTO> topHeroStatsRefresh(){
        List<HeroDataDTO> heroesList = this.openDotaRestService.retrieveAllHeroes();
        for(HeroDataDTO element : heroesList){
            int totalPubWins = IntStream.of(element.getPub_win_trend()).sum();
            int totalPubPicks = IntStream.of(element.getPub_pick_trend()).sum();
            float pubWinRate = this.winRateMethod(totalPubWins, totalPubPicks);

            float proWinRate = 100 * ( (float) element.getPro_win() / element.getPro_pick());
            element.setWinRate(pubWinRate);
            float disparityProToPub = this.disparityScore(proWinRate, pubWinRate);
            element.setDisparityScore(disparityProToPub);
        }

        //sorting the list into descending order based on disparity score and pub win rate respectively (pro-to-pub)
        heroesList.sort(Comparator.comparing(HeroDataDTO::getDisparityScore).thenComparing(HeroDataDTO::getWinRate).reversed());
        List<HeroDataDTO> topHeroes = heroesList.stream().limit(5).toList();
        System.out.println("topHeroStatsRefresh has been called");
        return topHeroes;
    }


    public List<HeroesPlayedByUserDTO> heroesPlayedByUser(BigInteger user){
        //there might be race condition issue here. fix later if possible
        CaffeineCache allHeroes = (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        List<HeroesPlayedByUserDTO> heroesPlayedList = this.openDotaRestService.retrieveHeroesPlayed(user);
        System.out.println("heroesPlayedByUser called");
        assert allHeroes != null;
        Cache<Object, Object> allHeroesNativeCache = allHeroes.getNativeCache();

        /* Since I'm not using Entity relationships and JPA advance mapping:
        Need to retrieve the allHeroes cache and filter out which item matches heroes' ID from
        heroesPlayedList so that I can assign the correct localized_name & img, and probably a few more*/
        for (HeroesPlayedByUserDTO element : heroesPlayedList){
            /*Reinder that the 'value' is a list of type HeroesPlayedByUserDTO itself, check the sout output and see.
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

    public double averageMethod(int[] pubWinTrend){
        int sum = 0;
        for (int j : pubWinTrend) {
            sum += j;
        }
        return (double) sum / pubWinTrend.length;
    }

    public int oldestPubPickOrWinChecker(int[] pub_trend){
        //get oldest
        int iterator = 0;
        int oldest_pub_pick = 1;
        while(pub_trend[iterator] == 0 && iterator != pub_trend.length - 1){
            iterator++;
        }
        oldest_pub_pick = pub_trend[iterator];
        return oldest_pub_pick;
    }
    public int latestPubPickOrWinChecker(int[] pub_trend){
        int latestIndex = pub_trend.length - 2; // -2 to exclude the 7th element.
        int latest_pub_pick = 1;
        for(int i = latestIndex; i >= 0; i--){
            if(pub_trend[latestIndex] > 0){
                latest_pub_pick = pub_trend[i];
                break;
            }
        }
        return latest_pub_pick;
    }
    public float winRateMethod(int win_total, int pick_total){
        return (100 *  ( (float) win_total / pick_total));
    }
    public float growthRateMethod(int latest_pick_trend, int oldest_pick_trend){
        return (100 * ((float) (latest_pick_trend - oldest_pick_trend) / oldest_pick_trend));
    }
    public float disparityScore(float proWinRate, float pubWinRate){
        return proWinRate - pubWinRate;
    }
    /*
        1. compute the difference between each pub wins per day and the average win value
        2. get the square of each difference and get the total sum of it all.
        3. divide the total sum by 6 (6 days)
        4. get the square root of the quotient.
        Needs to be BigDecimal because java basic data type can't return precise large value, instead just return concise values
    */
    public BigDecimal standardDeviationMethod(int[] pubWinTrend, double averageWin ){
        ArrayList<BigDecimal> deviationList = new ArrayList<>();
        BigDecimal standardDeviation = new BigDecimal("0.0");

        for (int i = 0; i < pubWinTrend.length - 1; i++) {
            //Check if data point is 0 because sometimes data sets from opendota have 0 values.
            //Must skip it to avoid negative values
            if(pubWinTrend[i] < 1)
                continue;
            BigDecimal difference =  BigDecimal.valueOf(pubWinTrend[i] - averageWin);
            standardDeviation = standardDeviation.add(difference.pow(2));
        }
        //MathContext is important for getting precise digits & decimal, and rounding rules
        MathContext mc = new MathContext(8, RoundingMode.HALF_UP);
        standardDeviation = standardDeviation.divide(BigDecimal.valueOf(6), mc); // Divide the total sum to 6 (6 days)
        standardDeviation = standardDeviation.sqrt(mc); //get square root of the current 'standardDeviation' value.

        return standardDeviation;
    }

}
