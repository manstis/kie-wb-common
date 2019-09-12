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

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionRule;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

import static org.junit.Assert.assertEquals;

public class DecisionRulePropertyConverterTest {

    private static final String ID = "uuid";

    private static final String DESCRIPTION = "description";

    @Test
    public void testWbFromDMN() {
        final JSITUnaryTests inputEntry = new JSITUnaryTests();
        final JSITLiteralExpression outputEntry = new JSITLiteralExpression();
        final JSITDecisionRule dmn = new JSITDecisionRule();
        dmn.setId(ID);
        dmn.setDescription(DESCRIPTION);
        dmn.getInputEntry().setAt(0, inputEntry);
        dmn.getOutputEntry().setAt(0, outputEntry);

        final DecisionRule wb = DecisionRulePropertyConverter.wbFromDMN(dmn);

        assertEquals(ID, wb.getId().getValue());
        assertEquals(DESCRIPTION, wb.getDescription().getValue());
        assertEquals(wb, wb.getInputEntry().get(0).getParent());
        assertEquals(wb, wb.getOutputEntry().get(0).getParent());
    }

    @Test
    public void testDmnFromWb() {
        final UnaryTests inputEntry = new UnaryTests();
        final LiteralExpression outputEntry = new LiteralExpression();
        final DecisionRule wb = new DecisionRule();
        wb.getId().setValue(ID);
        wb.getDescription().setValue(DESCRIPTION);
        wb.getInputEntry().add(inputEntry);
        wb.getOutputEntry().add(outputEntry);

        final JSITDecisionRule dmn = DecisionRulePropertyConverter.dmnFromWB(wb);

        assertEquals(ID, dmn.getId());
        assertEquals(DESCRIPTION, dmn.getDescription());
    }
}
