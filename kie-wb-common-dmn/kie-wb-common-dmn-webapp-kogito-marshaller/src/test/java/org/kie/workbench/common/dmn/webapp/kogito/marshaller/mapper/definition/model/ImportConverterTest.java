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

import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.NameSpaceUtils;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImportConverterTest {

    @Test
    public void testWbFromDMNWhenGeneric() {
        final JSITImport dmn = new JSITImport();
        dmn.setImportType("cheese");
        final JSITDefinitions definitions = mock(JSITDefinitions.class);
        final PMMLDocumentMetadata pmmlDocument = mock(PMMLDocumentMetadata.class);

        final org.kie.workbench.common.dmn.api.definition.model.Import anImport = ImportConverter.wbFromDMN(dmn, definitions, pmmlDocument);

        assertNotNull(anImport);
    }

    @Test
    public void testWbFromDMNWhenDMNImport() {
        final JSITImport dmn = new JSITImport();
        dmn.setImportType(DMNImportTypes.DMN.getDefaultNamespace());
        final JSITDefinitions definitions = mock(JSITDefinitions.class);
        final PMMLDocumentMetadata pmmlDocument = mock(PMMLDocumentMetadata.class);
        final String key = "drools";
        final String value = "http://www.drools.org/kie/dmn/1.1";
        dmn.getOtherAttributes().put(new QName(key), value);

        when(definitions.getDrgElement()).thenReturn(new MockJsArrayLike<>(mock(JSITDRGElement.class), mock(JSITDRGElement.class)));
        when(definitions.getItemDefinition()).thenReturn(new MockJsArrayLike<>(mock(JSITItemDefinition.class), mock(JSITItemDefinition.class), mock(JSITItemDefinition.class)));

        final org.kie.workbench.common.dmn.api.definition.model.Import anImport = ImportConverter.wbFromDMN(dmn, definitions, pmmlDocument);
        final Map<String, String> nsContext = anImport.getNsContext();

        assertEquals(1, nsContext.size());
        assertEquals(value, nsContext.get(key));

        assertTrue(anImport instanceof ImportDMN);
        final ImportDMN dmnImport = (ImportDMN) anImport;

        assertEquals(2, dmnImport.getDrgElementsCount());
        assertEquals(3, dmnImport.getItemDefinitionsCount());
    }

    @Test
    public void testWbFromDMNWhenPMMLImport() {
        final JSITImport dmn = new JSITImport();
        dmn.setImportType(DMNImportTypes.PMML.getDefaultNamespace());
        final JSITDefinitions definitions = mock(JSITDefinitions.class);
        final PMMLDocumentMetadata pmmlDocument = mock(PMMLDocumentMetadata.class);

        when(pmmlDocument.getModels()).thenReturn(asList(mock(PMMLModelMetadata.class), mock(PMMLModelMetadata.class)));

        final org.kie.workbench.common.dmn.api.definition.model.Import anImport = ImportConverter.wbFromDMN(dmn, definitions, pmmlDocument);

        assertTrue(anImport instanceof ImportPMML);
        final ImportPMML pmmlImport = (ImportPMML) anImport;

        assertEquals(2, pmmlImport.getModelCount());
    }

    @Test
    public void testDmnFromWb() {

        final org.kie.workbench.common.dmn.api.definition.model.Import wb = new org.kie.workbench.common.dmn.api.definition.model.Import();
        final String key = "drools";
        final String value = "http://www.drools.org/kie/dmn/1.1";
        wb.getNsContext().put(key, value);

        final JSITImport anImport = ImportConverter.dmnFromWb(wb);
        final Map<String, String> nsContext = NameSpaceUtils.extractNamespacesKeyedByPrefix(anImport);

        assertEquals(1, nsContext.size());
        assertEquals(value, nsContext.get(key));
    }
}
