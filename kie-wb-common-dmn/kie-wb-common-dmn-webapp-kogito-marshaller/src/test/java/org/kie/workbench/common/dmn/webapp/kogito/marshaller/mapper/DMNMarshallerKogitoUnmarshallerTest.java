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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Command;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.mocks.FactoryManagerMock;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class DMNMarshallerKogitoUnmarshallerTest extends GWTTestCase {

    private static final Logger LOG = Logger.getLogger(DMNMarshallerKogitoUnmarshallerTest.class.getName());

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.dmn.webapp.kogito.marshaller.DMNMarshallerKogitoUnmarshallerTest";
    }

    public void testUnmarshall() {
        LOG.info("---> Entering unmarshall test...");

        final Step step8 = new Step(() -> {
            MainJs.initializeJsInteropConstructors();
            doTestUnmarshall();
        });
        final Step step7 = new Step(() -> injectJavaScript("MainJs.js", step8));
        final Step step6 = new Step(() -> injectJavaScript("KIE.js", step7));
        final Step step5 = new Step(() -> injectJavaScript("DMN12.js", step6));
        final Step step4 = new Step(() -> injectJavaScript("DMNDI12.js", step5));
        final Step step3 = new Step(() -> injectJavaScript("DI.js", step4));
        final Step step2 = new Step(() -> injectJavaScript("DC.js", step3));
        final Step step1 = new Step(() -> injectJavaScript("Jsonix-all.js", step2));

        step1.onSuccess(null);

        LOG.info("---> Exiting unmarshall test...");
    }

    @SuppressWarnings("unchecked")
    private void doTestUnmarshall() {
        final FactoryManager factoryManager = new FactoryManagerMock();
        final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper = new DMNMarshallerImportsHelperKogitoImpl();
        final DMNMarshallerKogitoUnmarshaller unmarshaller = new DMNMarshallerKogitoUnmarshaller(factoryManager, dmnMarshallerImportsHelper);

        final Metadata metadata = new MetadataImpl();
        final JSITDefinitions jsiDefinitions = new JSITDefinitions();
        jsiDefinitions.setOtherAttributes(new HashMap<>());
        jsiDefinitions.setNamespace("namespace");

        final Graph graph = unmarshaller.unmarshall(metadata, jsiDefinitions);
        assertNotNull(graph);

        final Node<Definition<DMNDiagram>, ?> root = GraphUtils.getFirstNode(graph, DMNDiagram.class);
        assertNotNull(root);

        final DMNDiagram dmnDiagram = root.getContent().getDefinition();
        assertEquals("namespace", dmnDiagram.getDefinitions().getNamespace().getValue());
    }

    private void injectJavaScript(final String uri,
                                  final Step callback) {
        ScriptInjector
                .fromUrl(uri)
                .setWindow(ScriptInjector.TOP_WINDOW)
                .setCallback(callback)
                .inject();
    }

    private static class Step implements Callback<Void, Exception> {

        private final Command success;

        private Step(final Command success) {
            this.success = success;
        }

        @Override
        public void onFailure(final Exception reason) {
            fail(reason.getMessage());
        }

        @Override
        public void onSuccess(final Void result) {
            success.execute();
        }
    }
}