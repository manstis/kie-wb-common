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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.List;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore("WIP")
@RunWith(MockitoJUnitRunner.class)
public class ItemDefinitionPropertyConverterTest {

    @Test
    public void testWbFromDMNWhenDMNIsNull() {
        assertNull(ItemDefinitionPropertyConverter.wbFromDMN(null));
    }

    @Test
    public void testWbFromDMNWhenDMNIsNotNull() {

        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);
        final String id = "id";
        final String name = "name";
        final Id expectedId = new Id(id);
        final Name expectedName = new Name(name);
        final String expectedTypeLanguage = "typeLanguage";
        final boolean expectedIsCollection = true;
        final String description = "description";
        final boolean expectedAllowOnlyVisualChange = false;
        final String qNameLocalPart = "qName local part";
        final Description expectedDescription = new Description(description);

        when(dmn.getId()).thenReturn(id);
        when(dmn.getName()).thenReturn(name);
        when(dmn.getTypeLanguage()).thenReturn(expectedTypeLanguage);
        when(dmn.getIsCollection()).thenReturn(expectedIsCollection);
        when(dmn.getDescription()).thenReturn(description);
        when(dmn.getTypeRef()).thenReturn(qNameLocalPart);

        final ItemDefinition actualItemDefinition = ItemDefinitionPropertyConverter.wbFromDMN(dmn);

        final Id actualId = actualItemDefinition.getId();
        final Name actualName = actualItemDefinition.getName();
        final String actualTypeLanguage = actualItemDefinition.getTypeLanguage();
        final boolean actualIsCollection = actualItemDefinition.isIsCollection();
        final Description actualDescription = actualItemDefinition.getDescription();
        final QName actualTypeRef = actualItemDefinition.getTypeRef();
        final boolean actualAllowOnlyVisualChange = actualItemDefinition.isAllowOnlyVisualChange();

