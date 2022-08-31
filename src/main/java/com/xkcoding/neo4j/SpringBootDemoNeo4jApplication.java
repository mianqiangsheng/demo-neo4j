package com.xkcoding.neo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 13:11
 */
@SpringBootApplication
@EnableTransactionManagement
public class SpringBootDemoNeo4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoNeo4jApplication.class, args);
    }
}
