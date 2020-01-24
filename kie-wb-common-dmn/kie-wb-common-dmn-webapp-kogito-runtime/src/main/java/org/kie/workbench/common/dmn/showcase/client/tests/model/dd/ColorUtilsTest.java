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
package org.kie.workbench.common.dmn.showcase.client.tests.model.dd;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.showcase.client.tests.BaseDMNTest;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.dd.ColorUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;

@ApplicationScoped
public class ColorUtilsTest extends BaseDMNTest {

    public static final String NAME = ColorUtilsTest.class.getName();

    @PostConstruct
    public void init() {
        // The JSNI constructors are setup by DMNMarshallerKogitoMarshaller however
        // we're not instantiating that class for this test so perform manually.
        MainJs.initializeJsInteropConstructors(MainJs.getConstructorsMap());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected void doRun() {
        dmnFromWB();

        wbFromDMN();
    }

    private void dmnFromWB() {
        final JSIColor jsiColor = ColorUtils.dmnFromWB("#001122");
        informationMessages.add("DMN = RGB(" + jsiColor.getRed() + ", " + jsiColor.getGreen() + ", " + jsiColor.getBlue() + ")");

        if (!Objects.equals(Integer.decode("#00"), jsiColor.getRed())) {
            assertionErrors.add("JSIColor red component incorrect");
        }
        if (!Objects.equals(Integer.decode("#11"), jsiColor.getGreen())) {
            assertionErrors.add("JSIColor green component incorrect");
        }
        if (!Objects.equals(Integer.decode("#22"), jsiColor.getBlue())) {
            assertionErrors.add("JSIColor blue component incorrect");
        }
    }

    private void wbFromDMN() {
        final JSIColor jsiColor = new JSIColor();
        jsiColor.setRed(Integer.decode("#00"));
        jsiColor.setGreen(Integer.decode("#11"));
        jsiColor.setBlue(Integer.decode("#22"));

        final String wbColour = ColorUtils.wbFromDMN(jsiColor);
        informationMessages.add("WB = " + wbColour);

        if (!Objects.equals("#001122", wbColour)) {
            assertionErrors.add("wbColour incorrect");
        }
    }
}