        assertEquals(expectedId, actualId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedTypeLanguage, actualTypeLanguage);
        assertEquals(expectedIsCollection, actualIsCollection);
        assertEquals(expectedDescription, actualDescription);
        assertEquals(expectedAllowOnlyVisualChange, actualAllowOnlyVisualChange);
        assertEquals(qNameLocalPart, actualTypeRef.getLocalPart());
    }

    @Test
    public void testSetItemComponent() {

        final String id = "id";
        final String name = "name";
        final String typeLanguage = "typeLanguage";
        final boolean isCollection = true;
        final boolean isOnlyVisualChanges = false;
        final String description = "description";
        final ItemDefinition expectedWbChild = new ItemDefinition(new Id(id),
                                                                  new Description(description),
                                                                  new Name(name),
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  typeLanguage,
                                                                  isCollection,
                                                                  isOnlyVisualChanges);
        final ItemDefinition wb = new ItemDefinition();
        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);
        final JSITItemDefinition dmnChild = mock(JSITItemDefinition.class);

        when(dmnChild.getId()).thenReturn(id);
        when(dmnChild.getName()).thenReturn(name);
        when(dmnChild.getTypeLanguage()).thenReturn(typeLanguage);
        when(dmnChild.getIsCollection()).thenReturn(isCollection);
        when(dmnChild.getDescription()).thenReturn(description);
        when(dmnChild.getTypeRef()).thenReturn(null);
        when(dmn.getItemComponent()).thenReturn(new MockJsArrayLike<>(dmnChild));

        ItemDefinitionPropertyConverter.setItemComponent(wb, dmn);

        final List<ItemDefinition> expectedItemDefinitions = singletonList(expectedWbChild);
        final List<ItemDefinition> actualItemDefinitions = wb.getItemComponent();

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
        assertEquals(wb, actualItemDefinitions.get(0).getParent());
    }

    @Test
    public void testSetUnaryTestsWhenUnaryTestsIsNotNull() {

        final ItemDefinition wb = mock(ItemDefinition.class);
        final ArgumentCaptor<UnaryTests> argument = ArgumentCaptor.forClass(UnaryTests.class);
        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);
        final JSITUnaryTests dmnAllowedValues = mock(JSITUnaryTests.class);

        when(dmn.getAllowedValues()).thenReturn(dmnAllowedValues);

        ItemDefinitionPropertyConverter.setUnaryTests(wb, dmn);

        verify(wb).setAllowedValues(argument.capture());
        assertEquals(wb, argument.getValue().getParent());
    }

    @Test
    public void testSetUnaryTestsWhenUnaryTestsIsNull() {

        final ItemDefinition wb = mock(ItemDefinition.class);
        final UnaryTests wbAllowedValues = mock(UnaryTests.class);
        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);
        final JSITUnaryTests dmnAllowedValues = null;

        when(dmn.getAllowedValues()).thenReturn(dmnAllowedValues);

        ItemDefinitionPropertyConverter.setUnaryTests(wb, dmn);

        verify(wb, never()).setAllowedValues(wbAllowedValues);
        verify(wbAllowedValues, never()).setParent(wb);
    }

    @Test
    public void testWbDescriptionFromDMN() {

        final String description = "description";
        final Description expectedDescription = new Description(description);
        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);

        when(dmn.getDescription()).thenReturn(description);

        final Description actualDescription = ItemDefinitionPropertyConverter.wbDescriptionFromDMN(dmn);

        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    public void testWbTypeRefFromDMNWhenQNameIsUndefined() {

        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);
        final String dmnQName = null;

        when(dmn.getTypeRef()).thenReturn(dmnQName);

        final QName actualQName = ItemDefinitionPropertyConverter.wbTypeRefFromDMN(dmn);

        assertNull(actualQName);
    }

    @Test
    public void testWbTypeRefFromDMNWhenQNameIsNotUndefined() {

        final JSITItemDefinition dmn = mock(JSITItemDefinition.class);
        final String dmnQName = BuiltInType.STRING.asQName().getLocalPart();
        final QName expectedQName = BuiltInType.STRING.asQName();

        when(dmn.getTypeRef()).thenReturn(dmnQName);

        final QName actualQName = ItemDefinitionPropertyConverter.wbTypeRefFromDMN(dmn);

        assertEquals(actualQName, expectedQName);
    }

    @Test
    public void testWbChildFromDMNWhenWbChildIsNotNull() {

        final String id = "id";
        final String name = "name";
        final String typeLanguage = "typeLanguage";
        final boolean isCollection = true;
        final boolean isOnlyVisualChanges = false;
        final String description = "description";
        final ItemDefinition expectedWbParent = new ItemDefinition();
        final ItemDefinition expectedWbChild = new ItemDefinition(new Id(id),
                                                                  new Description(description),
                                                                  new Name(name),
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  typeLanguage,
                                                                  isCollection,
                                                                  isOnlyVisualChanges);
        final JSITItemDefinition dmnChild = mock(JSITItemDefinition.class);

        when(dmnChild.getId()).thenReturn(id);
        when(dmnChild.getName()).thenReturn(name);
        when(dmnChild.getTypeLanguage()).thenReturn(typeLanguage);
        when(dmnChild.getIsCollection()).thenReturn(isCollection);
        when(dmnChild.getDescription()).thenReturn(description);
        when(dmnChild.getTypeRef()).thenReturn(null);

        final ItemDefinition actualWbChild = ItemDefinitionPropertyConverter.wbChildFromDMN(expectedWbParent, dmnChild);
        final ItemDefinition actualParent = (ItemDefinition) actualWbChild.getParent();

        assertEquals(expectedWbChild, actualWbChild);
        assertEquals(expectedWbParent, actualParent);
    }

    @Test
    public void testWbChildFromDMNWhenWbChildIsNull() {

        final ItemDefinition expectedWbParent = new ItemDefinition();
        final JSITItemDefinition dmnChild = null;
        final ItemDefinition actualWbChild = ItemDefinitionPropertyConverter.wbChildFromDMN(expectedWbParent, dmnChild);

        assertNull(actualWbChild);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }
}
