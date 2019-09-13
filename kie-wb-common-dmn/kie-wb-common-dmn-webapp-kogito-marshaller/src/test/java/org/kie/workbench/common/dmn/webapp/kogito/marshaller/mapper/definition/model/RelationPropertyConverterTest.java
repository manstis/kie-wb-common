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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@Ignore("WIP")
@RunWith(MockitoJUnitRunner.class)
public class RelationPropertyConverterTest {

    private static final String RELATION_UUID = "r-uuid";

    private static final String RELATION_DESCRIPTION = "r-description";

    private static final String RELATION_QNAME_LOCALPART = "r-local";

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
        final JSITRelation dmn = new JSITRelation();
        final JSITInformationItem informationItem = new JSITInformationItem();
        final JSITList list = new JSITList();
        final JSITLiteralExpression literalExpression = new JSITLiteralExpression();
        literalExpression.setId(EXPRESSION_UUID);
        list.getExpression().setAt(0, literalExpression);

        dmn.setId(RELATION_UUID);
        dmn.setDescription(RELATION_DESCRIPTION);
        dmn.setTypeRef(RELATION_QNAME_LOCALPART);
        dmn.getColumn().setAt(0, informationItem);
        dmn.getRow().setAt(0, list);

        final Relation wb = RelationPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(RELATION_UUID);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(RELATION_DESCRIPTION);
        assertThat(wb.getTypeRef()).isNotNull();
        assertThat(wb.getTypeRef().getLocalPart()).isEqualTo(RELATION_QNAME_LOCALPART);
        assertThat(wb.getColumn()).isNotNull();
        assertThat(wb.getColumn().size()).isEqualTo(1);
        assertThat(wb.getColumn().get(0)).isNotNull();
        assertThat(wb.getRow()).isNotNull();
        assertThat(wb.getRow().size()).isEqualTo(1);
        assertThat(wb.getRow().get(0)).isNotNull();
        assertThat(wb.getRow().get(0).getExpression()).isNotNull();
        assertThat(wb.getRow().get(0).getExpression().size()).isEqualTo(1);
        assertThat(wb.getRow().get(0).getExpression().get(0).getId().getValue()).isEqualTo(EXPRESSION_UUID);

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb.getRow().get(0).getExpression().get(0));
    }

    @Test
    public void testDMNFromWB() {
        final Relation wb = new Relation();
        final InformationItem informationItem = new InformationItem();
        final List list = new List();
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getComponentWidths().set(0, 200.0);
        literalExpression.getId().setValue(EXPRESSION_UUID);
        list.getExpression().add(literalExpression);

        wb.getId().setValue(RELATION_UUID);
        wb.getDescription().setValue(RELATION_DESCRIPTION);
        wb.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI,
                                                                              RELATION_QNAME_LOCALPART));
        wb.getColumn().add(informationItem);
        wb.getRow().add(list);

        final JSITRelation dmn = RelationPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(RELATION_UUID);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(RELATION_DESCRIPTION);
        assertThat(dmn.getTypeRef()).isNotNull();
        assertThat(dmn.getTypeRef()).isEqualTo(RELATION_QNAME_LOCALPART);
        assertThat(dmn.getColumn()).isNotNull();
        assertThat(dmn.getColumn().getLength()).isEqualTo(1);
        assertThat(dmn.getColumn().getAt(0)).isNotNull();
        assertThat(dmn.getRow()).isNotNull();
        assertThat(dmn.getRow().getLength()).isEqualTo(1);
        assertThat(dmn.getRow().getAt(0)).isNotNull();
        assertThat(dmn.getRow().getAt(0).getExpression()).isNotNull();
        assertThat(dmn.getRow().getAt(0).getExpression().getLength()).isEqualTo(1);
        assertThat(dmn.getRow().getAt(0).getExpression().getAt(0).getId()).isEqualTo(EXPRESSION_UUID);

        verify(componentWidthsConsumer).accept(componentWidthsCaptor.capture());

        final JSITComponentWidths componentWidths = componentWidthsCaptor.getValue();
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths.getDmnElementRef()).isEqualTo(EXPRESSION_UUID);
        assertThat(componentWidths.getWidth().getLength()).isEqualTo(literalExpression.getRequiredComponentWidthCount());
        assertThat(componentWidths.getWidth().getAt(0)).isEqualTo(200.0);
    }
}
