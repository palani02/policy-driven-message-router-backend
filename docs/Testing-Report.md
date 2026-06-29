# Testing Report

# Overview

This document describes the functional testing performed for the Policy-Driven Message Routing System.

The application was tested using **Postman**, **MySQL**, **Gmail SMTP**, and **Twilio SMS** to verify that all routing policies, retry mechanisms, asynchronous processing, fallback logic, and tracking features work as expected.

---

# Test Environment

| Component      | Value      |
| -------------- | ---------- |
| Java           | 17         |
| Spring Boot    | 4.x        |
| Database       | MySQL      |
| Email Provider | Gmail SMTP |
| SMS Provider   | Twilio     |
| API Client     | Postman    |
| Build Tool     | Maven      |

---

# Test Case 1 — OTP Message

## Objective

Verify that OTP messages are routed through SMS.

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 1,
  "subject": "OTP Verification",
  "content": "Your OTP is 483921",
  "messageType": "OTP",
  "priority": "HIGH"
}
```

### Expected Result

* Message stored successfully
* SMS sent via Twilio
* Message status updated to SENT

### Actual Result

SMS was successfully delivered through Twilio and the message status was updated to `SENT`.

### Status

PASS

---

# Test Case 2 — OTP SMS Failure with Email Fallback

## Objective

Verify that Email is used when SMS delivery fails.

### Preconditions

Configure an invalid Twilio phone number or invalid credentials.

### Request

```json
{
  "userId": 1,
  "subject": "OTP Verification",
  "content": "Your OTP is 758463",
  "messageType": "OTP",
  "priority": "HIGH"
}
```

### Expected Result

* SMS retries three times
* SMS marked as FAILED
* Email automatically sent
* Message successfully delivered through Email

### Actual Result

SMS failed after the configured retry attempts. The system automatically routed the message to the Email channel, and the Email was delivered successfully.

### Status

PASS

---

# Test Case 3 — Promotion Message

## Objective

Verify that promotional messages are routed only through Email.

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 1,
  "subject": "Special Offer",
  "content": "Get 40% discount this weekend.",
  "messageType": "PROMOTION",
  "priority": "LOW"
}
```

### Expected Result

* Email sent
* SMS not triggered

### Actual Result

The message was successfully delivered through the Email channel only.

### Status

PASS

---

# Test Case 4 — Critical Alert

## Objective

Verify that critical alerts are delivered through both Email and SMS.

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 1,
  "subject": "Server Alert",
  "content": "CPU usage exceeds 95%.",
  "messageType": "CRITICAL",
  "priority": "HIGH"
}
```

### Expected Result

* Email sent
* SMS sent
* Delivery records created for both channels

### Actual Result

The message was successfully delivered through both Email and SMS channels.

### Status

PASS

---

# Test Case 5 — Reminder with User Preferences

## Objective

Verify routing based on user communication preferences.

### Preconditions

Configure user preferences.

Example:

```
Email Enabled = true
SMS Enabled = false
```

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 1,
  "subject": "Meeting Reminder",
  "content": "Project review at 10:00 AM.",
  "messageType": "REMINDER",
  "priority": "LOW"
}
```

### Expected Result

Only Email should be selected for delivery.

### Actual Result

The routing engine correctly selected Email according to the configured user preferences.

### Status

PASS

---

# Test Case 6 — Time-Based Routing

## Objective

Verify default routing when no communication channel is selected through user preferences.

### Scenario

If both Email and SMS preferences are disabled:

* Daytime → Email
* Night-time → SMS

### Expected Result

Routing should change based on the current system time.

### Actual Result

The routing engine selected the appropriate communication channel based on the configured time-based policy.

### Status

PASS

---

# Test Case 7 — Retry Policy

## Objective

Verify the retry mechanism.

### Preconditions

Disable Gmail SMTP or Twilio credentials.

### Expected Result

* Attempt 1
* Attempt 2
* Attempt 3

After the maximum retry count:

* Delivery status becomes FAILED

### Actual Result

