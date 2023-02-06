package com.yixihan.yibot.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author yixihan
 * @date 2023/2/6 20:21
 */
@Component
@Getter
@Setter
public class CommonProperties {
    
    @Value ("${qqbot.self-id}")
    private Long selfId;
}
