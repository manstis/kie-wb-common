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

import jsinterop.base.JsArrayLike;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITAttachment;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNExternalLinksToExtensionElementsTest {

    @Test
    public void testLoadExternalLinksFromExtensionElements() {

        final JSITDRGElement source = mock(JSITDRGElement.class);
        final org.kie.workbench.common.dmn.api.definition.model.DRGElement target = mock(org.kie.workbench.common.dmn.api.definition.model.DRGElement.class);

        final JSITDRGElement.JSIExtensionElements extensionElements = mock(JSITDRGElement.JSIExtensionElements.class);
        final JsArrayLike<Object> externalLinks = new MockJsArrayLike<>();

        final String linkDescription1 = "l1";
        final String url1 = "url1";
        final JSITAttachment external1 = createExternalLinkMock(linkDescription1, url1);
        externalLinks.setAt(0, external1);

        final String linkDescription2 = "l2";
        final String url2 = "url2";
        final JSITAttachment external2 = createExternalLinkMock(linkDescription2, url2);
        externalLinks.setAt(1, external2);

        when(extensionElements.getAny()).thenReturn(externalLinks);
        when(source.getExtensionElements()).thenReturn(extensionElements);

        final DocumentationLinksHolder linksHolder = mock(DocumentationLinksHolder.class);
        final DocumentationLinks links = new DocumentationLinks();
        when(linksHolder.getValue()).thenReturn(links);
        when(target.getLinksHolder()).thenReturn(linksHolder);

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(source, target);

        assertEquals(2, links.getLinks().size());

        compare(links.getLinks().get(0), linkDescription1, url1);
        compare(links.getLinks().get(1), linkDescription2, url2);
    }

    private void compare(final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink converted,
                         final String description,
                         final String url) {

        assertEquals(url, converted.getUrl());
        assertEquals(description, converted.getDescription());
    }

    private JSITAttachment createExternalLinkMock(final String description, final String url) {

        final JSITAttachment attachment = new JSITAttachment();
        attachment.setName(description);
        attachment.setUrl(url);
        return attachment;
    }

    @Test
    public void testLoadExternalLinksIntoExtensionElements() {

        final org.kie.workbench.common.dmn.api.definition.model.DRGElement source = mock(org.kie.workbench.common.dmn.api.definition.model.DRGElement.class);
        final JSITDRGElement target = mock(JSITDRGElement.class);

        final DocumentationLinksHolder linksHolder = mock(DocumentationLinksHolder.class);
        when(source.getLinksHolder()).thenReturn(linksHolder);
        final DocumentationLinks documentationLinks = new DocumentationLinks();

        final String url1 = "url1";
        final String description1 = "desc1";
        final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink link1 = createWBExternalLinkMock(description1, url1);
        documentationLinks.addLink(link1);

        final String url2 = "url2";
        final String description2 = "desc2";
        final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink link2 = createWBExternalLinkMock(description2, url2);
        documentationLinks.addLink(link2);

        when(linksHolder.getValue()).thenReturn(documentationLinks);
        final JSITDMNElement.JSIExtensionElements extensionElements = mock(JSITDMNElement.JSIExtensionElements.class);
        when(target.getExtensionElements()).thenReturn(extensionElements);
        final JsArrayLike<Object> externalLinks = new MockJsArrayLike<>();
        when(extensionElements.getAny()).thenReturn(externalLinks);

        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, target);

        assertEquals(2, externalLinks.getLength());

        compare((JSITAttachment) externalLinks.getAt(0), description1, url1);
        compare((JSITAttachment) externalLinks.getAt(1), description2, url2);
    }

    private void compare(final JSITAttachment converted,
                         final String description,
                         final String url) {
        assertEquals(url, converted.getUrl());
        assertEquals(description, converted.getName());
    }

    private static org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink createWBExternalLinkMock(final String description,
                                                                                                          final String url) {
        final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink external = mock(org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink.class);
        when(external.getDescription()).thenReturn(description);
        when(external.getUrl()).thenReturn(url);

        return external;
    }

    @Test
    public void testGetOrCreateExtensionElements() {

        final JSITDRGElement element = mock(JSITDRGElement.class);

        final JSITDRGElement.JSIExtensionElements result = DMNExternalLinksToExtensionElements.getOrCreateExtensionElements(element);

        assertNotNull(result);

        final JSITDRGElement.JSIExtensionElements existingElements = mock(JSITDRGElement.JSIExtensionElements.class);
        when(element.getExtensionElements()).thenReturn(existingElements);

        final JSITDMNElement.JSIExtensionElements actual = DMNExternalLinksToExtensionElements.getOrCreateExtensionElements(element);

        assertEquals(actual, existingElements);
    }
}