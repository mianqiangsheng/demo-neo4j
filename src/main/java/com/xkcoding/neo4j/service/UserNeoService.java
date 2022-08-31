package com.xkcoding.neo4j.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.xkcoding.neo4j.model.User;
import com.xkcoding.neo4j.payload.UserModel;
import com.xkcoding.neo4j.repository.UserRepository;
import lombok.SneakyThrows;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 13:11
 */
@Service
public class UserNeoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyNeoService myNeoService;

    @Autowired
    private SessionFactory sessionFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 查询指定深度的某个节点的累计active值
     * @param uid 用户uid
     * @param depth 指定深度 0表示仅计算其自身 1表示计算自身及其直接子节点 以此类推
     * @return
     */
    public Long getSumActive(Long uid, Integer depth) {

        Long sumActive;

        HashMap<String, Long> map = Maps.newHashMap();
        map.put("uid",uid);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        if (depth == null){
//            sumActive = userRepository.getSumActive(uid);
            Result result = session.query("MATCH (result)-[r*0..]->(c:User {uid:$uid}) return sum(result.active) as sumActive", map);
            sumActive = (Long)result.queryResults().iterator().next().get("sumActive");
        }else {
//            sumActive = userRepository.getDepthSumActive(uid,depth);
//            sumActive = myNeoService.getDepthSumActive(uid,depth);
            Result result = session.query("MATCH (father:User{uid:$uid})<-[*0.." + depth + "]-(result) return sum(result.active)", map);
            sumActive = (Long)result.queryResults().iterator().next().get("sum(result.active)");
        }
        transaction.commit();
        session.clear();
        return sumActive;
    }

    /**
     * 查询指定深度的某个节点的累计active值（排除直接子节点下2个最高的active值）
     * @param uid 用户uid
     * @param depth 指定深度 0表示仅计算其自身 1表示计算自身及其直接子节点 以此类推
     * @return
     */
    public Long getModSumActive(Long uid, Integer depth) {

        Long sumActive = 0L;
        List<Long> activeList = new ArrayList<>();

        HashMap<String, Long> map = Maps.newHashMap();
        map.put("uid",uid);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Result result2 = session.query("MATCH (n:User {uid:$uid}) return n.active", map);
        sumActive = sumActive + (Long)result2.queryResults().iterator().next().get("n.active");
        Result result = session.query("MATCH (childs:User)-[:son]->(n:User) WHERE n.uid = $uid RETURN childs.uid", map);
        List<Map<String, Object>> results = (List<Map<String, Object>>) result.queryResults();
        if (results.size() < 3 || depth == 0){
            return sumActive;
        }
        Iterator<Map<String, Object>> iterator = results.iterator();
        if (depth == null){
            while (iterator.hasNext()) {
                Map<String, Object> next = iterator.next();
                Long childUid = (Long) next.get("childs.uid");
                HashMap<String, Long> map1 = Maps.newHashMap();
                map1.put("uid",childUid);
                Result result1 = session.query("MATCH (result)-[r*0..]->(c:User {uid:$uid}) return sum(result.active) as sumActive", map1);
                Long active = (Long) result1.queryResults().iterator().next().get("sumActive");
                activeList.add(active);
                sumActive = sumActive + active;
            }
        }else if (depth > 0){
            while (iterator.hasNext()) {
                Map<String, Object> next = iterator.next();
                Long childUid = (Long) next.get("childs.uid");
                HashMap<String, Long> map1 = Maps.newHashMap();
                map1.put("uid",childUid);
                Result result1 = session.query("MATCH (father:User{uid:$uid})<-[*0.." + (depth-1) + "]-(result) return sum(result.active)", map1);
                Long active = (Long) result1.queryResults().iterator().next().get("sum(result.active)");
                activeList.add(active);
                sumActive = sumActive + active;
            }

        }

        activeList.sort((Comparator.nullsLast(Long::compareTo)).reversed());
        sumActive = sumActive - activeList.get(0) - activeList.get(1);

        transaction.commit();
        session.clear();
        return sumActive;
    }

