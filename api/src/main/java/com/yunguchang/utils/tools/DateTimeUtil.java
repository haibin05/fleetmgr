package com.yunguchang.utils.tools;

import org.joda.time.DateTime;

/**
 * Created by haibin on 2016/1/12.
 */
public class DateTimeUtil {
    public static DateTime appStartTime =  System.getenv( "SyncStartTime" ) == null ? DateTime.parse("2016-01-01T00:00:00") : DateTime.parse(System.getenv( "SyncStartTime"));
    public static DateTime appEndTime = DateTime.parse("2999-12-30T13:59:59");

}
