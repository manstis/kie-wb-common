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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.mocks;

import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactoryImpl;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactory;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactoryImpl;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNMarshallerKogitoUnmarshallerTest;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class FactoryManagerMock implements FactoryManager {

    private static final Logger LOG = Logger.getLogger(DMNMarshallerKogitoUnmarshallerTest.class.getName());

    private final NodeFactory<Object> nodeFactory;
    private final EdgeFactory edgeFactory;
    private final DMNGraphFactory dmnGraphFactory;
    private final DMNDiagramFactory dmnDiagramFactory;

    public FactoryManagerMock() {
        final DefinitionUtils definitionUtils = new DefinitionUtilsMock();
        final DefinitionManager definitionManager = new DefinitionManagerMock();
        final GraphCommandManager graphCommandManager = new GraphCommandManagerImpl(null, null, null);
        final GraphCommandFactory graphCommandFactory = new GraphCommandFactory();
        final GraphIndexBuilder<?> indexBuilder = new MapIndexBuilder();

        this.nodeFactory = new NodeFactoryImpl(definitionUtils) {
            @Override
            protected void appendLabels(final Set<String> target,
                                        final Object definition) {
                //TODO {manstis} Do nothing for now.
            }
        };
        this.edgeFactory = new EdgeFactoryImpl(definitionManager) {
            @Override
            protected void appendLabels(final Set<String> target,
                                        final Object definition) {
                //TODO {manstis} Do nothing for now.
            }
        };
        this.dmnGraphFactory = new DMNGraphFactoryImpl(definitionManager,
                                                       this,
                                                       graphCommandManager,
                                                       graphCommandFactory,
                                                       indexBuilder);
        this.dmnDiagramFactory = new DMNDiagramFactoryImpl();
    }

    @Override
    public <T> T newDefinition(final String id) {
        LOG.info("id=" + id);

        return null;
    }

    @Override
    public Element<?> newElement(final String uuid,
                                 final String id) {
        LOG.info("uuid=" + uuid + ", id=" + id);

        if (Objects.equals(id, getDefinitionId(DMNDiagram.class))) {
            LOG.info("DMNDiagram detected...");
            final Object definition = new DMNDiagram();
            return nodeFactory.build(id, definition);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends Metadata, D extends Diagram> D newDiagram(final String name,
                                                                final String definitionSetId,
                                                                final M metadata) {
        final Graph<DefinitionSet, ?> graph = dmnGraphFactory.build(name, DMNDefinitionSet.class.getName(), metadata);
        return (D) dmnDiagramFactory.build(name, metadata, graph);
    }

    @Override
    public FactoryRegistry registry() {
        return null;
    }
}
