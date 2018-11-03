package com.test.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.codec.KryoCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2018-06-14.
 */
public final class GRedisFactory {
    private static final Logger log = LoggerFactory.getLogger(GRedisFactory.class);
    private static final Map<String, GRedisHelper> REDISFACTORYS  = new HashMap<String, GRedisHelper>();

    public static final int DEFAULT_EXPIRY_IN_SECONDS = 120;
    private static int defaultExpiryInSeconds = DEFAULT_EXPIRY_IN_SECONDS;
    public static final String EXPIRY_PROPERTY_PREFIX = "redis.expiryInSeconds";

    public static void initialize(String configPath,String environment)throws Exception
    {
        InputStream inputStream = null;
        try {
            ClassLoader classLoader = GRedisFactory.class.getClassLoader();
            inputStream = classLoader.getResourceAsStream(configPath);
            log.error("GRedisFactory initialize: " + configPath + "," + environment);
            Config config = Config.fromYAML(inputStream);
            createRedisClient(environment, config);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createRedisClient(String name,@NonNull final Config config) {
        try {
            if (config.getCodec() == null) {
                config.setCodec(new KryoCodec());
            }
            log.debug("Set Redisson Codec = {}", config.getCodec().getClass().getName());
            RedissonClient redisson = Redisson.create(config);
            REDISFACTORYS.put(name,new GRedisHelper(redisson));
        } catch (Exception e) {
            log.error("Fail to create RedisClient.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Create {@link RedisClient} instance by properties
     *
     * @param redissonYamlUrl redisson yaml setting file URL
     * @return {@link RedisClient} instance
     */
    private static void createRedisClient(String name,@NonNull final URL redissonYamlUrl) {
        try {
            Config config = Config.fromYAML(redissonYamlUrl);
            RedissonClient redisson = Redisson.create(config);
            REDISFACTORYS.put(name,new GRedisHelper(redisson));
        } catch (IOException e) {
            log.error("Error in create RedisClient. redissonYamlUrl={}", redissonYamlUrl, e);
            throw new RuntimeException(e);
        }
    }

    public static GRedisHelper getRedisClient(String name){
        return REDISFACTORYS.get(name);
    }
    /**
     * get expiry timeout (seconds) by region name.
     *
     * @param region region name
     * @return expiry (seconds)
     */
    public static int getExpiryInSeconds(final String region) {
        try {
            String key = EXPIRY_PROPERTY_PREFIX + "." + region;
            String value = getProperty(key, String.valueOf(defaultExpiryInSeconds));

            log.trace("load expiry property. region={}, expiryInSeconds={}", key, value);

            return Integer.parseInt(value);
        } catch (Exception e) {
            log.warn("Fail to get expiryInSeconds in region={}", region, e);
            return defaultExpiryInSeconds;
        }
    }

    /**
     * Load default expiry (seconds)
     */
    private static void loadDefaultExpiry() {
        try {
            defaultExpiryInSeconds = Integer.parseInt(getProperty(EXPIRY_PROPERTY_PREFIX + ".default", String.valueOf(DEFAULT_EXPIRY_IN_SECONDS)));
        } catch (Exception ignored) {
            defaultExpiryInSeconds = DEFAULT_EXPIRY_IN_SECONDS;
        }
    }

    /**
     * get property value
     *
     * @param key          property key
     * @param defaultValue default value for not found key
     * @return property value
     */
    public static String getProperty(final String key, String defaultValue) {
        try {
            String value = System.getProperty(key, defaultValue);
            log.trace("get property. key={}, value={}, defaultValue={}", key, value, defaultValue);
            return value;
        } catch (Exception ignored) {
            log.warn("error occurred in reading properties. key={}", key, ignored);
            return defaultValue;
        }
    }

}
