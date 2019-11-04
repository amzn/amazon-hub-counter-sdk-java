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

package com.amazon.hub.counter;

import com.amazon.hub.counter.helpers.HttpUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpUtilsTest {

    private static final Integer PORT = 8090;
    private static final String MOCK_API_ENDPOINT = "http://localhost:" + PORT;
    private static final String MOCK_AUTH_ENDPOINT = "http://localhost:" + PORT + "/auth/o2/token";
    WireMockServer wireMockServer;

    @BeforeEach
    public void startWireMock() {
        // Initialize WireMock
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Error 403 (Forbidden) in HttpUtils.getJson({url}, {bearerToken})")
    public void httpUtilsGetJson403ErrorTest() {

        String url = MOCK_API_ENDPOINT + "/forbidden";

        // Create WireMock stub
        stubFor(
                WireMock.get(urlEqualTo("/forbidden"))
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("content-type", "application/json")));

        assertThrows(IOException.class, () -> {
            HttpUtils.getJson(url, null);
        });

    }

    @Test
    @DisplayName("Error 403 (Forbidden) in HttpUtils.postJson({url}, {json}, {bearerToken})")
    public void httpUtilsPostJson403ErrorTest() {

        String url = MOCK_API_ENDPOINT + "/forbidden";

        // Create WireMock stub
        stubFor(
                WireMock.post(urlEqualTo(url))
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("content-type", "application/json")));

        assertThrows(IOException.class, () -> {
            HttpUtils.postJson(url, "{}", null);
        });

    }

    @Test
    @DisplayName("Error reading response in HttpUtils.getJson({url}, {bearerToken})")
    public void httpUtilsGetJsonFaultTest() {

        String url = MOCK_API_ENDPOINT + "/fault";

        stubFor(WireMock.get(urlEqualTo("/fault"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        assertThrows(IOException.class, () -> {
            HttpUtils.getJson(url, "null");
        });

    }

    @Test
    @DisplayName("Error writing request in HttpUtils.postJson({url}, {json}, " +
            "{bearerToken})")
    public void httpUtilsPostJsonFaultTest() {

        String url = MOCK_API_ENDPOINT + "/fault";

        stubFor(WireMock.post(urlEqualTo("/fault"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        assertThrows(IOException.class, () -> {
            HttpUtils.postJson(url, "{}", "null");
        });

    }
}
