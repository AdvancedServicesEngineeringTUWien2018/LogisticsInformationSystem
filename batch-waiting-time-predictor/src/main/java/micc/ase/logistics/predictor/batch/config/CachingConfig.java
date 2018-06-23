package micc.ase.logistics.predictor.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
public class CachingConfig {

    private final static Logger LOG = LoggerFactory.getLogger(CachingConfig.class);
 
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("waitingTimePredictions");
    }

    @CacheEvict(allEntries = true, value = "waitingTimePredictions")
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000, initialDelay = 500)
    public void evictCache() {
        LOG.info("Waiting time predictions cache evicted");
    }

}