/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yunguchang.restapp;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating
 * JAX-RS.
 * <p/>
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </p>
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
    public static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=utf-8";
    public static final String APPLICATION_SPREAD_SHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
    public static final MediaType APPLICATION_EXCEL_TYPE =new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    /* class body intentionally left blank */
}