//    public void addUser(UserModel userModel){
//
//        User pUser = userRepository.findByUid(userModel.getPUid());
//        if (pUser != null){
//            User user = User.builder()
//                .uid(userModel.getUid())
//                .nickname(userModel.getNickname())
//                .active(userModel.getActive())
//                .pUser(pUser)
//                .build();
//            userRepository.save(user);
//        }
//    }

    /**
     * 添加节点并添加关系，如果父节点不存在，则同步创建父节点
     * @param userModel 节点
     * @param nodup 是否去重 true去重 false不去重
     * @return
     */
    public boolean addUser(UserModel userModel, Boolean nodup) {

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String property = propertiesMapToPropertiesStr(userModel,1);
        property = property.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
        String nodeCypherSql = String.format("%s(%s%s)", nodup ? "MERGE" : "create", ":User", property);
        Result result = session.query(nodeCypherSql, new HashMap<>());

        HashMap<String, Long> map = Maps.newHashMap();
        map.put("uid",userModel.getPUid());
        Result pResult = session.query("match (n:User{uid:$uid}) return n", map);
        if (!pResult.queryResults().iterator().hasNext()){
            String pProperty = propertiesMapToPropertiesStr(userModel, 2);
            pProperty = pProperty.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
            String pNodeCypherSql = String.format("%s(%s%s)", nodup ? "MERGE" : "create", ":User", pProperty);
            Result paResult = session.query(pNodeCypherSql, new HashMap<>());
            if (paResult.queryStatistics().getNodesCreated() <= 0)
                throw new RuntimeException();
        }

        String relationCypherSql;
        String relationType = ":" + "son";
        String relationProperty = "";

        relationCypherSql = String.format("MATCH (from:User{uid:%s}),(to:User{uid:%s}) MERGE (from)-[r%s%s]->(to)", userModel.getUid(), userModel.getPUid(), relationType, relationProperty);
        Result query = session.query(relationCypherSql, new HashMap<>());

        transaction.commit();
        session.clear();
        return result.queryStatistics().getNodesCreated() > 0 && query.queryStatistics().getRelationshipsCreated() > 0;
    }

    /**
     * 刪除节点并刪除与其有关的关系
     * @param uid  节点uid
     * @return
     */
    public boolean deleteUser(Long uid) {

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        HashMap<String, Long> map = Maps.newHashMap();
        map.put("uid",uid);
        Result result = session.query("MATCH (n {uid:$uid}) DETACH DELETE n", map);

        transaction.commit();
        session.clear();
        return result.queryStatistics().getNodesDeleted() > 0;
    }

    /**
     * 更新节点
     * @param userModel 节点
     * @return
     */
    public boolean updateUser(UserModel userModel) {

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        HashMap<String, Long> map = Maps.newHashMap();
        map.put("uid",userModel.getUid());
        map.put("active",userModel.getActive());
        Result result = session.query("MATCH (n:User {uid:$uid}) SET n.active = $active", map);
        transaction.commit();
        session.clear();
        return result.queryStatistics().getPropertiesSet() > 0;
    }

    @SneakyThrows
    public static String propertiesMapToPropertiesStr(UserModel userModel, Integer type) {

        HashMap<String,Object> map = Maps.newHashMap();
        if (type == 1){
            map.put("uid",userModel.getUid());
            map.put("nickname",userModel.getNickname());
            map.put("active",userModel.getActive());
        }else if (type == 2){
            map.put("uid",userModel.getPUid());
            map.put("nickname",userModel.getPNickname());
            map.put("active",userModel.getPActive());
        }

        map.entrySet().removeIf(entry -> Objects.isNull(entry.getValue()));
        return mapper.writeValueAsString(map);
    }
}
