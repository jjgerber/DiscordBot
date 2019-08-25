package org.j3y.discordbot.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableCaching
@EnableScheduling
class CacheConfig {

    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager()

        simpleCacheManager.setCaches([
                new ConcurrentMapCache("cfbScoreboards"),
                new ConcurrentMapCache("nflScoreboards")
        ])

        return simpleCacheManager
    }

}
