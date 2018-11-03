package com.test.redis;

import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018-06-14.
 */
public class GRedisHelper  {
    private static final Logger log = LoggerFactory.getLogger(GRedisHelper.class);
    private RedissonClient redisson;

    public GRedisHelper(){};


    public GRedisHelper(@NonNull RedissonClient redisson) {
        this.redisson = redisson;
    }

    public RedissonClient getRedisson(){
        return redisson;
    }

    public long nextTimestamp(final List<Object> keys){
        return redisson.getScript().eval(RScript.Mode.READ_WRITE,
                "redis.call('setnx', KEYS[1], ARGV[1]); " +
                        "return redis.call('incr', KEYS[1]);",
                RScript.ReturnType.INTEGER, keys, System.currentTimeMillis());
    }

    public long dbSize() throws Exception {
        return redisson.getKeys().count();
    }



    public void flushDb() {
        log.info("flush db...");
        redisson.getKeys().flushdb();
    }

    public boolean isShutdown() {
        return redisson.isShutdown();
    }

    public void shutdown() {
        redisson.shutdown();
    }



}
