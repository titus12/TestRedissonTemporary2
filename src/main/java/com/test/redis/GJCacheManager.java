package com.test.redis;

import org.redisson.api.RedissonClient;
import org.redisson.jcache.configuration.RedissonConfiguration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;

public class GJCacheManager {

    public static Cache<String, String> gCache = null;

    public static void initialize(GRedisHelper helper)throws Exception
    {
        MutableConfiguration<String, String> jcacheConfig = new MutableConfiguration<String, String>();

        RedissonClient redisson = helper.getRedisson();
        Configuration<String, String> config = RedissonConfiguration.fromInstance(redisson, jcacheConfig);

        CacheManager manager = Caching.getCachingProvider().getCacheManager();
        gCache = manager.createCache("namedCache", config);
    }



}
