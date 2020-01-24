/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.selenium;

import java.util.List;

/**
 * Composite AssertionsError to report individual test failures.
 */
public class MultipleAssertionsError extends AssertionError {

    /**
     * Constructs an AssertionError with no detail message.
     */
    public MultipleAssertionsError() {
    }

    /**
     * Constructs an AssertionError containing an aggregation of the individual detail messages.
     * @param errors List of individual detail messages.
     */
    public MultipleAssertionsError(final List<String> errors) {
        super(createMessage(errors));
    }

    private static String createMessage(final List<String> errors) {
        final StringBuilder msg = new StringBuilder("\nThe following assertion(s) failed:\n");
        int i = 1;
        for (String error : errors) {
            msg.append(i++).append(") ").append(error).append("\n");
        }
        return msg.toString();
    }
}