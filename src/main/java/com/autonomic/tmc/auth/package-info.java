/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * tmc-auth
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2016 - 2024 Autonomic, LLC
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
/**
 * Using the <code>tmc-auth</code> library with your Autonomic provided client credentials, you can obtain an access token
 * that can be used when calling any of the Transportation Mobility Cloud (TMC) services. With this library,
 * you don't need to worry about the TMC's expiring tokens. The token you <code>get()</code> is always valid. If a token
 * expires, this library will automatically get a new one.
 *
 * <p>Using this library is easy:</p>
 * <pre>
 *     TokenSupplier supplier = ClientCredentialsTokenSupplier.builder()
 *         .clientId("{your-client-id}")
 *         .clientSecret("{your-client-secret}")
 *         .tokenUrl("{your-token-url-if-not-using-default}")
 *         .build());
 *
 *     String token = supplier.get()
 * </pre>
 *
 * Calling <code>supplier.get()</code> before passing the client credential token ensures that you will always have a valid token.
 */
package com.autonomic.tmc.auth;
