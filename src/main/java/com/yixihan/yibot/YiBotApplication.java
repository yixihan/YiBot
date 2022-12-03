package com.yixihan.yibot;

import cn.hutool.cron.CronUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/**
 * 小易 qq bot
 *
 * @author yixihan
 * @date 2022-11-24
 */
@Slf4j
@EnableDiscoveryClient
@EnableScheduling
@RefreshScope
@EnableCaching
@SpringBootApplication
public class YiBotApplication {
    
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(YiBotApplication.class);
        Environment env = springApplication.run(args).getEnvironment();
        log.info("yibot server has started : {}, CPU core : {}",
                Arrays.toString(env.getActiveProfiles()), Runtime.getRuntime().availableProcessors());
    
        CronUtil.start ();
    }

}
