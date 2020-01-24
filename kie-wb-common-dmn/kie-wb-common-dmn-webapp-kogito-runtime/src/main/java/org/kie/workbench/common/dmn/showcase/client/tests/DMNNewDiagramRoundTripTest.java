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
package org.kie.workbench.common.dmn.showcase.client.tests;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;

@ApplicationScoped
public class DMNNewDiagramRoundTripTest extends BaseDMNClientDiagramServiceTest {

    public static final String NAME = DMNNewDiagramRoundTripTest.class.getName();

    @Inject
    public DMNNewDiagramRoundTripTest(final DMNClientDiagramServiceImpl service) {
        super(service);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void doRun() {
        doUnmarshall();
    }

    private void doUnmarshall() {
        service.transform("",
                          new ServiceCallback<Diagram>() {
                              @Override
                              public void onSuccess(final Diagram diagram) {
                                  informationMessages.add("Diagram = " + diagram.toString());
                                  doMarshall(diagram);
                              }

                              @Override
                              public void onError(final ClientRuntimeError error) {
                                  assertionErrors.add(error.getMessage());
                              }
                          });
    }

    private void doMarshall(final Diagram diagram) {
        final KogitoDiagramResourceImpl resource = new KogitoDiagramResourceImpl(diagram);
        service.doTransform(resource,
                            new ServiceCallback<String>() {
                                @Override
                                public void onSuccess(final String xml) {
                                    informationMessages.add("XML = " + xml);
                                }

                                @Override
                                public void onError(final ClientRuntimeError error) {
                                    assertionErrors.add(error.getMessage());
                                }
                            });
    }
}
