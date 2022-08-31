package com.xkcoding.neo4j.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.neo4j.ogm.id.IdStrategy;

/**
 * <p>
 * 自定义主键策略
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:40
 */
public class UserIdStrategy implements IdStrategy {

    private static Snowflake snowflake = IdUtil.createSnowflake(1L,1L);

    @Override
    public Object generateId(Object o) {
        return snowflake.nextId();
    }
}
