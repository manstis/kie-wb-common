/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDI;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.AssociationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DecisionConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DecisionServiceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DefinitionsConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.InputDataConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.TextAnnotationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.DMNMarshallerUtils;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.WrapperUtils.getWrappedJSIDMNEdge;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.WrapperUtils.getWrappedJSIDMNShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.WrapperUtils.getWrappedJSITAssociation;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.WrapperUtils.getWrappedJSITComponentsWidthsExtension;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.WrapperUtils.getWrappedJSITDRGElement;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.WrapperUtils.getWrappedJSITTextAnnotation;

@ApplicationScoped
public class DMNMarshallerKogitoMarshaller {

    private InputDataConverter inputDataConverter;
    private DecisionConverter decisionConverter;
    private BusinessKnowledgeModelConverter bkmConverter;
    private KnowledgeSourceConverter knowledgeSourceConverter;
    private TextAnnotationConverter textAnnotationConverter;
    private DecisionServiceConverter decisionServiceConverter;

    protected DMNMarshallerKogitoMarshaller() {
        this(null);
    }

    @Inject
    public DMNMarshallerKogitoMarshaller(final FactoryManager factoryManager) {
        this.inputDataConverter = new InputDataConverter(factoryManager);
        this.decisionConverter = new DecisionConverter(factoryManager);
        this.bkmConverter = new BusinessKnowledgeModelConverter(factoryManager);
        this.knowledgeSourceConverter = new KnowledgeSourceConverter(factoryManager);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager);
        this.decisionServiceConverter = new DecisionServiceConverter(factoryManager);
    }

    @PostConstruct
    public void init() {
        MainJs.initializeJsInteropConstructors(MainJs.getConstructorsMap());
    }

    // ==================================
    // MARSHALL
    // ==================================

    @SuppressWarnings("unchecked")
    public JSITDefinitions marshall(final Graph<?, Node<View, ?>> graph) {
        final Map<String, JSITDRGElement> nodes = new HashMap<>();
        final Map<String, JSITTextAnnotation> textAnnotations = new HashMap<>();
        final Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) DMNMarshallerUtils.findDMNDiagramRoot(graph);
        final Definitions definitionsStunnerPojo = dmnDiagramRoot.getContent().getDefinition().getDefinitions();
        final List<JSIDMNEdge> dmnEdges = new ArrayList<>();

        cleanImportedItemDefinitions(definitionsStunnerPojo);

        final JSITDefinitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo);
        if (Objects.isNull(definitions.getExtensionElements())) {
            JSITDMNElement.JSIExtensionElements jsiExtensionElements = new JSITDMNElement.JSIExtensionElements();
            definitions.setExtensionElements(jsiExtensionElements);
        }

        if (Objects.isNull(definitions.getDMNDI())) {
            definitions.setDMNDI(new JSIDMNDI());
        }
        final JSIDMNDiagram dmnDDDMNDiagram = new JSIDMNDiagram();
        // TODO {gcardosi} add because  present in original json
        dmnDDDMNDiagram.setDMNDiagramElement(new ArrayList<>());
        definitions.getDMNDI().addDMNDiagram(dmnDDDMNDiagram);

        //Convert relative positioning to absolute
        for (Node<?, ?> node : graph.nodes()) {
            PointUtils.convertToAbsoluteBounds(node);
        }

        //Setup callback for marshalling ComponentWidths
        if (Objects.isNull(dmnDDDMNDiagram.getExtension())) {
            dmnDDDMNDiagram.setExtension(new JSIDiagramElement.JSIExtension());
        }
        final JSITComponentsWidthsExtension componentsWidthsExtension = new JSITComponentsWidthsExtension();
        final JSIDiagramElement.JSIExtension extension = dmnDDDMNDiagram.getExtension();
        JSITComponentsWidthsExtension wrappedComponentsWidthsExtension = getWrappedJSITComponentsWidthsExtension(componentsWidthsExtension);
        extension.addAny(wrappedComponentsWidthsExtension);

        final Consumer<JSITComponentWidths> componentWidthsConsumer = (cw) ->  componentsWidthsExtension.addComponentWidths(cw);

        //Iterate Graph processing nodes..
        for (Node<?, ?> node : graph.nodes()) {
            if (node.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement n = (DRGElement) view.getDefinition();
                    if (view.getDefinition() instanceof DynamicReadOnly) {
                        final DynamicReadOnly def = (DynamicReadOnly) view.getDefinition();
                        if (!def.isAllowOnlyVisualChange()) {
                            nodes.put(n.getId().getValue(),
                                      stunnerToDMN(node,
                                                   componentWidthsConsumer));
                        }
                    } else {
                        nodes.put(n.getId().getValue(),
                                  stunnerToDMN(node,
                                               componentWidthsConsumer));
                    }

                    String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    dmnDDDMNDiagram.addDMNDiagramElement(getWrappedJSIDMNShape((View<? extends DMNElement>) view,
                                                                                              namespaceURI));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    final TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node,
                                                                            componentWidthsConsumer));
                    String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                    dmnDDDMNDiagram.addDMNDiagramElement(getWrappedJSIDMNShape((View<? extends DMNElement>) view,
                                                                                              namespaceURI));

                    final List<JSITAssociation> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    for (int i = 0; i < associations.size(); i++) {
                        JSITAssociation wrappedJSITAssociation = getWrappedJSITAssociation(Js.uncheckedCast(associations.get(i)));
                        definitions.addArtifact(wrappedJSITAssociation);
                    }
                }

                // DMNDI Edge management.
                final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
                for (Edge<?, ?> e : inEdges) {
                    if (e.getContent() instanceof ViewConnector) {
                        final ViewConnector connectionContent = (ViewConnector) e.getContent();
                        if (connectionContent.getSourceConnection().isPresent() && connectionContent.getTargetConnection().isPresent()) {
                            Point2D sourcePoint = ((Connection) connectionContent.getSourceConnection().get()).getLocation();
                            Point2D targetPoint = ((Connection) connectionContent.getTargetConnection().get()).getLocation();
                            if (sourcePoint == null) { // If the "connection source/target location is null" assume it's the centre of the shape.
                                final Node<?, ?> sourceNode = e.getSourceNode();
                                final View<?> sourceView = (View<?>) sourceNode.getContent();
                                double xSource = xOfBound(upperLeftBound(sourceView));
                                double ySource = yOfBound(upperLeftBound(sourceView));
                                if (sourceView.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) sourceView.getDefinition();
                                    xSource += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    ySource += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                sourcePoint = Point2D.create(xSource, ySource);
                            } else { // If it is non-null it is relative to the source/target shape location.
                                final Node<?, ?> sourceNode = e.getSourceNode();
                                final View<?> sourceView = (View<?>) sourceNode.getContent();
                                double xSource = xOfBound(upperLeftBound(sourceView));
                                double ySource = yOfBound(upperLeftBound(sourceView));
                                sourcePoint = Point2D.create(xSource + sourcePoint.getX(), ySource + sourcePoint.getY());
                            }
                            if (targetPoint == null) { // If the "connection source/target location is null" assume it's the centre of the shape.
                                double xTarget = xOfBound(upperLeftBound(view));
                                double yTarget = yOfBound(upperLeftBound(view));
                                if (view.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) view.getDefinition();
                                    xTarget += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    yTarget += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                targetPoint = Point2D.create(xTarget, yTarget);
                            } else { // If it is non-null it is relative to the source/target shape location.
                                final double xTarget = xOfBound(upperLeftBound(view));
                                final double yTarget = yOfBound(upperLeftBound(view));
                                targetPoint = Point2D.create(xTarget + targetPoint.getX(), yTarget + targetPoint.getY());
                            }

                            final JSIDMNEdge dmnEdge = new JSIDMNEdge();
                            // DMNDI edge elementRef is uuid of Stunner edge,
                            // with the only exception when edge contains as content a DMN Association (Association is an edge)
                            String uuid = e.getUUID();
                            if (e.getContent() instanceof View<?>) {
                                final View<?> edgeView = (View<?>) e.getContent();
                                if (edgeView.getDefinition() instanceof Association) {
                                    uuid = ((Association) edgeView.getDefinition()).getId().getValue();
                                }
                            }
                            dmnEdge.setId("dmnedge-" + uuid);
                            String namespaceURI = definitionsStunnerPojo.getDefaultNamespace();
                            dmnEdge.setDmnElementRef(new QName(namespaceURI,
                                                               uuid,
                                                               XMLConstants.DEFAULT_NS_PREFIX));
                            dmnEdge.addWaypoint(PointUtils.point2dToDMNDIPoint(sourcePoint));
                            for (ControlPoint cp : connectionContent.getControlPoints()) {
                                dmnEdge.addWaypoint(PointUtils.point2dToDMNDIPoint(cp.getLocation()));
                            }
                            dmnEdge.addWaypoint(PointUtils.point2dToDMNDIPoint(targetPoint));
                            dmnEdges.add(dmnEdge);
                        }
                    }
                }
            }
        }
        nodes.values().forEach(n -> {
            String localPart = "UNKNOWN";
            if (JSITBusinessKnowledgeModel.instanceOf(n)) {
                localPart = "businessKnowledgeModel";
            } else if (JSITDecision.instanceOf(n)) {
                localPart = "decision";
            } else if (JSITDecisionService.instanceOf(n)) {
                localPart = "decisionService";
            } else if (JSITInputData.instanceOf(n)) {
                localPart = "inputData";
            } else if (JSITKnowledgeSource.instanceOf(n)) {
                localPart = "knowledgeSource";
            }
            JSITDRGElement toAdd = getWrappedJSITDRGElement(n, "dmn", localPart);
            definitions.addDrgElement(toAdd);
        });
        textAnnotations.values().forEach(text -> {
            JSITTextAnnotation wrappedText = getWrappedJSITTextAnnotation(text);
            definitions.addArtifact(wrappedText);
        });
        for (int i = 0; i < dmnEdges.size(); i++) {
            dmnDDDMNDiagram.addDMNDiagramElement(getWrappedJSIDMNEdge(Js.uncheckedCast(dmnEdges.get(i))));
        }
        return definitions;
    }

    void cleanImportedItemDefinitions(final Definitions definitions) {
        definitions.getItemDefinition().removeIf(ItemDefinition::isAllowOnlyVisualChange);
    }

    @SuppressWarnings("unchecked")
    public JSITDRGElement stunnerToDMN(final Node<?, ?> node,
                                       final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        if (node.getContent() instanceof View<?>) {
            View<?> view = (View<?>) node.getContent();
            if (view.getDefinition() instanceof InputData) {
                return inputDataConverter.dmnFromNode((Node<View<InputData>, ?>) node,
                                                      componentWidthsConsumer);
            } else if (view.getDefinition() instanceof Decision) {
                return decisionConverter.dmnFromNode((Node<View<Decision>, ?>) node,
                                                     componentWidthsConsumer);
            } else if (view.getDefinition() instanceof BusinessKnowledgeModel) {
                return bkmConverter.dmnFromNode((Node<View<BusinessKnowledgeModel>, ?>) node,
                                                componentWidthsConsumer);
            } else if (view.getDefinition() instanceof KnowledgeSource) {
                return knowledgeSourceConverter.dmnFromNode((Node<View<KnowledgeSource>, ?>) node,
                                                            componentWidthsConsumer);
            } else if (view.getDefinition() instanceof DecisionService) {
                return decisionServiceConverter.dmnFromNode((Node<View<DecisionService>, ?>) node,
                                                            componentWidthsConsumer);
            } else {
                throw new UnsupportedOperationException("Unsupported View type [" + view.getDefinition().getClass().getName() + "]");
            }
        }
        throw new RuntimeException("wrong diagram structure to marshall");
    }
}
