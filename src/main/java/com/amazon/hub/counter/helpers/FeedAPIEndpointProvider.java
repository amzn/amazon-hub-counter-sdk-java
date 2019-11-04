package com.amazon.hub.counter.helpers;


import com.amazon.hub.counter.entities.FeedType;

/**
 * Utility class to handle the endpoint urls construction for the Amazon Hub
 * Counter API.
 */
public class FeedAPIEndpointProvider {

    private final String baseUrl;

    private static final String GET_FEEDS_ENDPOINT = "/v1/feeds";
    private static final String GET_FEED_ENDPOINT = "/v1/feeds/%s";
    private static final String GET_DOCUMENT_ENDPOINT = "/v1/feeds/%s"
            + "/documents/%s";
    private static final String POST_FEED_ENDPOINT = "/v1/feeds?feedType=%s";
    private static final String GET_FEEDS_PAGE_SIZE_PARAM_KEY = "pageSize";
    private static final String GET_FEEDS_NEXT_OFFSET_PARAM_KEY = "nextOffset";

    private FeedAPIEndpointProvider() {
        throw new IllegalStateException("Cannot instantiate utility class.");
    }

    public FeedAPIEndpointProvider(final String baseUrl) {

        if (baseUrl.isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null.");
        }

        this.baseUrl = baseUrl;
    }

    /**
     * @param nextOffset Pagination offset id.
     * @param pageSize   Size of the response results.
     * @return Returns the endpoint for the get feeds method.
     */
    public String getFeedsEndpoint(final String nextOffset,
                                   final Integer pageSize) {

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(baseUrl);
        urlBuilder.append(GET_FEEDS_ENDPOINT);

        if (pageSize != null) {
            urlBuilder.append("?");
            urlBuilder.append(GET_FEEDS_PAGE_SIZE_PARAM_KEY);
            urlBuilder.append("=");
            urlBuilder.append(pageSize);


            if (nextOffset != null) {

                urlBuilder.append("&");
                urlBuilder.append(GET_FEEDS_NEXT_OFFSET_PARAM_KEY);
                urlBuilder.append("=");
                urlBuilder.append(nextOffset);
            }
        } else {
            if (nextOffset != null) {
                urlBuilder.append("?");
                urlBuilder.append(GET_FEEDS_NEXT_OFFSET_PARAM_KEY);
                urlBuilder.append("=");
                urlBuilder.append(nextOffset);

            }
        }

        return urlBuilder.toString();
    }

    /**
     * @param feedId The requested feed id.
     * @return Returns the endpoint for the get feed by id method.
     */
    public String getFeedByIdEndpoint(final String feedId) {

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(baseUrl);
        urlBuilder.append("".format(GET_FEED_ENDPOINT, feedId));

        return urlBuilder.toString();
    }

    /**
     * @param feedId     The feed id of the requested document.
     * @param documentId The requested document id.
     * @return Returns the endpoint for the get document by id method.
     */
    public String getDocumentEndpoint(final String feedId,
                                      final String documentId) {

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(baseUrl);
        urlBuilder.append("".format(GET_DOCUMENT_ENDPOINT, feedId, documentId));

        return urlBuilder.toString();

    }

    /**
     * @param feedType The type of the feed to be submitted.
     * @return Returns the endpoint for the create/update feed method.
     */
    public String postFeedEndpoint(final FeedType feedType) {
        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(baseUrl);
        urlBuilder.append("".format(POST_FEED_ENDPOINT, feedType.toString()));

        return urlBuilder.toString();
    }
}
