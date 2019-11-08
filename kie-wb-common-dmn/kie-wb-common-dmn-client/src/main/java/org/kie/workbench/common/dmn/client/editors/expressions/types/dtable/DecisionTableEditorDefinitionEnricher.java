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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorModelEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.util.TypeRefUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;

@ApplicationScoped
public class DecisionTableEditorDefinitionEnricher implements ExpressionEditorModelEnricher<DecisionTable> {

    private SessionManager sessionManager;
    private DMNGraphUtils dmnGraphUtils;
    private ItemDefinitionUtils itemDefinitionUtils;

    static class InputClauseRequirement {

        String text;
        QName typeRef;

        InputClauseRequirement(final String text,
                               final QName typeRef) {
            this.text = text;
            this.typeRef = typeRef;
        }
    }

    public DecisionTableEditorDefinitionEnricher() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinitionEnricher(final SessionManager sessionManager,
                                                 final DMNGraphUtils dmnGraphUtils,
                                                 final ItemDefinitionUtils itemDefinitionUtils) {
        this.sessionManager = sessionManager;
        this.dmnGraphUtils = dmnGraphUtils;
        this.itemDefinitionUtils = itemDefinitionUtils;
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<DecisionTable> expression) {
        expression.ifPresent(dtable -> {
            dtable.setHitPolicy(HitPolicy.UNIQUE);
            dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

            final InputClause inputClause = new InputClause();
            final InputClauseLiteralExpression literalExpression = new InputClauseLiteralExpression();
            literalExpression.getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
            inputClause.setInputExpression(literalExpression);
            dtable.getInput().add(inputClause);

            final OutputClause outputClause = new OutputClause();
            outputClause.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
            final HasTypeRef hasTypeRef = TypeRefUtils.getTypeRefOfExpression(dtable, hasExpression);
            outputClause.setTypeRef(!Objects.isNull(hasTypeRef) ? hasTypeRef.getTypeRef() : BuiltInType.UNDEFINED.asQName());
            dtable.getOutput().add(outputClause);

            for (int rows = 0; rows < 500; rows++) {
                final DecisionRule decisionRule = new DecisionRule();
                final UnaryTests decisionRuleUnaryTest = new UnaryTests();
                decisionRuleUnaryTest.getText().setValue(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
                decisionRule.getInputEntry().add(decisionRuleUnaryTest);

                final LiteralExpression decisionRuleLiteralExpression = new LiteralExpression();
                decisionRuleLiteralExpression.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
                decisionRule.getOutputEntry().add(decisionRuleLiteralExpression);

                final Description description = new Description();
                description.setValue(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
                decisionRule.setDescription(description);

                dtable.getRule().add(decisionRule);

                decisionRule.setParent(dtable);
                decisionRuleUnaryTest.setParent(decisionRule);
                decisionRuleLiteralExpression.setParent(decisionRule);
            }

            //Setup parent relationships
            inputClause.setParent(dtable);
            outputClause.setParent(dtable);
            literalExpression.setParent(inputClause);

            if (nodeUUID.isPresent()) {
                enrichInputClauses(nodeUUID.get(), dtable);
            } else {
                enrichOutputClauses(dtable);
            }
        });
    }

    @SuppressWarnings("unchecked")
    void enrichInputClauses(final String uuid,
                            final DecisionTable dtable) {
        final Graph<?, Node> graph = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph();
        final Node<?, Edge> node = graph.getNode(uuid);
        if (Objects.isNull(node)) {
            return;
        }

        //Get all InputData nodes feeding into this DecisionTable
        final List<InputData> inputDataSet = node.getInEdges().stream()
                .map(Edge::getSourceNode)
                .map(Node::getContent)
                .filter(content -> content instanceof Definition)
                .map(content -> (Definition) content)
                .map(Definition::getDefinition)
                .filter(definition -> definition instanceof InputData)
                .map(definition -> (InputData) definition)
                .collect(Collectors.toList());
        if (inputDataSet.isEmpty()) {
            return;
        }

        //Extract individual components of InputData TypeRefs
        final Definitions definitions = dmnGraphUtils.getDefinitions();
        final List<InputClauseRequirement> inputClauseRequirements = new ArrayList<>();
        inputDataSet.forEach(inputData -> addInputClauseRequirement(inputData.getVariable().getTypeRef(),
                                                                    definitions,
                                                                    inputClauseRequirements,
                                                                    inputData.getName().getValue()));

        //Add InputClause columns for each InputData TypeRef component, sorted alphabetically
        dtable.getInput().clear();
        dtable.getRule().stream().forEach(decisionRule -> decisionRule.getInputEntry().clear());
        inputClauseRequirements
                .stream()
                .sorted(Comparator.comparing(inputClauseRequirement -> inputClauseRequirement.text))
                .forEach(inputClauseRequirement -> {
                    final InputClause inputClause = new InputClause();
                    final InputClauseLiteralExpression literalExpression = new InputClauseLiteralExpression();
                    literalExpression.getText().setValue(inputClauseRequirement.text);
                    literalExpression.setTypeRef(inputClauseRequirement.typeRef);
                    inputClause.setInputExpression(literalExpression);
                    dtable.getInput().add(inputClause);

                    dtable.getRule().stream().forEach(decisionRule -> {
                        final UnaryTests decisionRuleUnaryTest = new UnaryTests();
                        decisionRuleUnaryTest.getText().setValue(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
                        decisionRule.getInputEntry().add(decisionRuleUnaryTest);
                        decisionRuleUnaryTest.setParent(decisionRule);
                    });

                    inputClause.setParent(dtable);
                    literalExpression.setParent(inputClause);
                });
    }

    private void addInputClauseRequirement(final QName typeRef,
                                           final Definitions definitions,
                                           final List<InputClauseRequirement> inputClauseRequirements,
                                           final String text) {
        //TypeRef matches a BuiltInType
        for (BuiltInType bi : BuiltInType.values()) {
            for (String biName : bi.getNames()) {
                if (Objects.equals(biName, typeRef.getLocalPart())) {
                    inputClauseRequirements.add(new InputClauseRequirement(text, typeRef));
                    return;
                }
            }
        }

        //Otherwise lookup and expand ItemDefinition from the QName's LocalPart
        definitions.getItemDefinition()
                .stream()
                .filter(itemDef -> itemDef.getName().getValue().equals(typeRef.getLocalPart()))
                .findFirst()
                .ifPresent(itemDefinition -> addInputClauseRequirement(itemDefinition, inputClauseRequirements, text));
    }

    void addInputClauseRequirement(final ItemDefinition itemDefinition,
                                   final List<InputClauseRequirement> inputClauseRequirements,
                                   final String text) {
        if (itemDefinition.getItemComponent().size() == 0) {
            inputClauseRequirements.add(new InputClauseRequirement(text,
                                                                   getQName(itemDefinition)));
        } else {
            itemDefinition.getItemComponent()
                    .forEach(itemComponent -> addInputClauseRequirement(itemComponent,
                                                                        inputClauseRequirements,
                                                                        text + "." + itemComponent.getName().getValue()));
        }
    }

    private QName getQName(final ItemDefinition itemDefinition) {
        return Optional
                .ofNullable(itemDefinition.getTypeRef())
                .orElse(getQNameFromItemDefinitionName(itemDefinition));
    }

    private QName getQNameFromItemDefinitionName(final ItemDefinition itemDefinition) {

        final Name name = itemDefinition.getName();
        final QName typeRef = new QName(NULL_NS_URI, name.getValue());

        return itemDefinitionUtils.normaliseTypeRef(typeRef);
    }

    void enrichOutputClauses(final DecisionTable dtable) {
        if (dtable.getParent() instanceof ContextEntry) {
            final ContextEntry contextEntry = (ContextEntry) dtable.getParent();
            dtable.getOutput().clear();
            dtable.getRule().stream().forEach(decisionRule -> decisionRule.getOutputEntry().clear());

            final OutputClause outputClause = new OutputClause();
            outputClause.setName(getOutputClauseName(contextEntry).orElse(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable)));
            outputClause.setTypeRef(getOutputClauseTypeRef(contextEntry).orElse(BuiltInType.UNDEFINED.asQName()));
            dtable.getOutput().add(outputClause);

            dtable.getRule().stream().forEach(decisionRule -> {
                final LiteralExpression decisionRuleLiteralExpression = new LiteralExpression();
                decisionRuleLiteralExpression.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
                decisionRule.getOutputEntry().add(decisionRuleLiteralExpression);
                decisionRuleLiteralExpression.setParent(decisionRule);
            });

            outputClause.setParent(dtable);
        }
    }

    private Optional<String> getOutputClauseName(final HasVariable hasVariable) {
        final IsInformationItem variable = hasVariable.getVariable();
        if (variable instanceof InformationItem) {
            return Optional.ofNullable(((InformationItem) variable).getName().getValue());
        }

        final DMNModelInstrumentedBase base = hasVariable.asDMNModelInstrumentedBase().getParent();
        final DMNModelInstrumentedBase parent = base.getParent();
        if (parent instanceof HasName) {
            return Optional.ofNullable(((HasName) parent).getName().getValue());
        }
        if (parent instanceof HasVariable) {
            return getOutputClauseName((HasVariable) parent);
        }
        return Optional.empty();
    }

    private Optional<QName> getOutputClauseTypeRef(final HasVariable hasVariable) {
        final IsInformationItem variable = hasVariable.getVariable();
        if (Objects.nonNull(variable)) {
            return Optional.ofNullable(variable.getTypeRef());
        }

        final DMNModelInstrumentedBase base = hasVariable.asDMNModelInstrumentedBase().getParent();
        final DMNModelInstrumentedBase parent = base.getParent();
        if (parent instanceof HasTypeRef) {
            return Optional.ofNullable(((HasTypeRef) parent).getTypeRef());
        }
        if (parent instanceof HasVariable) {
            return getOutputClauseTypeRef((HasVariable) parent);
        }
        return Optional.empty();
    }
}
