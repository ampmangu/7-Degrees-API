package com.ampmangu.degrees.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    private final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    private final Environment env;

    @Value("${spring.redis.host}")
    private String serverUrl;

    public RedisConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public Jedis jedisClient() {
        log.info("Configuring Redis at {}", serverUrl);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(200);
        config.setMaxTotal(300);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        JedisPool pool = new JedisPool(config, serverUrl, 6379, 3000, "73656775726f64656e74616c6c6973616e65636573697461756e6170617261746f");
        Jedis jedis;
        jedis = pool.getResource();
        pool.close();
        return jedis;
    }
}
