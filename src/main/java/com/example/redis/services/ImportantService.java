package com.example.redis.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ImportantService {
    
    @Cacheable(cacheNames = "cache")
    public String longExecutingMethod(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            return "critical error";
        }
        return "business critical data";
    }

    @CacheEvict(value = "cache", allEntries = true)
    public void evictCache() {}

}
