package org.davinci.spring.boot.redisson.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * redisson auto configuration tests
 *
 * @author linux_china
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedissonDemoApplication.class)
public class RedissonAutoConfigurationTests {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testRedis() {
        RHyperLogLog<Object> test = redissonClient.getHyperLogLog("test");
        test.add(1);
        System.out.println(test.count());
    }

}
