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

import com.amazon.hub.counter.entities.*;
import com.amazon.hub.counter.helpers.FeedAPIEndpointProvider;
import com.amazon.hub.counter.helpers.HttpUtils;
import com.amazon.hub.counter.login.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

/**
 * Solves the interaction with Amazon Counter API for submitting, updating and
 * querying feeds and their associate documents.
 */
public class AmazonHubCounterFeedAPI {

    private static final Logger logger = LogManager.getLogger(
            AmazonHubCounterFeedAPI.class.getName());

    private static final String AUTH_TOKEN_PREFIX = "Bearer ";

    private final ClientCredentials clientCredentials;
    private final String authEndpoint;
    private final String apiEndpoint;
    private final FeedAPIEndpointProvider apiEndpointProvider;

    /**
     * @param clientCredentials Your client credentials.
     * @param apiEndpoint       URL for the API endpoint to be used.
     * @param authEndpoint      URL for the authentication endpoint to be used.
     */
    public AmazonHubCounterFeedAPI(final ClientCredentials clientCredentials,
                                   final String apiEndpoint,
                                   final String authEndpoint) {

        this.clientCredentials = clientCredentials;
        this.apiEndpoint = apiEndpoint;
        this.authEndpoint = authEndpoint;
        this.apiEndpointProvider = new FeedAPIEndpointProvider(
                this.apiEndpoint);
    }

    /**
     * @return A valid accessToken that can be used when reaching the
     * Amazon Counter Hub API resources.
     */
    public String getAccessToken() {

        LoginRequest loginRequest = LoginRequest.builder()
                .client_id(this.clientCredentials.getClientId())
                .client_secret(this.clientCredentials.getClientSecret())
                .grant_type(this.clientCredentials.getAuthGrantType())
                .scope(this.clientCredentials.getAuthScope())
                .build();

        logger.debug("Login in...");

        String json = loginRequest.toJson();

        try {
            String response = HttpUtils.postJson(this.authEndpoint, json, null);
            Gson gson = new Gson();
            Map obj = gson.fromJson(response, Map.class);

            String token = obj.get("access_token").toString();

            return token;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * @param nextOffset  The pagination page id.
     * @param pageSize    Defines the size of the pagination of the results,
     *                    default: 50, max_value: 100.
     * @param accessToken The Bearer token that authenticates the user.
     * @return A list of feeds that this client has submitted, the results are
     * paginated.
     */
    public FeedsResponse getFeeds(final String nextOffset,
                                  final Integer pageSize,
                                  final String accessToken) {

        String url = this.apiEndpointProvider.getFeedsEndpoint(nextOffset,
                pageSize);


        String bearerToken = getBearerToken(accessToken);

        logger.debug("Getting client Feeds...");

        try {
            String response = HttpUtils.getJson(url, bearerToken);

            // Map JSON response to POJO
            Gson gson = new Gson();
            FeedsResponse feedsResponse = gson.fromJson(response,
                    FeedsResponse.class);

            return feedsResponse;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private String getBearerToken(final String accessToken) {
        return AUTH_TOKEN_PREFIX + accessToken;
    }

    /**
     * @param feedId      The feedId of the requested feed.
     * @param accessToken The Bearer token that authenticates the user.
     * @return A Feed that matches that feedId or null if it doesn't exists.
     */
    public Feed getFeedById(final String feedId, final String accessToken) {

        String url = this.apiEndpointProvider.getFeedByIdEndpoint(feedId);
        String bearerToken = getBearerToken(accessToken);

        logger.debug("Getting client Feed by id, FeedID: [{}]", feedId);

        try {
            String response = HttpUtils.getJson(url, bearerToken);

            // Map JSON response to POJO
            Gson gson = new Gson();
            Feed feedResponse = gson.fromJson(response, Feed.class);

            return feedResponse;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * @param feedId      The feedId of the requested document.
     * @param documentId  The documentId of the requested document.
     * @param accessToken The Bearer token that authenticates the user.
     * @return An OutputDocument that matches that feedId and documentId or
     * null if it doesn't exists.
     */
    public OutputDocument getOutputDocument(final String feedId,
                                       final String documentId,
                                      final String accessToken) {

        String url = this.apiEndpointProvider.getDocumentEndpoint(feedId,
                documentId);
        String bearerToken = getBearerToken(accessToken);

        logger.debug("Getting feed Output Document by id, FeedID: [{}] , "
                + "DocumentID: [{}]", feedId, documentId);

        try {
            String response = HttpUtils.getJson(url, bearerToken);

            // Map JSON response to POJO
            Gson gson = new Gson();
            OutputDocument document = gson.fromJson(response,
                    OutputDocument.class);

            return document;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param feedId      The feedId of the requested document.
     * @param documentId  The documentId of the requested document.
     * @param accessToken The Bearer token that authenticates the user.
     * @return An AccessPointsFeedRequest representing the feed store request.
     */
    public AccessPointsFeedRequest getInputDocument(final String feedId,
                                            final String documentId,
                                            final String accessToken) {

        String url = this.apiEndpointProvider.getDocumentEndpoint(feedId,
                documentId);
        String bearerToken = getBearerToken(accessToken);

        logger.debug("Getting feed Input Document by id, FeedID: [{}] , "
                + "DocumentID: [{}]", feedId, documentId);

        try {
            String response = HttpUtils.getJson(url, bearerToken);

            // Map JSON response to POJO
            Gson gson = new Gson();
            AccessPointsFeedRequest request = gson.fromJson(response,
                    AccessPointsFeedRequest.class);

            return request;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param accessPointsFeedPost The object containing the list of feeds to be
     *                             submitted.
     * @param feedType             The type of feed to submit (STORE_FEED|THIRD_PARTY_FEED)
     * @param accessToken          The Bearer token that authenticates the user.
     * @return The feedId of the created feed.
     */
    public String postFeed(final AccessPointsFeedRequest accessPointsFeedPost,
                           final FeedType feedType, final String accessToken) {

        String url = this.apiEndpointProvider.postFeedEndpoint(feedType);
        String json = accessPointsFeedPost.toJson();

        String bearerToken = getBearerToken(accessToken);

        logger.debug("Creating Feed.");

        try {

            String response = HttpUtils.postJson(url, json, bearerToken);
            Gson gson = new Gson();
            Map obj = gson.fromJson(response, Map.class);
            String feedId = obj.get("feedId").toString();

            logger.info("Created Feed with FeedID: [{}]", feedId);

            return feedId;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * @return Returns the Amazon Hub Counter API endpoint.
     */
    public String getApiEndpoint() {

        return apiEndpoint;
    }


}
