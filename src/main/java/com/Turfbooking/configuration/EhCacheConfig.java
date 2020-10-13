package com.Turfbooking.configuration;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class EhCacheConfig extends CachingConfigurerSupport {

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration listOfSlotsByTurfIdAndDate = new CacheConfiguration();
        listOfSlotsByTurfIdAndDate.setName("listOfSlotsByTurfIdAndDate");
        listOfSlotsByTurfIdAndDate.setMemoryStoreEvictionPolicy("LRU");
        listOfSlotsByTurfIdAndDate.maxEntriesLocalHeap(10000);
        listOfSlotsByTurfIdAndDate.setTimeToLiveSeconds(2*60); //15 minute * 60 seconds

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(listOfSlotsByTurfIdAndDate);

        return net.sf.ehcache.CacheManager.newInstance();
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

}
