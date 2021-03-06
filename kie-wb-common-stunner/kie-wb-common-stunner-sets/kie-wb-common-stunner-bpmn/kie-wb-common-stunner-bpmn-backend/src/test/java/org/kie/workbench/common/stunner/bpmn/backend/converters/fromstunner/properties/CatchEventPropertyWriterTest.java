/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.HashSet;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.spy;

public class CatchEventPropertyWriterTest extends EventPropertyWriterTest {

    @Before
    public void init() {
        event = bpmn2.createStartEvent();
        event.setId(elementId);
        propertyWriter = spy(new CatchEventPropertyWriter((StartEvent) event,
                                                          new FlatVariableScope(),
                                                          new HashSet<>()));
    }

    @Test
    public void testSimulationSetMustHaveElementRef() {
        CatchEventPropertyWriter catchEventPropertyWriter = (CatchEventPropertyWriter) propertyWriter;
        SimulationAttributeSet defaults = new SimulationAttributeSet();
        catchEventPropertyWriter.setSimulationSet(defaults);

        ElementParameters simulationParameters = catchEventPropertyWriter.getSimulationParameters();
        assertEquals(elementId, simulationParameters.getElementRef());
    }

    @Override
    public ErrorEventDefinition getErrorDefinition() {
        return (ErrorEventDefinition) ((CatchEvent)event).getEventDefinitions().get(0);
    }
}