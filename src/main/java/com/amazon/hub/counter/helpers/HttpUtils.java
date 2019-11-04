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

package com.amazon.hub.counter.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Solves the HTTP requests for reaching the API.
 */
public final class HttpUtils {

    private static final Logger logger = LogManager.getLogger(
            HttpUtils.class.getName());

    private static final String JSON_CONTENT_TYPE = "application/json";

    private HttpUtils() {
        throw new IllegalStateException("Cannot instantiate utility class.");
    }

    /**
     * @param url         The target URL to be sent the POST Request.
     * @param json        The JSON that will be sent in the POST Request Body.
     * @param bearerToken The accessToken that will be added as an Authorization
     *                    HTTP header.
     * @return The raw server response.
     * @throws IOException If the HTTP POST fails to the destination URL.
     */
    public static String postJson(final String url, final String json,
                                  final String bearerToken) throws IOException {

        logger.debug("Sending HTTP POST Request to: [{}]", url);

        // Create the HTTP POST Request
        URL uri = new URL(url);
        final HttpURLConnection httpURLConnection =
                (HttpURLConnection) uri.openConnection();

        httpURLConnection.setRequestMethod(HttpMethod.POST);
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE,
                JSON_CONTENT_TYPE);
        httpURLConnection.setRequestProperty(HttpHeaders.CONTENT_LENGTH,
                Integer.toString(json.length()));
        if (bearerToken != null) {
            httpURLConnection.setRequestProperty(HttpHeaders.AUTHORIZATION,
                    bearerToken);
        }

        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT,
                JSON_CONTENT_TYPE);

        // Write request body
        writeRequestBody(json, httpURLConnection);
        String responseBody = getHttpResponseBody(httpURLConnection);

        // Check HTTP Response status code
        Integer responseCode = httpURLConnection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_CREATED
                && responseCode != HttpURLConnection.HTTP_OK) {
            logger.error("HTTP POST: Bad HTTP Status: [{}]", responseCode);
        }

        // Read the HTTP Response
        return responseBody;

    }

    private static void writeRequestBody(
            final String json, final HttpURLConnection httpURLConnection)
            throws IOException {

        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        byte[] input = json.getBytes(StandardCharsets.UTF_8);
        outputStream.write(input, 0, input.length);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * @param url         The target URL to be sent the GET Request.
     * @param bearerToken The accessToken that will be added as an Authorization
     *                    HTTP header.
     * @return The raw server response.
     * @throws IOException If the HTTP GET fails to the destination URL.
     */
    public static String getJson(final String url, final String bearerToken)
            throws IOException {

        logger.debug("Sending HTTP GET Request to: [{}]", url);

        // Create the HTTP GET Request
        URL uri = new URL(url);
        final HttpURLConnection httpURLConnection =
                (HttpURLConnection) uri.openConnection();

        httpURLConnection.setRequestMethod(HttpMethod.GET);

        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT,
                JSON_CONTENT_TYPE);
        httpURLConnection.setRequestProperty(HttpHeaders.AUTHORIZATION,
                bearerToken);

        // Check HTTP Response status code
        int responseCode = httpURLConnection.getResponseCode();

        // Read the HTTP Response
        String responseBody = getHttpResponseBody(httpURLConnection);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            logger.error("HTTP GET: Bad HTTP Status: [{}]", responseCode);
        }

        return responseBody;
    }

    /**
     * @param httpURLConnection An active HTTP connection.
     * @return The raw server response.
     * @throws IOException IOException If an error occurs reading the response
     *                     stream.
     */
    private static String getHttpResponseBody(
            final HttpURLConnection httpURLConnection) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        httpURLConnection.getInputStream(),
                        StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String responseLine;

        while ((responseLine = bufferedReader.readLine()) != null) {
            response.append(responseLine.trim());
        }

        bufferedReader.close();

        return response.toString();
    }
}
