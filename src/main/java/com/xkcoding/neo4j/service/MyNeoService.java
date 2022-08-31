package com.xkcoding.neo4j.service;

import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.neo4j.driver.v1.Values.parameters;


/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 14:15
 */
@Service
public class MyNeoService implements AutoCloseable {

    private Driver driver;

    @Value("${spring.data.neo4j.uri}")
    private String uri;

    @Value("${spring.data.neo4j.username}")
    private String user;

    @Value("${spring.data.neo4j.password}")
    private String password;

    public MyNeoService() {
    }

    @PostConstruct
    public void init(){
        driver = GraphDatabase.driver(this.uri, AuthTokens.basic(user, password));
    }

    public Long getDepthSumActive(Long uid, Integer depth) {
        try (Session session = driver.session()) {
            Long aLong = session.writeTransaction(tx -> {
                StatementResult statementResult = tx.run(
                    "MATCH (father:User{uid:$uid})<-[*0.."+ depth + "]-(result) return sum(result.active)",
                    parameters("uid", uid));
                return statementResult.single().get(0).asLong();
            });
            return aLong;
        }

    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
