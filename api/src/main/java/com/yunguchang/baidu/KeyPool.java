package com.yunguchang.baidu;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by gongy on 2016/1/7.
 */
@ApplicationScoped
public class KeyPool {
    private BlockingQueue<String> pool;

    final private static String DEFAULT_KEY_ONE="Qv3ObVtIt91cf06lXPGsQbE6";
    final private static String DEFAULT_KEY_TWO="kXXrgwW1h0H5LIYPjz1XOHWY";


    @Inject
    @ConfigProperty(name = "baidu.key", defaultValue = DEFAULT_KEY_ONE+","+DEFAULT_KEY_TWO)
    private String baiduKeys = DEFAULT_KEY_ONE+","+DEFAULT_KEY_TWO;

    private String reservedKey;

    public KeyPool() {


    }

    @PostConstruct
    public void init() {

        Iterable<String> keysIter = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(baiduKeys);


        List<String> keys = new ArrayList<>();
        Iterables.addAll(keys, keysIter);
        if (keys.size() == 0) {
            keys = Arrays.asList(
                    DEFAULT_KEY_ONE,
                    DEFAULT_KEY_TWO
            );
        }



        pool = new ArrayBlockingQueue<>(keys.size() * 20, true);

        for (String key : keys) {
            for (int i = 0; i < 20; i++) {
                try {
                    pool.put(key);
                } catch (InterruptedException e) {

                }
            }
        }

        reservedKey=keys.get(0);
    }

    public String acquire() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
        }
        return null;
    }



    public String getReservedKey() {
        return reservedKey;
    }

    public void release(String key) {
        try {
            if (key!=null) {
                pool.put(key);
            }else{
                throw new RuntimeException("return a null key");
            }
        } catch (InterruptedException e) {

        }
    }
}

