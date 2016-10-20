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

package koryphe.function.stateless.validator;

import koryphe.function.stateless.StatelessFunction;

/**
 * A <code>Validator</code> {@link StatelessFunction} tests an input value
 * and outputs a <code>boolean</code> result.
 * @param <I> Function input type
 */
public interface Validator<I> extends StatelessFunction<I, Boolean> { }