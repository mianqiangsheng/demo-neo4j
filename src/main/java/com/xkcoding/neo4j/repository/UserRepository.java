package com.xkcoding.neo4j.repository;

import com.xkcoding.neo4j.model.User;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 9:42
 */
public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("MATCH (result)-[r*0..]->(c:User {uid:{uid}}) return sum(result.active) as sumActive")
    Long getSumActive(@Param("uid") Long uid);

    @Query("MATCH (father:User{uid:{uid}})<-[*0..{depth}]-(result) return sum(result.active)")
    Long getDepthSumActive(@Param("uid") Long uid, @Param("depth") Integer depth);

    List<User> findAllByUid(@Param("uid") Long uid, @Depth Integer depth);

    User findByUid(@Param("uid") Long uid);


}
