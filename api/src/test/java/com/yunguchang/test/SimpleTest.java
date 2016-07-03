package com.yunguchang.test;

import org.junit.Test;

import java.util.UUID;

/**
 * Created by 禕 on 2015/12/5.
 */

public class SimpleTest {
    @Test
    public void testUUID(){
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }
}
