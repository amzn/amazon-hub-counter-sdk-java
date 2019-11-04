/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.hub.counter.login;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents the payload of the request for obtaining the accessToken.
 */
@Getter
@Builder
public class LoginRequest {
    @NonNull
    private final String client_id;
    @NonNull
    private final String client_secret;
    @NonNull
    private final String grant_type;
    @NonNull
    private final String scope;

    /**
     * @return Returns the JSON representation of the Login request.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
