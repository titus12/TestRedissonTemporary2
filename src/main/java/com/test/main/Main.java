package com.test.main;

import com.test.redis.GRedisFactory;
import com.test.redis.GRedisHelper;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;

import java.util.concurrent.*;

public class Main {
    private static ConcurrentMap<String, RLocalCachedMap<String, Long>> rLocalCachedMap = new ConcurrentHashMap<String, RLocalCachedMap<String, Long>>(5000);
    public static void main(String[] args) {
        try {
            GRedisFactory.initialize("redisson.yaml","test");
            Thread tr = new Thread(new TestThread());
            tr.start();

          /*  ScheduledThreadPoolExecutor se = new ScheduledThreadPoolExecutor(1);
            int count = 100;
            se.scheduleAtFixedRate(
                    ()-> {
                        try {
                            for (int i = 0; i < count; i++) {
                                String region = i + "";
                                String key = "key-" + i;
                                System.out.println("begin read region:" + region + ",key:"+ key);
                                RLocalCachedMap<String, Long> rLocalCachedMap = getLocalCachedMap(region);
                                Long v = rLocalCachedMap.get(key);
                                if(v != null){
                                    System.out.println("region:" + region + ",key:"+ key);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    ,1000,1000, TimeUnit.MILLISECONDS);
*/
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static RLocalCachedMap<String, Long> getLocalCachedMap(final String region) throws Exception {
        try {
            RLocalCachedMap<String, Long> cache = rLocalCachedMap.get(region);
            if (cache == null) {
                GRedisHelper helper = GRedisFactory.getRedisClient("test");
                LocalCachedMapOptions<String,Long> localCachedMapOptions = LocalCachedMapOptions.defaults();
                cache = helper.getRedisson().getLocalCachedMap(region, localCachedMapOptions);
                RLocalCachedMap<String, Long> concurrent = rLocalCachedMap.putIfAbsent(region, cache);
                if (concurrent != null) {
                    cache = concurrent;
                }
            }
            return cache;
        }catch (Exception e){
            throw new RuntimeException(region + " get cache failure from redis!!! " + e.getMessage());
        }
    }

    public static void releaseLocalCachedMap(final String region) throws Exception {
        RLocalCachedMap<String, Long> cache = rLocalCachedMap.get(region);
        cache.destroy();
        rLocalCachedMap.remove(region);
    }
}
