package com.xkcoding.neo4j.model;

import com.xkcoding.neo4j.config.UserIdStrategy;
import lombok.*;
import org.neo4j.ogm.annotation.*;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 9:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NodeEntity(label = "User")
public class User {

    @Id
    @GeneratedValue(strategy = UserIdStrategy.class)
    private Long id;

    @Property(name="uid")
    private Long uid;
    @Property(name="nickname")
    private String nickname;
    @Property(name="active")
    private Long active;

    @Relationship(type="son")
    private User pUser;
}
