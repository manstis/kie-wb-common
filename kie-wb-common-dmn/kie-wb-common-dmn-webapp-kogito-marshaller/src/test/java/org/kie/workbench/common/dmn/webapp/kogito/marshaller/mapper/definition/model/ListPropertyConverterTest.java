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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListPropertyConverterTest {

    private static final String LIST_UUID = "l-uuid";

    private static final String LIST_DESCRIPTION = "l-description";

    private static final String LIST_QNAME_LOCALPART = "l-local";

    private static final String EXPRESSION_UUID = "uuid";

    @Mock
    private BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer;

    @Mock
    private Consumer<JSITComponentWidths> componentWidthsConsumer;

    @Captor
    private ArgumentCaptor<HasComponentWidths> hasComponentWidthsCaptor;

    @Captor
    private ArgumentCaptor<JSITComponentWidths> componentWidthsCaptor;

    @Test
    public void testWBFromDMN() {
        final JSITList dmn = new JSITList();
        final JSITLiteralExpression literalExpression = new JSITLiteralExpression();
        literalExpression.setId(EXPRESSION_UUID);

        dmn.setId(LIST_UUID);
        dmn.setDescription(LIST_DESCRIPTION);
        dmn.setTypeRef(LIST_QNAME_LOCALPART);
        dmn.getExpression().setAt(0, literalExpression);

        final List wb = ListPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(LIST_UUID);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(LIST_DESCRIPTION);
        assertThat(wb.getTypeRef()).isNotNull();
        assertThat(wb.getTypeRef().getLocalPart()).isEqualTo(LIST_QNAME_LOCALPART);
        assertThat(wb.getExpression()).isNotNull();
        assertThat(wb.getExpression().size()).isEqualTo(1);
        assertThat(wb.getExpression().get(0).getId().getValue()).isEqualTo(EXPRESSION_UUID);

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb.getExpression().get(0));
    }

    @Test
    public void testDMNFromWB() {
        final List wb = new List();
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getComponentWidths().set(0, 200.0);
        literalExpression.getId().setValue(EXPRESSION_UUID);

        wb.getId().setValue(LIST_UUID);
        wb.getDescription().setValue(LIST_DESCRIPTION);
        wb.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI,
                                                                              LIST_QNAME_LOCALPART));
        wb.getExpression().add(literalExpression);

        final JSITList dmn = ListPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(LIST_UUID);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(LIST_DESCRIPTION);
        assertThat(dmn.getTypeRef()).isNotNull();
        assertThat(dmn.getTypeRef()).isEqualTo(LIST_QNAME_LOCALPART);
        assertThat(dmn.getExpression()).isNotNull();
        assertThat(dmn.getExpression().getLength()).isEqualTo(1);
        assertThat(dmn.getExpression().getAt(0).getId()).isEqualTo(EXPRESSION_UUID);

        verify(componentWidthsConsumer).accept(componentWidthsCaptor.capture());

        final JSITComponentWidths componentWidths = componentWidthsCaptor.getValue();
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths.getDmnElementRef()).isEqualTo(EXPRESSION_UUID);
        assertThat(componentWidths.getWidth().getLength()).isEqualTo(literalExpression.getRequiredComponentWidthCount());
        assertThat(componentWidths.getWidth().getAt(0)).isEqualTo(200.0);
    }
}
