package org.davinci.spring.boot.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.StringUtils;

/**
 * redisson auto configuration
 *
 * @author linux_china
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@ConditionalOnClass({Redisson.class, RedisOperations.class})
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedissonAutoConfiguration {
    @Autowired
    private RedisProperties redisProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        //集群配置
        if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes() != null && redisProperties.getCluster().getNodes().isEmpty()) {
            config.useClusterServers()
                    .setConnectTimeout(redisProperties.getTimeout().getNano() / 1000)
                    .setSubscriptionsPerConnection(redisProperties.getLettuce().getPool().getMaxActive())
                    // 集群状态扫描间隔时间，单位是毫秒
                    .setScanInterval(2000)
                    .setPassword(StringUtils.isEmpty(redisProperties.getPassword()) ? null : redisProperties.getPassword())
                    .addNodeAddress(redisProperties.getCluster().getNodes().toArray(new String[]{}));
        } else if (redisProperties.getSentinel() != null) {
            //哨兵配置
            config.useSentinelServers()
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(redisProperties.getSentinel().getNodes().toArray(new String[]{}))
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(StringUtils.isEmpty(redisProperties.getPassword()) ? null : redisProperties.getPassword());
        } else {
            //单机配置
            //判断是否是ssl
            String schema = redisProperties.isSsl() ? "rediss://" : "redis://";
            config.useSingleServer()
                    .setAddress(schema + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(StringUtils.isEmpty(redisProperties.getPassword()) ? null : redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}