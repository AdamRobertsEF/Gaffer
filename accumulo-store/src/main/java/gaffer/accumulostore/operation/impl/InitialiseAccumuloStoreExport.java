/*
 * Copyright 2016 Crown Copyright
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

package gaffer.accumulostore.operation.impl;

import gaffer.accumulostore.export.AccumuloStoreExporter;
import gaffer.operation.impl.export.initialise.InitialiseExport;

public class InitialiseAccumuloStoreExport extends InitialiseExport {
    public InitialiseAccumuloStoreExport() {
        super(new AccumuloStoreExporter());
    }

    @Override
    public AccumuloStoreExporter getExporter() {
        return ((AccumuloStoreExporter) super.getExporter());
    }

    public static class Builder extends InitialiseExport.Builder<InitialiseAccumuloStoreExport> {
        public Builder() {
            super(new InitialiseAccumuloStoreExport());
        }
    }
}
