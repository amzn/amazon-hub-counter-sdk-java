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

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Builder;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents an Amazon Hub Counter Pick-Up Point.
 */
@Getter
@Builder
public class AccessPoint {
    @NonNull
    private final String accessPointId;
    @NonNull
    private final String accessPointName;
    @NonNull
    private final Boolean isActive;
    private final Boolean isRestrictedAccess;
    @NonNull
    private final String timeZone;
    @NonNull
    private final Address address;
    private final Capacity capacity;
    private final String terminationDate;
    @NonNull
    private final String[] capabilities;
    @SerializedName("standardHours")
    @NonNull
    private final StandardHours[] standardHoursList;
    private final ExceptionalClosure[] exceptionalClosures;
    private final CommunicationDetails communicationDetails;

    /**
     * Override lombok default constructor to add validations.
     */
    public static class AccessPointBuilder {

        /**
         * Override lombok default constructor to add validations.
         *
         * @param standardHoursList
         * @return A valid list of standard hours.
         * @throws IllegalArgumentException If there is an invalid standard
         *                                  hours
         */
        public AccessPointBuilder standardHoursList(
                final StandardHours[] standardHoursList) {

            this.standardHoursList = standardHoursList;

            validateStandardHours();

            return this;
        }

        private void validateStandardHours() {
            if (standardHoursList.length > 7) {
                throw new IllegalArgumentException(String.format("Invalid "
                        + "StandardHours "
                        + "list size (%d), maximum list size can be 7(number "
                        + "of week days).", standardHoursList.length)
                );
            }

            List<Map.Entry<String, Long>> repeatedDays =
                    Arrays.stream(standardHoursList)
                            .collect(Collectors.groupingBy(day ->
                                            day.getDay().toUpperCase(),
                                    Collectors.counting()
                                    )
                            )
                            .entrySet()
                            .stream()
                            .filter(frequency -> frequency.getValue() > 1)
                            .collect(Collectors.toList());

            if (repeatedDays.size() > 0) {
                throw new IllegalArgumentException(String.format("Duplicate "
                                + "standard hours found for week day %s.",
                        repeatedDays.get(0).getKey()
                ));
            }
        }

    }

    /**
     * @param day The name of the day to search for.
     * @return The StandardHours object that matches the given day name or null
     * if it is not present.
     */
    public Optional<StandardHours> getStandardHoursByDayName(
            @NonNull String day) {

        return Arrays.stream(standardHoursList)
                .filter(standardHours -> day.compareToIgnoreCase(
                        standardHours.getDay()) == 0
                )
                .findAny();
    }


}

