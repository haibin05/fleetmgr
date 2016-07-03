package com.yunguchang.hibernate;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Created by gongy on 2015/11/2.
 */
public class MySQL5InnoDBDialectExt extends MySQL5InnoDBDialect {

    public MySQL5InnoDBDialectExt() {
        super();
        registerFunction("casttogbk",new ConvertToGBKFunction());
    }
}
