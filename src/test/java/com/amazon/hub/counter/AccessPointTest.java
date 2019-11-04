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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the Access Points basic validations.
 */
public class AccessPointTest {
    /**
     * Test an exception is thrown when the user tries to submit a feed with
     * two standard hours set for one same day.
     */
    @Test
    @DisplayName("Invalid standard days (repeated day)")
    public void standardDaysDuplicatedTest() {
        // Create an array of Access Points
        AccessPoint[] accessPoints = new AccessPoint[1];

        // Create Address
        Address address = Address.builder()
                .addressFieldOne("1918 8th Ave, Seattle, WA 98101, USA")
                .city("Seattle")
                .postalCode("98101")
                .countryCode("US")
                .latitude("47.615564")
                .longitude("-122.335819")
                .build();

        // Create Capabilities
        String[] capabilities = { "PICK_UP", "DROP_OFF" };

        // Create Standard Hours
        StandardHours[] standardHours = new StandardHours[2];

        // Monday
        standardHours[0] = StandardHours.builder()
                .day("MONDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Monday
        standardHours[1] = StandardHours.builder()
                .day("MONDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();


        assertThrows(IllegalArgumentException.class, () -> {
            // Create an Access Point
            accessPoints[0] = AccessPoint.builder()
                    .accessPointId("AMAZON-US-HQ")
                    .accessPointName("Amazon Hub Counter - Amazon US HQ")
                    .isActive(true)
                    .timeZone("America/Los_Angeles")
                    .address(address)
                    .capabilities(capabilities)
                    .standardHoursList(standardHours)
                    .build();
        });
    }

    @Test
    @DisplayName("Invalid standard days (too many days)")
    public void standardDaysManyDaysTest() {
        // Create an array of Access Points
        AccessPoint[] accessPoints = new AccessPoint[1];

        // Create Address
        Address address = Address.builder()
                .addressFieldOne("1918 8th Ave, Seattle, WA 98101, USA")
                .city("Seattle")
                .postalCode("98101")
                .countryCode("US")
                .latitude("47.615564")
                .longitude("-122.335819")
                .build();

        // Create the Communication Details
        CommunicationDetails communicationDetails = CommunicationDetails.builder()
                .phoneNumber("00 1 206-922-0880")
                .emailId("store-id@example.com")
                .build();

        // Create Capabilities
        String[] capabilities = {"PICK_UP", "DROP_OFF"};

        // Create Standard Hours
        StandardHours[] standardHours = new StandardHours[8];

        // Monday
        standardHours[0] = StandardHours.builder()
                .day("MONDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Tuesday
        standardHours[1] = StandardHours.builder()
                .day("TUESDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Wednesday
        standardHours[2] = StandardHours.builder()
                .day("WEDNESDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Thursday
        standardHours[3] = StandardHours.builder()
                .day("THURSDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Friday
        standardHours[4] = StandardHours.builder()
                .day("FRIDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Saturday
        standardHours[5] = StandardHours.builder()
                .day("SATURDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Sunday
        standardHours[6] = StandardHours.builder()
                .day("SUNDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Sunday
        standardHours[7] = StandardHours.builder()
                .day("SUNDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            // Create an Access Point
            accessPoints[0] = AccessPoint.builder()
                    .accessPointId("AMAZON-US-HQ")
                    .accessPointName("Amazon Hub Counter - Amazon US HQ")
                    .isActive(true)
                    .timeZone("America/Los_Angeles")
                    .address(address)
                    .communicationDetails(communicationDetails)
                    .capabilities(capabilities)
                    .standardHoursList(standardHours)
                    .build();
        });
    }

    @Test
    @DisplayName("Invalid FeedRequest (accessPointId duplicated)")
    public void accessPointIdDuplicatedTest() {
        // Create an array of Access Points
        AccessPoint[] accessPoints = new AccessPoint[2];

        // Create Address
        Address address = Address.builder()
                .addressFieldOne("1918 8th Ave, Seattle, WA 98101, USA")
                .city("Seattle")
                .postalCode("98101")
                .countryCode("US")
                .latitude("47.615564")
                .longitude("-122.335819")
                .build();

        // Create Capabilities
        String[] capabilities = {"PICK_UP", "DROP_OFF"};

        // Create Standard Hours
        StandardHours[] standardHours = new StandardHours[2];

        // Monday
        standardHours[0] = StandardHours.builder()
                .day("MONDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        // Tuesday
        standardHours[1] = StandardHours.builder()
                .day("Tuesday")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();


        // Create an Access Point
        accessPoints[0] = AccessPoint.builder()
                .accessPointId("AMAZON-US-HQ")
                .accessPointName("Amazon Hub Counter - Amazon US HQ")
                .isActive(true)
                .timeZone("America/Los_Angeles")
                .address(address)
                .capabilities(capabilities)
                .standardHoursList(standardHours)
                .build();


        // Create an Access Point
        accessPoints[1] = AccessPoint.builder()
                .accessPointId("AMAZON-US-HQ")
                .accessPointName("Amazon Hub Counter - Amazon US HQ")
                .isActive(true)
                .timeZone("America/Los_Angeles")
                .address(address)
                .capabilities(capabilities)
                .standardHoursList(standardHours)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            AccessPointsFeedRequest accessPointsFeedRequest = AccessPointsFeedRequest.builder()
                    .accessPoints(accessPoints)
                    .build();
        });
    }
}
