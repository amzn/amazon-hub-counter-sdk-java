

## Java SDK for Amazon Hub Counter API
This module implements all the logic to resolve the Amazon Hub Counter API requests used for manage access points locations. This includes the creation, update, activation and deactivation of the access points points.

### Installation
Download the _amazon-hub-counter-sdk-1.0.jar_ file from the releases or:

1. Clone this repository.
2. Run:
```cd amazon-hub-counter-sdk-java```
4. Compile the project:
   ```mvn clean compile assembly:single```
5. Add _target/amazon-hub-counter-sdk-1.0-jar-with-dependencies.jar_ as a reference to your project or run ```mvn clean install``` if you want to install the package to your local Maven repository.

### Usage
#### 1. Import the package

```java
import com.amazon.hub.counter.AmazonHubCounterFeedAPI;
```

#### 2. Set your credentials
```java
ClientCredentials clientCredentials = ClientCredentials.builder()
                .clientId("amzn1.application-oa2-client.xxxxx")
                .clientSecret("client_secret")
                .build();
```
* _clientId_ has to be filled with the _clientId_ corresponding to the security profile created in the developer.amazon.com portal, use the _clientId_ corresponding to the environment that you want to send the requests (sandbox/production).
* _clientSecret_ is the password for this security profile, this also can be found in the developer.amazon.com portal.

#### 3. Instantiate the API handler
```java
AmazonHubCounterFeedAPI api = new AmazonHubCounterFeedAPI(
									clientCredentials,
									"https://accesspoints-api-sandbox-eu.amazon.com",
									"https://api.amazon.com/auth/o2/token"
							);
```
* Set the _apiEndpoint_ to the corresponding url according to your region and environment.

#### 4. Submit a store
```java
AccessPoint[] accessPoints = new AccessPoint[1];

Address address = Address.builder()
        .addressFieldOne("1918 8th Ave, Seattle, WA 98101, USA")
        .city("Seattle")
        .postalCode("98101")
        .countryCode("US")
        .latitude("47.615564")
        .longitude("-122.335819")
        .build();

String[] capabilities = {"PICK_UP", "DROP_OFF"};

ExceptionalClosure[] exceptionalClosures = new ExceptionalClosure[1];
exceptionalClosures[0] = ExceptionalClosure.builder()
        .startDateTime("2038-01-19T00:00:00")
        .endDateTime("2038-01-20T00:00:00")
        .build();

CommunicationDetails communicationDetails = CommunicationDetails.builder()
        .phoneNumber("00 1 206-922-0880")
        .emailId("store-id@example.com")
        .build();

StandardHours[] standardHours = new StandardHours[6];

standardHours[0] = StandardHours.builder()
        .day("MONDAY")
        .openingTime("08:30:00")
        .closingTime("20:00:00")
        .build();
standardHours[1] = StandardHours.builder()
        .day("TUESDAY")
        .openingTime("08:30:00")
        .closingTime("20:00:00")
        .build();
standardHours[2] = StandardHours.builder()
        .day("WEDNESDAY")
        .openingTime("08:30:00")
        .closingTime("20:00:00")
        .build();
standardHours[3] = StandardHours.builder()
        .day("THURSDAY")
        .openingTime("08:30:00")
        .closingTime("20:00:00")
        .build();
standardHours[4] = StandardHours.builder()
        .day("FRIDAY")
        .openingTime("08:30:00")
        .closingTime("20:00:00")
        .build();
MidDayClosure[] midDayClosures = new MidDayClosure[1];
midDayClosures[0] = MidDayClosure.builder()
        .startTime("12:00:00")
        .endTime("13:00:00")
        .build();
standardHours[5] = StandardHours.builder()
        .day("SATURDAY")
        .openingTime("08:30:00")
        .closingTime("20:00:00")
        .midDayClosures(midDayClosures)
        .build();

accessPoints[0] = AccessPoint.builder()
        .accessPointId("AMAZON-US-HQ")
        .accessPointName("Amazon Hub Counter - Amazon US HQ")
        .isActive(true)
        .isRestrictedAccess(false)
        .timeZone("America/Los_Angeles")
        .address(address)
        .terminationDate("2199-12-31")
        .capabilities(capabilities)
        .standardHoursList(standardHours)
        .exceptionalClosures(exceptionalClosures)
        .communicationDetails(communicationDetails)
        .build();

AccessPointsFeedRequest accessPointsFeedRequest = AccessPointsFeedRequest.builder()
        .accessPoints(accessPoints)
        .build();

String accessToken = api.getAccessToken();

String feedId = api.postFeed(accessPointsFeedRequest,
        FeedType.STORE_FEED,
        accessToken);
```

#### 5. Check the status of the feed submission
```java
Feed feed = api.getFeedById(feedId, accessToken);
 ```
* You can get the status of the feed calling the method _feed.getStatus()_, this can result in:
	* ___Processing___ the feed has not been processed yet, this task is asynchronous so you have to poll for updates, build a retry logic with a max retries/timeout break builtin.
	* ___Completed___ the feed processing is completed, an OutputDocument is generated with the processing details.
	* ___Failed___ the feed processing failed, no OutputDocument could be generated, if this occurs report the issue to your Amazon POC.

#### 6. Check the processing results (OutputDocument)
```java
OutputDocument doc= api.getOutputDocument(
											feed.getFeedId(),
											feed.getOutputDocuments()[0].getDocumentId(),
											accessToken
										);
 ```

* Check if _doc.getNoOfAccessPointsFailedToProcess()_ is greater than 0, if that's the case, you can retrieve the details of the errors calling the method doc.getFailedAccessPointProcessingDetails()_.
* If _doc.getNoOfAccessPointsProcessed() == doc.getNoOfAccessPointsSuccessfullyProcessed()_, all access points were successfully processed.

 ___NOTE:___ A sample code using this package exists this repository: [amazon-hub-counter-api-samples/java](https://github.com/amzn/amazon-hub-counter-api-samples/java).

### Copyright and License
All content in this repository, unless otherwise stated, is Copyright © 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Except where otherwise noted, all examples in this collection are licensed under the MIT-0 license. The full license text is provided in the LICENSE file accompanying this repository.