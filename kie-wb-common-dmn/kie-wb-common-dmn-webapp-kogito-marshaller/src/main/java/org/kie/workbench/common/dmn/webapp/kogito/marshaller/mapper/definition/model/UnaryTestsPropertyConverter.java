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

import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.IsUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;

public class UnaryTestsPropertyConverter {

    public static UnaryTests wbFromDMN(final JSITUnaryTests dmn) {
        if (dmn == null) {
            return null;
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final ExpressionLanguage expressionLanguage = ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage());
        final ConstraintType constraintTypeField;
        final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                    ConstraintType.CONSTRAINT_KEY,
                                    DMNModelInstrumentedBase.Namespace.KIE.getPrefix());

        final Map<QName, String> otherAttributes = JsUtils.toAttributesMap(dmn);
        if (otherAttributes.containsKey(key)) {
            constraintTypeField = ConstraintTypeFieldPropertyConverter.wbFromDMN(otherAttributes.get(key));
        } else {
            constraintTypeField = NONE;
        }
        final UnaryTests result = new UnaryTests(id,
                                                 description,
                                                 new Text(dmn.getText()),
                                                 expressionLanguage,
                                                 constraintTypeField);
        return result;
    }

    public static JSITUnaryTests dmnFromWB(final IsUnaryTests wb) {
        if (wb == null) {
            return null;
        }
        final JSITUnaryTests result = new JSITUnaryTests();
        result.setId(wb.getId().getValue());
        result.setText(wb.getText().getValue());

        final ConstraintType constraint = wb.getConstraintType();

        if (isNotNone(constraint)) {
            final QName key = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                        ConstraintType.CONSTRAINT_KEY,
                                        DMNModelInstrumentedBase.Namespace.KIE.getPrefix());
            result.getOtherAttributes().put(key, constraint.value());
        }

        return result;
    }

    private static boolean isNotNone(final ConstraintType constraint) {
        return !Objects.equals(constraint, NONE);
    }
}
