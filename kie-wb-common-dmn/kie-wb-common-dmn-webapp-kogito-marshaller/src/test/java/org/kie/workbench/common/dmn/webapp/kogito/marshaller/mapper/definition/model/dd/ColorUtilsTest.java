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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd;

import org.junit.Test;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.BaseKogitoGwtTestCase;

public class ColorUtilsTest extends BaseKogitoGwtTestCase {

    @Test
    public void test_roundtrip() {
        JSIColor x = ColorUtils.dmnFromWB("#FFAA00");

        assertEquals(255, x.getRed());
        assertEquals(170, x.getGreen());
        assertEquals(0, x.getBlue());

        String x2 = ColorUtils.wbFromDMN(x);

        assertEquals("#FFAA00", x2);
    }
}
