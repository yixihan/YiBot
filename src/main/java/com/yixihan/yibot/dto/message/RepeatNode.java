package com.yixihan.yibot.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/25 20:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepeatNode {

    private String lastMessage;

    private int cnt;

    private boolean flag;
}
