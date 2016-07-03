package com.yunguchang.model.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by haibin on 2015/12/23.
 */
public class OnWorkStateEnum {
    public static Set<String> onWorkState = new HashSet<>();

    static {
        onWorkState.add("0");   // 出勤
        onWorkState.add("4");   // 加班
        onWorkState.add("5");   // 拖班
        onWorkState.add("6");   // 抢修
        onWorkState.add("9");   // 出差
        onWorkState.add("a");   // 通宵班
    }
}
