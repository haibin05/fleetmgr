package com.yunguchang.logger;


import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Created by gongy on 9/10/2015.
 */
public class LoggerProducer {


    @Produces
    public Logger produceJbossLogger(InjectionPoint injectionPoint){
        return org.jboss.logging.Logger.getMessageLogger(Logger.class, injectionPoint.getMember().getDeclaringClass().getName());
    }


}
