package com.yixihan.yibot;

import com.yixihan.yibot.constant.PatternConstants;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/23 23:35
 */
public class MatcherTest {

    @Test
    public void test() {
        String pattern = "^消息记录 群聊 \\.+$";
        String testStr1 = "消息记录 群聊 802086225";
        System.out.println (testStr1.matches (pattern));
    }

    @Test
    public void test2() {
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        String testStr1 = "2022-10-31";
        System.out.println (testStr1.matches (pattern));
    }

    @Test
    public void test3() {
        String pattern = "^消息记录 群聊 " + PatternConstants.GROUP_ID_PATTERN + ".*$";
        String testStr1 = "消息记录 群聊 802086225 2022-10-31 2022-11-14";
        System.out.println ("pattern : " + pattern);
        System.out.println ("str : " + testStr1);
        System.out.println (testStr1.matches (pattern));
    }

    @Test
    public void test4() {
        String pattern = "^xxx " + PatternConstants.GROUP_ID_PATTERN + "\\.*$";
        String testStr1 = "xxx 802086225";
        System.out.println ("pattern : " + pattern);
        System.out.println ("str : " + testStr1);
        System.out.println (testStr1.matches (pattern));
    }
}
