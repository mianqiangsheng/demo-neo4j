package com.xkcoding.neo4j.model;

import com.xkcoding.neo4j.config.UserIdStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 15:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RelationshipEntity(type = "son")
public class SonOf {

    @Id
    @GeneratedValue(strategy = UserIdStrategy.class)
    private Long relationshipId;
    @StartNode
    private User son;
    @EndNode
    private User father;
}
