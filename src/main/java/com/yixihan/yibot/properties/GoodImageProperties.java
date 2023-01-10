package com.yixihan.yibot.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 色图 配置参数
 *
 * @author yixihan
 * @date 2022/11/24 23:11
 */
@Component
@Getter
public class GoodImageProperties {

    @Value("${setu.api}")
    private String url;

    @Value("${setu.list}")
    private String val;

    @Value("${setu.setnx-key}")
    private String setnxKey;

    @Value("${setu.setnx-cnt}")
    private Long setnxCnt;

    @Value("${setu.setnx-time}")
    private Long setnxTime;
}
