package com.xkcoding.neo4j.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：li zhen
 * @description:
 * @date ：2022/8/8 13:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {

    private Long uid;
    private String nickname;
    private Long active;
    private Long pUid;
    private String pNickname;
    private Long pActive;
}
