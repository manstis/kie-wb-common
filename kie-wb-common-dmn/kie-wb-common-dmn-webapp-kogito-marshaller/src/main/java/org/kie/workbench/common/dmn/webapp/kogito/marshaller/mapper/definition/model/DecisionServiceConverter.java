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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.DMNElementReference;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class DecisionServiceConverter implements NodeConverter<JSITDecisionService, org.kie.workbench.common.dmn.api.definition.model.DecisionService> {

    private FactoryManager factoryManager;

    public DecisionServiceConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<DecisionService>, ?> nodeFromDMN(final JSITDecisionService dmn,
                                                      final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<DecisionService>, ?> node = (Node<View<DecisionService>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                               getDefinitionId(DecisionService.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(), dmn);
        final List<DMNElementReference> outputDecision = Stream.of(dmn.getOutputDecision().asArray()).map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> encapsulatedDecision = Stream.of(dmn.getEncapsulatedDecision().asArray()).map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> inputDecision = Stream.of(dmn.getInputDecision().asArray()).map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final List<DMNElementReference> inputData = Stream.of(dmn.getInputData().asArray()).map(DMNElementReferenceConverter::wbFromDMN).collect(Collectors.toList());
        final DecisionService decisionService = new DecisionService(id,
                                                                    description,
                                                                    name,
                                                                    informationItem,
                                                                    outputDecision,
                                                                    encapsulatedDecision,
                                                                    inputDecision,
                                                                    inputData,
                                                                    new BackgroundSet(),
                                                                    new FontSet(),
                                                                    new DecisionServiceRectangleDimensionsSet(),
                                                                    new DecisionServiceDividerLineY());
        node.getContent().setDefinition(decisionService);

        if (informationItem != null) {
            informationItem.setParent(decisionService);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, decisionService);

        return node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSITDecisionService dmnFromNode(final Node<View<DecisionService>, ?> node,
                                           final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final DecisionService source = node.getContent().getDefinition();
        final JSITDecisionService ds = new JSITDecisionService();
        ds.setId(source.getId().getValue());
        ds.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        ds.setName(source.getName().getValue());
        final JSITInformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        ds.setVariable(variable);

        final List<JSITDMNElementReference> existing_outputDecision = source.getOutputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> existing_encapsulatedDecision = source.getEncapsulatedDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> existing_inputDecision = source.getInputDecision().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> existing_inputData = source.getInputData().stream().map(DMNElementReferenceConverter::dmnFromWB).collect(Collectors.toList());
        final List<JSITDMNElementReference> candidate_outputDecision = new ArrayList<>();
        final List<JSITDMNElementReference> candidate_encapsulatedDecision = new ArrayList<>();
        final List<JSITDMNElementReference> candidate_inputDecision = new ArrayList<>();
        final List<JSITDMNElementReference> candidate_inputData = new ArrayList<>();

        final List<InputData> reqInputs = new ArrayList<>();
        final List<Decision> reqDecisions = new ArrayList<>();

        // DMN spec table 2: Requirements connection rules
        final List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        for (Edge<?, ?> e : outEdges) {
            if (e.getContent() instanceof Child) {
                @SuppressWarnings("unchecked")
                final Node<View<?>, ?> targetNode = e.getTargetNode();
                final View<?> targetNodeView = targetNode.getContent();
                if (targetNodeView.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) targetNodeView.getDefinition();
                    if (drgElement instanceof Decision) {
                        final Decision decision = (Decision) drgElement;
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(decision.getId().getValue()).toString());
                        if (isOutputDecision(targetNode.getContent(), node.getContent())) {
                            candidate_outputDecision.add(ri);
                        } else {
                            candidate_encapsulatedDecision.add(ri);
                        }
                        inspectDecisionForDSReqs(targetNode, reqInputs, reqDecisions);
                    } else {
                        throw new UnsupportedOperationException("wrong model definition: a DecisionService is expected to encapsulate only Decision");
                    }
                }
            } else if (e.getContent() instanceof View && ((View) e.getContent()).getDefinition() instanceof KnowledgeRequirement) {
                // this was taken care by the receiving Decision or BKM.
            } else {
                throw new UnsupportedOperationException("wrong model definition.");
            }
        }
        reqInputs.stream()
                .sorted(Comparator.comparing(x -> x.getName().getValue()))
                .map(x -> {
                    final JSITDMNElementReference ri = new JSITDMNElementReference();
                    ri.setHref(new StringBuilder("#").append(x.getId().getValue()).toString());
                    return ri;
                })
                .forEach(candidate_inputData::add);
        reqDecisions.stream()
                .sorted(Comparator.comparing(x -> x.getName().getValue()))
                .map(x -> {
                    final JSITDMNElementReference ri = new JSITDMNElementReference();
                    ri.setHref(new StringBuilder("#").append(x.getId().getValue()).toString());
                    return ri;
                })
                .forEach(candidate_inputDecision::add);
        for (JSITDMNElementReference er : candidate_outputDecision) {
            candidate_inputDecision.removeIf(x -> x.getHref().equals(er.getHref()));
        }
        for (JSITDMNElementReference er : candidate_encapsulatedDecision) {
            candidate_inputDecision.removeIf(x -> x.getHref().equals(er.getHref()));
        }

        reconcileExistingAndCandidate(ds.getInputData(), existing_inputData, candidate_inputData);
        reconcileExistingAndCandidate(ds.getInputDecision(), existing_inputDecision, candidate_inputDecision);
        reconcileExistingAndCandidate(ds.getEncapsulatedDecision(), existing_encapsulatedDecision, candidate_encapsulatedDecision);
        reconcileExistingAndCandidate(ds.getOutputDecision(), existing_outputDecision, candidate_outputDecision);

        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, ds);

        return ds;
    }

    private void reconcileExistingAndCandidate(final JsArrayLike<JSITDMNElementReference> targetList,
                                               final List<JSITDMNElementReference> existingList,
                                               final List<JSITDMNElementReference> candidateList) {
        final List<JSITDMNElementReference> existing = new ArrayList<>(existingList);
        final List<JSITDMNElementReference> candidate = new ArrayList<>(candidateList);
        for (JSITDMNElementReference e : existing) {
            boolean existingIsAlsoCandidate = candidate.removeIf(er -> er.getHref().equals(e.getHref()));
            if (existingIsAlsoCandidate) {
                JsUtils.add(targetList, e);
            }
        }
        for (JSITDMNElementReference c : candidate) {
            JsUtils.add(targetList, c);
        }
    }

    @SuppressWarnings("unchecked")
    private void inspectDecisionForDSReqs(final Node<View<?>, ?> targetNode,
                                          final List<InputData> reqInputs,
                                          final List<Decision> reqDecisions) {
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        reqDecisions.add((Decision) drgElement);
                    } else if (drgElement instanceof InputData) {
                        reqInputs.add((InputData) drgElement);
                    }
                }
            }
        }
    }

    private static boolean isOutputDecision(final View<?> childView,
                                            final View<DecisionService> decisionServiceView) {
        //ChildViewY is absolute
        //DecisionServiceViewY is absolute
        //DecisionServiceViewLineY is relative to the DecisionService
        final double childViewY = childView.getBounds().getUpperLeft().getY();
        final double decisionServiceViewY = decisionServiceView.getBounds().getUpperLeft().getY();
        final double decisionServiceViewLineY = decisionServiceView.getDefinition().getDividerLineY().getValue();
        return childViewY < decisionServiceViewY + decisionServiceViewLineY;
    }
}