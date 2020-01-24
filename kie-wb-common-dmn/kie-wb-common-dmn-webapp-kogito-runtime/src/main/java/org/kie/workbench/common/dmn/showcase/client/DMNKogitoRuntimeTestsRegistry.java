/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.showcase.client;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.dmn.showcase.client.tests.DMNTest;

@EntryPoint
public class DMNKogitoRuntimeTestsRegistry {

    private Instance<DMNTest> tests;

    @Inject
    public DMNKogitoRuntimeTestsRegistry(final Instance<DMNTest> tests) {
        this.tests = tests;
    }

    @PostConstruct
    public void init() {
        setupRegistry();

        tests.forEach(test -> nativeRegisterTest(test.name(), test));
    }

    public static native void setupRegistry() /*-{

        console.log("nativeRegisterTests");

        $wnd.gwtEditorTests = new Map();

        $wnd.GWTEditorTest = function (instance) {
            this.instance = instance;
        };

        $wnd.GWTEditorTest.prototype.run = function () {
            return this.instance.@org.kie.workbench.common.dmn.showcase.client.tests.DMNTest::run()();
        };

        $wnd.GWTEditorTest.prototype.messages = function () {
            return this.instance.@org.kie.workbench.common.dmn.showcase.client.tests.DMNTest::messages()();
        };

    }-*/;

    public static native void nativeRegisterTest(final String name, final DMNTest instance) /*-{
        $wnd.gwtEditorTests.set(name, new $wnd.GWTEditorTest(instance));
    }-*/;
}
