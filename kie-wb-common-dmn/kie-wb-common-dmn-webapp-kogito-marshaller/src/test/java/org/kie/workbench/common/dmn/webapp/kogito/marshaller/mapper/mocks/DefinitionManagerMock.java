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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;

public class DefinitionManagerMock implements DefinitionManager {

    private static final Logger LOG = Logger.getLogger(DefinitionManagerMock.class.getName());

    @Override
    public TypeDefinitionSetRegistry<?> definitionSets() {
        return null;
    }

    @Override
    public AdapterManager adapters() {
        return null;
    }

    @Override
    public CloneManager cloneManager() {
        return null;
    }
}
