package com.xkcoding.neo4j;

import com.xkcoding.neo4j.payload.UserModel;
import com.xkcoding.neo4j.service.UserNeoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 13:11
 */
@Slf4j
public class Neo4jTest extends SpringBootDemoNeo4jApplicationTests {

    @Autowired
    private UserNeoService userNeoService;

    @Test
    public void test1() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long l = System.currentTimeMillis();
        String format = simpleDateFormat.format(l);
        System.out.println("开始时间: " + format);

        Long sumActive = userNeoService.getSumActive(10L, null);
//        Long sumActive1 = userNeoService.getSumActive(88888L, 1);
        System.out.println(sumActive);
//        System.out.println(sumActive1);

        long l1 = System.currentTimeMillis();
        String format1 = simpleDateFormat.format(l1);
        System.out.println("结束时间: " + format1);
        long l2 = l1 - l;
        long l3 = l2 / 1000;
        System.out.println("结束---------------------,花费：" + l3 + "秒");
    }

    @Test
    public void test2() {
        UserModel lizhen = UserModel.builder().pUid(105137L).uid(105138L).active(1L).nickname("suyuanke").build();
//        UserModel lizhen = UserModel.builder().pUid(102136L).uid(105137L).active(1L).nickname("lizhen").build();

        userNeoService.addUser(lizhen,true);
    }

    @Test
    public void test3() {
        userNeoService.deleteUser(105137L);
    }

    @Test
    public void test4() {
        UserModel lizhen = UserModel.builder().uid(105137L).active(101L).build();

        userNeoService.updateUser(lizhen);
    }

    @Test
    public void test5() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long l = System.currentTimeMillis();
        String format = simpleDateFormat.format(l);
        System.out.println("开始时间: " + format);

        Long modSumActive = userNeoService.getModSumActive(10L, 0);
        System.out.println(modSumActive);

        long l1 = System.currentTimeMillis();
        String format1 = simpleDateFormat.format(l1);
        System.out.println("结束时间: " + format1);
        long l2 = l1 - l;
        long l3 = l2 / 1000;
        System.out.println("结束---------------------,花费：" + l3 + "秒");
    }


}
