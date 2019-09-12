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
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImportedItemDefinitionConverterTest {

    @Test
    public void testWbFromDMN() {

        final JSITItemDefinition itemDefinition = new JSITItemDefinition();
        final JSITItemDefinition itemComponent1 = new JSITItemDefinition();
        final JSITItemDefinition itemComponent2 = new JSITItemDefinition();
        final JSITItemDefinition itemComponent3 = new JSITItemDefinition();

        itemDefinition.setName("tPerson");
        itemDefinition.setTypeRef(null);
        itemDefinition.getItemComponent().setAt(0, itemComponent1);
        itemDefinition.getItemComponent().setAt(1, itemComponent2);
        itemDefinition.getItemComponent().setAt(2, itemComponent3);

        itemComponent1.setName("id");
        itemComponent1.setTypeRef("tUUID");

        itemComponent2.setName("name");
        itemComponent2.setTypeRef("string");

        itemComponent3.setName("age");
        itemComponent3.setTypeRef("number");

        final ItemDefinition actualItemDefinition = ImportedItemDefinitionConverter.wbFromDMN(itemDefinition, "model");

        assertEquals("model.tPerson", actualItemDefinition.getName().getValue());
        assertNull(actualItemDefinition.getTypeRef());
        assertTrue(actualItemDefinition.isAllowOnlyVisualChange());

        assertEquals(3, actualItemDefinition.getItemComponent().size());

        final ItemDefinition actualItemDefinition1 = actualItemDefinition.getItemComponent().get(0);
        assertEquals("model.id", actualItemDefinition1.getName().getValue());
        assertEquals("model.tUUID", actualItemDefinition1.getTypeRef().getLocalPart());
        assertTrue(actualItemDefinition1.isAllowOnlyVisualChange());

        final ItemDefinition actualItemDefinition2 = actualItemDefinition.getItemComponent().get(1);
        assertEquals("model.name", actualItemDefinition2.getName().getValue());
        assertEquals("string", actualItemDefinition2.getTypeRef().getLocalPart());
        assertTrue(actualItemDefinition2.isAllowOnlyVisualChange());

        final ItemDefinition actualItemDefinition3 = actualItemDefinition.getItemComponent().get(2);
        assertEquals("model.age", actualItemDefinition3.getName().getValue());
        assertEquals("number", actualItemDefinition3.getTypeRef().getLocalPart());
        assertTrue(actualItemDefinition3.isAllowOnlyVisualChange());
    }
}
