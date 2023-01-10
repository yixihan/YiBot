package com.yixihan.yibot.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 翻译 配置参数
 *
 * @author yixihan
 * @date 2022/12/8 21:32
 */
@Component
@Getter
public class TranslateProperties {
    
    @Value ("${translate.api}")
    private String api;
    
    @Value ("${translate.appid}")
    private String appId;
    
    @Value ("${translate.sign}")
    private String sign;
}
