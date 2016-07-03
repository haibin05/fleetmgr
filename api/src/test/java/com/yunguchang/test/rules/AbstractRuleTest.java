package com.yunguchang.test.rules;

import com.yunguchang.alarm.RuleBridge;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.AlarmEvent;
import org.drools.core.ObjectFilter;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.AgendaFilter;
import org.mockito.ArgumentMatcher;
import org.mockito.verification.VerificationMode;

import javax.enterprise.event.Event;
import java.util.Collection;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Created by gongy on 9/21/2015.
 */
public class AbstractRuleTest {
    KieSession basicKSession;
    Event<AlarmEvent> spiedAlarmEventSource;
    Event<AlarmEvent> spiedClearAlarmEventSource;
    RuleBridge ruleBridge;


    @Before
    public void setUp() {
        KieServices ks = KieServices.Factory.get();

        KieContainer kc = ks.getKieClasspathContainer();
        KieSessionConfiguration config = KieServices.Factory.get().newKieSessionConfiguration();
        config.setOption(ClockTypeOption.get("pseudo"));
        this.basicKSession = kc.newKieSession("basicKSession", config);
        spiedAlarmEventSource = mock(Event.class);
        basicKSession.setGlobal("alarmEventSource", spiedAlarmEventSource);

        spiedClearAlarmEventSource = mock(Event.class);
        basicKSession.setGlobal("clearAlarmEventSource", spiedClearAlarmEventSource);

        ruleBridge=mock(RuleBridge.class);
        basicKSession.setGlobal("ruleBridge", ruleBridge);


        basicKSession.insert(basicKSession.getSessionClock());
        basicKSession.addEventListener(new DebugAgendaEventListener());
        basicKSession.addEventListener(new DebugRuleRuntimeEventListener() );

        new Thread(new Runnable() {
            @Override
            public void run() {
                basicKSession.fireUntilHalt(
                        getAgendaFilter()
                );
            }
        }).start();
    }

    @After
    public void tearDown() {
        basicKSession.halt();
        basicKSession.dispose();
    }

    protected AgendaFilter getAgendaFilter(){
        return null;
    }


    public <T> Collection<T> getObjectsFromSessionByClass(final Class<T> clazz){
        return (Collection) basicKSession.getObjects(new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().isAssignableFrom (clazz);
            }
        });
    }

    public <T> Collection<T> getObjectsFromSessionByClassName(final String className){
        return (Collection) basicKSession.getObjects(new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().getName().equals(className);
            }
        });
    }




    protected AlarmEvent matchArg(Alarm alarm) {
        return argThat( new AlarmMatcher(alarm));
    }
    private static class AlarmMatcher extends ArgumentMatcher<AlarmEvent> {

        private Alarm alarm;

        public AlarmMatcher(Alarm alarm) {
            this.alarm = alarm;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof AlarmEvent && ((AlarmEvent) argument).getAlarm().equals(alarm)){
                return true;
            }
            return false;
        }
    }
}
