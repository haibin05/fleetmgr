package com.yunguchang.test.ejb;

import org.joda.time.DateTime;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by gongy on 2015/10/14.
 */
@Stateless
public class AsyncEjb {



    @Inject
    private AsyncEjb asyncEjb;
    @Asynchronous
    public void foo() throws InterruptedException {
        asyncEjb.bar();
        Thread.sleep(5000);
        System.out.println("foo");
    }

    @Asynchronous
    public void bar(){
        System.out.println("bar");
    }
}
