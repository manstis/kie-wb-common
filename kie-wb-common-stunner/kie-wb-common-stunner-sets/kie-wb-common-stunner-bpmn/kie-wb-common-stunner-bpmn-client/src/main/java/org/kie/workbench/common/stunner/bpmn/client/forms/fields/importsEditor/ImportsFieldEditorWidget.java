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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.ImportsEditor;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

@Dependent
@Templated
public class ImportsFieldEditorWidget extends Composite implements HasValue<ImportsValue> {

    @Inject
    protected ImportsEditor importsEditor;
    @Inject
    @DataField
    private Button importsButton;
    @Inject
    @DataField
    private TextBox importsTextBox;
    private ImportsValue importsValue;

    public ImportsFieldEditorWidget() {
    }

    public ImportsFieldEditorWidget(final ImportsValue importsValue) {
        this.importsValue = importsValue;
    }

    @Override
    public ImportsValue getValue() {
        return importsValue;
    }

    @Override
    public void setValue(final ImportsValue value) {
        setValue(value, false);
    }

    @Override
    public void setValue(final ImportsValue value,
                         final boolean fireEvents) {
        ImportsValue oldValue = copyImportsValue(importsValue);
        importsValue = value;
        setImportsCount(value);
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            importsValue);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<ImportsValue> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    private void setImportsCount(ImportsValue importsValue) {
        int defaultImportsCount = 0;
        int wsdlImportsCount = 0;

        if (importsValue != null) {
            defaultImportsCount = importsValue.getDefaultImports().size();
            wsdlImportsCount = importsValue.getWSDLImports().size();
        }

        String importsString = buildImportsCountString(defaultImportsCount, wsdlImportsCount);
        importsTextBox.setText(importsString);
    }

    private String buildImportsCountString(final int defaultImportsCount, final int wsdlImportsCount) {
        if (defaultImportsCount == 0 && wsdlImportsCount == 0) {
            return StunnerFormsClientFieldsConstants.INSTANCE.No_Imports();
        } else {
            String defaultImportsCountString = buildDefaultImportsCountString(defaultImportsCount);
            String wsdlImportsCountString = buildWSDLImportsCountString(wsdlImportsCount);
            return defaultImportsCountString + ", " + wsdlImportsCountString;
        }
    }

    private String buildDefaultImportsCountString(final int defaultImportsCount) {
        if (defaultImportsCount == 0) {
            return StunnerFormsClientFieldsConstants.INSTANCE.No_Data_Type_Import();
        } else if (defaultImportsCount == 1) {
            return StunnerFormsClientFieldsConstants.INSTANCE.Data_Type_Import();
        } else {
            return defaultImportsCount + " " + StunnerFormsClientFieldsConstants.INSTANCE.Data_Type_Imports();
        }
    }

    private String buildWSDLImportsCountString(final int wsdlImportsCount) {
        if (wsdlImportsCount == 0) {
            return StunnerFormsClientFieldsConstants.INSTANCE.No_WSDL_Import();
        } else if (wsdlImportsCount == 1) {
            return StunnerFormsClientFieldsConstants.INSTANCE.WSDL_Import();
        } else {
            return wsdlImportsCount + " " + StunnerFormsClientFieldsConstants.INSTANCE.WSDL_Imports();
        }
    }

    private void showImportsEditor() {
        importsEditor.setImportsValue(copyImportsValue(importsValue));

        ImportsEditor.GetDataCallback callback = value -> {
            setValue(value, true);
        };
        importsEditor.setCallback(callback);

        importsEditor.show();
    }

    public ImportsValue copyImportsValue(ImportsValue importsValue) {
        ImportsValue copy = new ImportsValue();

        if (importsValue != null) {
            for (DefaultImport defaultImport : importsValue.getDefaultImports()) {
                DefaultImport importCopy = new DefaultImport();
                importCopy.setClassName(defaultImport.getClassName());
                copy.addImport(importCopy);
            }
            for (WSDLImport wsdlImport : importsValue.getWSDLImports()) {
                WSDLImport importCopy = new WSDLImport();
                importCopy.setLocation(wsdlImport.getLocation());
                importCopy.setNamespace(wsdlImport.getNamespace());
                copy.addImport(importCopy);
            }
        }

        return copy;
    }

    @EventHandler("importsButton")
    public void onClickImportsButton(final ClickEvent clickEvent) {
        showImportsEditor();
    }

    @EventHandler("importsTextBox")
    public void onClickImportsTextBox(final ClickEvent clickEvent) {
        showImportsEditor();
    }
}