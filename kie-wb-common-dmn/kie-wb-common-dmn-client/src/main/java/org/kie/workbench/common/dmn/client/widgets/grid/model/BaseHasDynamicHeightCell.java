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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.Objects;

import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

public abstract class BaseHasDynamicHeightCell<T> extends DMNGridCell<T> implements HasDynamicHeight {

    protected final double lineHeight;

    private double height;

    public BaseHasDynamicHeightCell(final GridCellValue<T> value,
                                    final double lineHeight) {
        super(value);
        this.lineHeight = lineHeight;
        this.height = getExpressionTextHeight();
    }

    @Override
    protected void setValue(final GridCellValue<T> value) {
        super.setValue(value);
        this.height = getExpressionTextHeight();
    }

    @Override
    public double getHeight() {
        return height;
    }

    protected double getExpressionTextHeight() {
        if (Objects.isNull(value) || Objects.isNull(value.getValue())) {
            return DEFAULT_HEIGHT;
        }
        final int expressionLineCount = getExpressionLineCount();
        return expressionLineCount * lineHeight + (RendererUtils.EXPRESSION_TEXT_PADDING * 3);
    }

    protected int getExpressionLineCount() {
        final String asText = getValue().getValue().toString();
        return asText.split("\\r?\\n", -1).length;
    }
}
