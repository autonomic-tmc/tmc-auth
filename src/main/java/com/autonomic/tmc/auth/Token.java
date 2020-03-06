package com.autonomic.tmc.auth;

/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * tmc-auth
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2018 Autonomic, LLC - All rights reserved
 * ——————————————————————————————————————————————————————————————————————————————
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 * ______________________________________________________________________________
 */

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * This class is a data object representation of an OAuth 2.0 Access Token Response.
 *
 * <p>Reference: <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.4.3">
 * RFC 6749, Section 4.4.3</a></p>
 *
 * <ul>
 *     <li>
 *         <b><i>value:</i></b> The &quot;access_token&quot; part of the Access Token Response
 *     </li>
 *     <li>
 *         <b><i>expiration:</i></b> The point in time at which this token will expire.
 *         Derived from the &quot;expires_in&quot; field of the Access Token Response
 *     </li>
 * </ul>
 */
//@Getter
@EqualsAndHashCode
public class Token implements Supplier<String> {

    private Clock clock = java.time.Clock.systemDefaultZone();

    private String value;
    private Instant expiration;

    /**
     * Public Constructor.
     *
     * <p>A builder is also provided and encouraged as an option in lieu of using this constructor
     * directly.</p>
     *
     * <p>Builder Example: </p>
     * <pre>{@code
     *    Token token = Token.builder()
     *             .value("a-token-value")
     *             .expiration(expirationInstant)
     *             .build();
     *  }</pre>
     *
     * @param value String representation of Bearer token
     * @param expiration Instant at which this value expires
     */
    @Builder
    public Token(@NonNull String value, @NonNull Instant expiration) {
        this.value = value;
        this.expiration = expiration;
    }

    /**
     * Check if this token has expired and therefore no longer valid.
     *
     * @return true if this token is expired
     */
    boolean isExpired() {
        // Treat tokens set to expire within 10 seconds as expired.
        // The aim of this is to eliminate number of tokens that expire while in-flight.
        return clock.instant().plus(10, ChronoUnit.SECONDS)
            .isAfter(expiration);
    }

    @Override
    public String get() {
        return this.value;
    }
}
