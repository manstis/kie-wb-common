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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBinding;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

public class BindingPropertyConverter {

    public static Binding wbFromDMN(final JSITBinding dmn,
                                    final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn == null) {
            return null;
        }
        final InformationItem convertedParameter = InformationItemPropertyConverter.wbFromDMN(dmn.getParameter());
        final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn.getExpression()));
        final Expression convertedExpression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                                                     Js.uncheckedCast(dmn),
                                                                                     hasComponentWidthsConsumer);

        final Binding result = new Binding();
        if (convertedParameter != null) {
            convertedParameter.setParent(result);
        }
        result.setParameter(convertedParameter);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }
        result.setExpression(convertedExpression);
        return result;
    }

    public static JSITBinding dmnFromWB(final Binding wb,
                                        final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        if (wb == null) {
            return null;
        }
        final JSITBinding result = new JSITBinding();
        final JSITInformationItem convertedParameter = InformationItemPropertyConverter.dmnFromWB(wb.getParameter());
        final JSITExpression convertedExpression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                                         componentWidthsConsumer);
        result.setParameter(convertedParameter);
        result.setExpression(convertedExpression);

        return result;
    }
}
