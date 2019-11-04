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

package com.amazon.hub.counter.entities;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Builder;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents the payload of the request that is sent when submitting a feed.
 */
@Getter
@Builder
public class AccessPointsFeedRequest {
    @NonNull
    private final AccessPoint[] accessPoints;

    /**
     * Override lombok default constructor to add validations.
     */
    public static class AccessPointsFeedRequestBuilder {

        /**
         * @param accessPoints List of access points to be validated.
         * @return Returns a valid list of access points.
         * @throws IllegalArgumentException If there is an invalid access point.
         */
        public AccessPointsFeedRequestBuilder accessPoints (
                final AccessPoint[] accessPoints) {

            this.accessPoints = accessPoints;

            validateAccessPoints();

            return this;
        }

        private void validateAccessPoints() {

            List<Map.Entry<String, Long>> repeatedAccessPoints =
                    Arrays.stream(accessPoints)
                            .collect(Collectors.groupingBy(ap ->
                                    ap.getAccessPointId(), Collectors.counting()
                                    )
                            )
                            .entrySet()
                            .stream()
                            .filter(frequency -> frequency.getValue() > 1)
                            .collect(Collectors.toList());

            if (repeatedAccessPoints.size() > 0) {
                throw new IllegalArgumentException(String.format("The "
                                + "accessPointsId cannot  be the same in "
                                + "different Access Points. The access point "
                                + "with id: %s is repeated.",
                                repeatedAccessPoints.get(0).getKey()
                        )
                    );
            }
        }
    }

    /**
     * @param accessPointId The id of the Access Point we are looking for.
     * @return Returns the Access Point with the matching id or null.
     */
    public Optional<AccessPoint> getAccessPointById(String accessPointId) {

        return Arrays.stream(accessPoints)
                .filter(ap -> ap.getAccessPointId().equals(accessPointId))
                .findAny();
    }

    /**
     * @return Returns the JSON representation of the Access Points
     * create / update Request.
     */
    public String toJson() {

        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
