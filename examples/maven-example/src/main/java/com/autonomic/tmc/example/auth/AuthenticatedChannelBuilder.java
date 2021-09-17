/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * TMC Auth SDK
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2021 Autonomic, LLC
 * ——————————————————————————————————————————————————————————————————————————————
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * ______________________________________________________________________________
 */
package com.autonomic.tmc.example.auth;

import static java.lang.String.format;

import com.autonomic.tmc.auth.TokenSupplier;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.net.MalformedURLException;
import java.net.URL;

class AuthenticatedChannelBuilder {
    private TokenSupplier tokenSupplier;

    AuthenticatedChannelBuilder(TokenSupplier tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    Channel buildWithUrl(String url) throws MalformedURLException {
        return ClientInterceptors
            .intercept(getChannel(url), new AuthHeaderInterceptor(tokenSupplier));
    }

    private ManagedChannel getChannel(String feedUrl) throws MalformedURLException {
        URL url = new URL(feedUrl);
        ManagedChannelBuilder builder = ManagedChannelBuilder
            .forAddress(url.getHost(), url.getPort());
        if ("http".equals(url.getProtocol())) {
            builder.usePlaintext();
        }
        return builder.build();
    }

    static final class AuthHeaderInterceptor implements ClientInterceptor {

        private static final Metadata.Key<String> AUTH_HEADER_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

        private TokenSupplier tokenSupplier;

        private AuthHeaderInterceptor(TokenSupplier tokenSupplier) {
            this.tokenSupplier = tokenSupplier;
        }

        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions, Channel next) {

            return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

                @Override
                public void start(Listener<RespT> responseListener, Metadata headers) {
                    headers.put(AUTH_HEADER_KEY, "Bearer " + tokenSupplier.get());
                    super.start(responseListener, headers);
                }
            };
        }
    }
}
