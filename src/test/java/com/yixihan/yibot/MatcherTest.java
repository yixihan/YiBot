package com.yixihan.yibot;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.engine.mmseg.MmsegEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/23 23:35
 */
@Slf4j
@SuppressWarnings ("all")
public class MatcherTest {
    
    @Test
    public void test () {
        //自动根据用户引入的分词库的jar来自动选择使用的引擎
        TokenizerEngine engine = new MmsegEngine ();
    
        //解析文本
        String text = "这两个方法的区别在于返回值";
        Result result = engine.parse(text);
        
        //输出：这 两个 方法 的 区别 在于 返回 值
        String resultStr = IterUtil.join(result, " ");
        System.out.println (resultStr);
    }

}
    
