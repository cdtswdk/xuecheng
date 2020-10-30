package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GovernCenterTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovernCenterTestApplication.class,args);
    }
}
