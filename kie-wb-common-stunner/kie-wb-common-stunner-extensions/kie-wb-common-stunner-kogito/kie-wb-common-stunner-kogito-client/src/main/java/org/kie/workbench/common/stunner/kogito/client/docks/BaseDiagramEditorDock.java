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
package org.kie.workbench.common.stunner.kogito.client.docks;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public abstract class BaseDiagramEditorDock {

    protected static final double DOCK_SIZE = 400d;

    protected final UberfireDocks uberfireDocks;
    protected UberfireDock uberfireDock;
    protected boolean isOpened = false;
    protected String owningPerspectiveId;
    protected final TranslationService translationService;

    public BaseDiagramEditorDock(UberfireDocks uberfireDocks, TranslationService translationService) {
        this.uberfireDocks = uberfireDocks;
        this.translationService = translationService;
    }

    public void init(final String owningPerspectiveId) {
        this.owningPerspectiveId = owningPerspectiveId;
        this.uberfireDock = makeUberfireDock();
    }

    public void open() {
        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.add(getUberfireDock());
        uberfireDocks.show(position(), owningPerspectiveId());
    }

    public void close() {
        if (!isOpened()) {
            return;
        }

        isOpened = false;
        uberfireDocks.close(getUberfireDock());
        uberfireDocks.remove(getUberfireDock());
    }

    public boolean isOpened() {
        return isOpened;
    }

    protected UberfireDockPosition position() {
        return UberfireDockPosition.EAST;
    }

    protected String owningPerspectiveId() {
        return owningPerspectiveId;
    }

    protected UberfireDock getUberfireDock() {
        return uberfireDock;
    }

    protected UberfireDock makeUberfireDock() {
        final UberfireDock uberfireDock = new UberfireDock(position(), icon(), placeRequest(), owningPerspectiveId());
        return uberfireDock.withSize(DOCK_SIZE).withLabel(dockLabel());
    }

    protected DefaultPlaceRequest placeRequest() {
        return new DefaultPlaceRequest(getScreenId());
    }

    protected abstract String getScreenId();

    protected String dockLabel() {
        return translationService.getTranslation(getLabelKey());
    }

    protected abstract String getLabelKey();

    protected abstract String icon();
}
