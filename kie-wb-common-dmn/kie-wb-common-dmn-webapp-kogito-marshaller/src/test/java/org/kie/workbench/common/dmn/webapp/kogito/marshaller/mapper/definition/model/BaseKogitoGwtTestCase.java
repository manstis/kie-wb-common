/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.junit.client.GWTTestCase;

public class BaseKogitoGwtTestCase extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.dmn.webapp.kogito.marshaller.DMNMarshaller";
    }

    @Override
    public void gwtSetUp() {
        ScriptInjector.fromUrl("model/Jsonix-all.js").inject();
        ScriptInjector.fromUrl("model/DC.js").inject();
        ScriptInjector.fromUrl("model/DI.js").inject();
        ScriptInjector.fromUrl("model/DMNDI12.js").inject();
        ScriptInjector.fromUrl("model/DMN12.js").inject();
        ScriptInjector.fromUrl("model/KIE.js").inject();
        ScriptInjector.fromUrl("model/MainJs.js").inject();
    }
}
