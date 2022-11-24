package com.yixihan.yibot.constant;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/24 23:11
 */
@Component
@Getter
public class GoodImageConstants {

    @Value ("${setu.api}")
    private String url;

    @Value ("${setu.list}")
    private String val;

    private final List<String> whiteList = new ArrayList<> ();

    public void initList () {
        String[] splits = val.split (", ");
        whiteList.addAll (Arrays.asList (splits));
    }
}
