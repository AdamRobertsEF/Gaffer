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

package koryphe.function.stateless;

import koryphe.function.Function;

/**
 * A <code>StatelessFunction</code> is a {@link Function} that returns an output
 * in response to each input.
 * @param <I> Function input type
 * @param <O> Function output type
 */
public interface StatelessFunction<I, O> extends Function<I, O> {
    /**
     * Execute this <code>StatelessFunction</code>.
     * @param input Input value
     * @return Output value
     */
    O execute(I input);
}