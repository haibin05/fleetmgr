package com.yunguchang.schedule;

import com.yunguchang.alarm.RulesExecutor;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by gongy on 2015/10/14.
 */
@Singleton
@Startup
public class RuleExecutorTimer {



    @Inject
    private RulesExecutor rulesExecutor;





    @Schedule(second = "0", minute = "5", hour = "0", persistent = false)
    public void initRuleEngine() {
        rulesExecutor.initRuleEngine();
    }


}
