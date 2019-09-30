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

import java.util.logging.Logger;

import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class DefinitionUtilsMock extends DefinitionUtils {

    private static final Logger LOG = Logger.getLogger(DefinitionUtilsMock.class.getName());

    @Override
    public Bounds buildBounds(final Object definition,
                              final double x,
                              final double y) {
        LOG.info("Building bounds....");
        LOG.info("Building bounds for [" + definition.getClass().getSimpleName() + "]....");

        if (definition instanceof DMNViewDefinition) {
            LOG.info("Definition [" + definition.getClass().getSimpleName() + "] is an instance of DMNViewDefinition.");
            final DMNViewDefinition view = (DMNViewDefinition) definition;
            final Double width = view.getDimensionsSet().getWidth().getValue();
            final Double height = view.getDimensionsSet().getHeight().getValue();
            final double _width = null != width ? width : 0d;
            final double _height = null != height ? height : 0d;
            return Bounds.create(x, y, x + _width, y + _height);
        }

        LOG.info("Definition [" + definition.getClass().getSimpleName() + "] has no explicit bounds. Using (0, 0).");
        return Bounds.create(x, y, x, y);
    }
}
