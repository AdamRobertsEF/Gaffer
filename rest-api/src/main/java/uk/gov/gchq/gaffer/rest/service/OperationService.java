/*
 * Copyright 2016-2017 Crown Copyright
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

package uk.gov.gchq.gaffer.rest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.rest.factory.GraphFactory;
import uk.gov.gchq.gaffer.rest.factory.UserFactory;
import uk.gov.gchq.gaffer.user.User;
import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;

import static uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser.createDefaultMapper;

/**
 * An implementation of {@link uk.gov.gchq.gaffer.rest.service.IOperationService}. By default it will use a singleton
 * {@link uk.gov.gchq.gaffer.graph.Graph} generated using the {@link uk.gov.gchq.gaffer.rest.factory.GraphFactory}.
 * All operations are simple delegated to the graph.
 * Pre and post operation hooks are available by extending this class and implementing preOperationHook and/or
 * postOperationHook.
 * <p>
 * By default queries will be executed with an UNKNOWN user containing no auths.
 * The createUser() method should be overridden and a {@link User} object should
 * be created from the http request.
 * </p>
 */
public class OperationService implements IOperationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationService.class);
    public final ObjectMapper mapper = createDefaultMapper();

    @Inject
    private GraphFactory graphFactory;

    @Inject
    private UserFactory userFactory;

    @Override
    public Object execute(final OperationChain opChain) {
        return _execute(opChain);
    }

    @SuppressFBWarnings
    @Override
    public ChunkedOutput<String> executeChunked(final OperationChain opChain) {
        // Create chunked output instance
        final ChunkedOutput<String> output = new ChunkedOutput<>(String.class, "\r\n");

        // write chunks to the chunked output object
        new Thread() {
            public void run() {
                try {
                    final Object result = _execute(opChain);
                    chunkResult(result, output);
                } finally {
                    IOUtils.closeQuietly(output);
                }
            }
        }.start();

        return output;
    }

    @Override
    public CloseableIterable<Object> generateObjects(final GenerateObjects<Object> operation) {
        return _execute(operation);
    }

    @Override
    public CloseableIterable<Element> generateElements(final GenerateElements<Object> operation) {
        return _execute(operation);
    }

    @Override
    public CloseableIterable<EntityId> getAdjacentIds(final GetAdjacentIds operation) {
        return _execute(operation);
    }

    @Override
    public CloseableIterable<Element> getAllElements(final GetAllElements operation) {
        return _execute(operation);
    }

    @Override
    public CloseableIterable<Element> getElements(final GetElements operation) {
        return _execute(operation);
    }

    @Override
    public void addElements(final AddElements operation) {
        _execute(operation);
    }

    protected void preOperationHook(final OperationChain<?> opChain, final User user) {
        // no action by default
    }

    protected void postOperationHook(final OperationChain<?> opChain, final User user) {
        // no action by default
    }

    protected <O> O _execute(final Operation operation) {
        return _execute(new OperationChain<>(operation));
    }

    protected <O> O _execute(final OperationChain<O> opChain) {
        final User user = userFactory.createUser();
        preOperationHook(opChain, user);

        try {
            return graphFactory.getGraph().execute(opChain, user);
        } catch (OperationException e) {
            throw new RuntimeException("Error executing opChain", e);
        } finally {
            postOperationHook(opChain, user);
        }
    }

    protected void chunkResult(final Object result, final ChunkedOutput<String> output) {
        if (result instanceof Iterable) {
            final Iterable itr = (Iterable) result;
            try {
                for (final Object item : itr) {
                    output.write(mapper.writeValueAsString(item));
                }
            } catch (final IOException ioe) {
                LOGGER.warn("IOException (chunks)", ioe);
            } finally {
                if (itr instanceof Closeable) {
                    IOUtils.closeQuietly(((Closeable) itr));
                }
            }
        } else {
            try {
                output.write(mapper.writeValueAsString(result));
            } catch (final IOException ioe) {
                LOGGER.warn("IOException (chunks)", ioe);
            }
        }
    }
}