The worker attempted delivery three times before marking the delivery as FAILED.

### Status

PASS

---

# Test Case 8 — Dead Letter Queue

## Objective

Verify permanently failed messages are stored in the Dead Letter Queue.

### Preconditions

Configure an invalid provider to force permanent delivery failure.

### Expected Result

A record should be inserted into:

```
dead_letter_messages
```

### Actual Result

The failed message was successfully stored in the Dead Letter Queue after all retry attempts were exhausted.

### Status

PASS

---

# Test Case 9 — Message Tracking API

## Objective

Verify message lifecycle tracking.

### HTTP Method

GET

### URL

```
/tracking/{messageId}
```

Example

```
GET /tracking/22
```

### Expected Result

The response should include:

* Overall Message Status
* Channel Status
* Retry Count
* Failure Reason (if applicable)

### Actual Result

The Tracking API returned the correct lifecycle information for the requested message.

### Status

PASS

---

# Test Case 10 — User Preference Filtering

## Objective

Verify routing behavior based on user communication preferences.

### Scenario 1

```
Email Enabled = true
SMS Enabled = false
```

Expected Result

Email only

---

### Scenario 2

```
Email Enabled = false
SMS Enabled = true
```

Expected Result

SMS only

---

### Scenario 3

```
Email Enabled = true
SMS Enabled = true
```

Expected Result

Both communication channels are available when permitted by routing rules.

### Actual Result

The routing engine correctly honored all configured user preferences.

### Status

PASS

---

# Test Case 11 — Invalid User

## Objective

Verify validation for an invalid user.

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 9999,
  "subject": "Invalid User",
  "content": "Testing",
  "messageType": "OTP",
  "priority": "HIGH"
}
```

### Expected Result

The system should return a "User Not Found" response.

### Actual Result

The request was rejected because the specified user does not exist.

### Status

PASS

---

# Test Case 12 — Invalid Message Type

## Objective

Verify validation of unsupported message types.

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 1,
  "subject": "Test",
  "content": "Hello",
  "messageType": "NORMAL",
  "priority": "LOW"
}
```

### Expected Result

The request should fail with an HTTP validation error.

### Actual Result

The invalid message type was correctly rejected.

### Status

PASS

---

# Test Case 13 — Empty Content Validation

## Objective

Verify validation for empty message content.

### HTTP Method

POST

### URL

```
/messages
```

### Request

```json
{
  "userId": 1,
  "subject": "Test",
  "content": "",
  "messageType": "OTP",
  "priority": "HIGH"
}
```

### Expected Result

The request should fail due to validation constraints.

### Actual Result

The validation layer correctly rejected the request.

### Status

PASS

---

# Test Summary

| Test Case | Description               | Result |
| --------- | ------------------------- | ------ |
| TC-01     | OTP Routing               | PASS   |
| TC-02     | OTP Email Fallback        | PASS   |
| TC-03     | Promotion Routing         | PASS   |
| TC-04     | Critical Alert Routing    | PASS   |
| TC-05     | Reminder Routing          | PASS   |
| TC-06     | Time-Based Routing        | PASS   |
| TC-07     | Retry Mechanism           | PASS   |
| TC-08     | Dead Letter Queue         | PASS   |
| TC-09     | Tracking API              | PASS   |
| TC-10     | User Preference Filtering | PASS   |
| TC-11     | Invalid User              | PASS   |
| TC-12     | Invalid Message Type      | PASS   |
| TC-13     | Validation Checks         | PASS   |

---

# Conclusion

The Policy-Driven Message Routing System was successfully tested against all functional requirements defined in the technical assessment.

The testing validated:

* Policy-based routing
* Dynamic rule evaluation
* User preference filtering
* Time-based routing
* Multi-channel message delivery
* Email integration using Gmail SMTP
* SMS integration using Twilio
* Retry policy
* Email fallback after SMS failure
* Dead Letter Queue (DLQ)
* Message lifecycle tracking
* Input validation and edge-case handling

All executed test cases passed successfully.
