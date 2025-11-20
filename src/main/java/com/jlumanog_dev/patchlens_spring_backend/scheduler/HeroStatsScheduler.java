package com.jlumanog_dev.patchlens_spring_backend.scheduler;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
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
import java.util.List;
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

    @Scheduled(fixedRate = 15 * 60 * 1000) //15 minutes
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

            int latestPickPubPoint = this.latestPubPickOrWinChecker(element.getPub_win_trend());
            int oldestPickPubPoint = this.oldestPubPickOrWinChecker(element.getPub_win_trend());
            float pickPubGrowthRate = this.growthRateMethod(latestPickPubPoint, oldestPickPubPoint);


            double averageWin = this.averageMethod(element.getPub_win_trend());
            BigDecimal trendStability = this.standardDeviationMethod(element.getPub_win_trend(), averageWin);
            element.setWinRate(pubWinRate);
            element.setDisparityScore(disparityProToPub);
            element.setWinGrowthRateChange(winPubGrowthRate);
            element.setPickGrowthRateChange(pickPubGrowthRate);
            element.setTrendStdDev(trendStability);
        }
        System.out.println("CALLED allHeroesStatsRefresh");
        return heroesList;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000) //15 minutes
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
        System.out.println("topHeroStatsRefresh has been called");
        return heroesList;
    }

    public HeroesPlayedByUserDTO[] heroesPlayedByUser(BigInteger user){
        System.out.println("heroesPlayedByUser has been called - must be new game played");
        CaffeineCache allHeroes = (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        //call allHeroesStatsRefresh if this cache is empty
        if(allHeroes == null){
            this.allHeroesStatsRefresh();
        }

        return this.openDotaRestService.retrieveHeroesPlayed(user);
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
