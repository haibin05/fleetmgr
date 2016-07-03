package com.yunguchang.hibernate;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Created by gongy on 2015/11/2.
 */
public class ConvertToGBKFunction implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
        return StandardBasicTypes.STRING;
    }

    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
        if (arguments.size() != 1) {
            throw new QueryException(new IllegalArgumentException(
                    "ConvertToGBKFunction shoudld have one arg"));
        }
        return "CONVERT(" + arguments.get(0) + " USING gbk )";
    }
}
