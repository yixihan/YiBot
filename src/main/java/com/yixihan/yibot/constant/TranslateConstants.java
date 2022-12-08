package com.yixihan.yibot.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author yixihan
 * @date 2022/12/8 21:32
 */
@Component
@Getter
public class TranslateConstants {
    
    public static final String WORD_ONE = "翻译";
    
    @Value ("${translate.api}")
    private String api;
    
    @Value ("${translate.appid}")
    private String appId;
    
    @Value ("${translate.sign}")
    private String sign;
}
