# Policy-Driven Message Routing System

A Spring Boot based asynchronous policy-driven message routing system that intelligently delivers messages using Email and SMS based on configurable business rules.

---

# Features

* Policy-Based Routing Engine
* Rules Engine
* Email Delivery (Gmail SMTP)
* SMS Delivery (Twilio)
* User Preference Filtering
* Time-Based Routing
* Priority-Based Routing
* Retry Mechanism
* Dead Letter Queue (DLQ)
* Message Tracking API
* Asynchronous Processing using Worker Thread
* Extensible Channel Architecture

---

# Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA
* MySQL
* Gmail SMTP
* Twilio SMS API
* Lombok
* Maven
* Postman

---

# Project Architecture

```text
                                        CLIENT
                                           │
                                           │
                                  POST /messages
                                           │
                                           ▼
                                MessageController
                                           │
                                           ▼
                                 MessageService
                                           │
                 ┌─────────────────────────┴─────────────────────────┐
                 │                                                   │
                 │ 1. Save Message                                   │
                 │ 2. Load User                                      │
                 │ 3. Load User Preferences                          │
                 │ 4. Invoke Routing Engine                          │
                 └─────────────────────────┬─────────────────────────┘
                                           │
                                           ▼
                                  RoutingEngine
                                           │
                                           ▼
                                   RulesEngine
                                           │
        ┌──────────────────────────────────┼──────────────────────────────────┐
        │                                  │                                  │
        │ Message Type                     │ User Preferences                 │
        │ Priority                         │ Time of Day                      │
        │ Business Rules                   │                                  │
        └──────────────────────────────────┼──────────────────────────────────┘
                                           │
                                           ▼
                               Routing Decision
                       (EMAIL / SMS / EMAIL + SMS)
                                           │
                                           ▼
                                   MessageQueue
                               (BlockingQueue)
                                           │
                              Async Processing Begins
                                           │
                                           ▼
                                   MessageWorker
                                           │
                              Creates Delivery Record
                                           │
                                           ▼
                                  ChannelRegistry
                                           │
                    ┌──────────────────────┴──────────────────────┐
                    │                                             │
                    ▼                                             ▼
               EmailChannel                                  SmsChannel
                    │                                             │
                    ▼                                             ▼
              EmailServiceImpl                            SmsServiceImpl
                    │                                             │
                    ▼                                             ▼
               Gmail SMTP API                               Twilio API
                    │                                             │
             Success / Failure                           Success / Failure
                    │                                             │
                    └──────────────────────┬──────────────────────┘
                                           │
                                  Retry Mechanism
                                (Maximum 3 Attempts)
                                           │
                              ┌────────────┴────────────┐
                              │                         │
                           Success                  Permanent Failure
                              │                         │
                              ▼                         ▼
                  Update MessageDelivery        Dead Letter Queue (DLQ)
                              │                         │
                              └────────────┬────────────┘
                                           │
                              OTP SMS Failure Only
                                           │
                                           ▼
                              Automatic Email Fallback
                                           │
                                           ▼
                             Update Final Message Status
                          (SENT / FAILED / PARTIALLY_FAILED)
                                           │
                                           ▼
                                 Tracking Repository
                                           │
                                           ▼
                             GET /tracking/{messageId}
```


---

# Routing Rules

| Message Type      | Routing            |
| ----------------- | ------------------ |
| OTP               | SMS                |
| OTP (SMS Failure) | Email Fallback     |
| PROMOTION         | Email              |
| CRITICAL          | SMS + Email        |
| REMINDER          | User Preferences   |
| Default           | Time-based Routing |

---

# Failure Handling

* Maximum Retry Count : 3
* Retry Delay : 1 Second
* Failed messages are stored in Dead Letter Queue
* Complete delivery history stored in MessageDelivery table

---

# Tracking

Every message can be tracked using

```
GET /tracking/{messageId}
```

Example Response

```
{
   "messageId":22,
   "overallStatus":"SENT",
   "channels":[
      {
         "channel":"EMAIL",
         "status":"SENT"
      },
      {
         "channel":"SMS",
         "status":"SENT"
      }
   ]
}
```

---

# API

## Create Message

POST

```
/messages
```

Example

```
{
  "userId":1,
  "subject":"Server Alert",
  "content":"CPU Usage High",
  "messageType":"CRITICAL",
  "priority":"HIGH"
}
```

---

## Track Message

GET

```
/tracking/{id}
```

---

# Setup Instructions

## Clone

```
git clone https://github.com/palani02/policy-driven-message-router-backend.git
```

---

## Configure Database

Create MySQL database

```
policy_router
```

Update

```
application.properties
```

```
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```

---

## Configure Gmail SMTP

```
spring.mail.username=
spring.mail.password=
```

Use Gmail App Password.

---

## Configure Twilio

```
twilio.account.sid=
twilio.auth.token=
twilio.phone.number=
```

---

## Run

```
mvn spring-boot:run
```



---

# Implemented Design Patterns

* Strategy Pattern
* Registry Pattern
* Builder Pattern
* Repository Pattern
* Dependency Injection
* Producer-Consumer Pattern


---

# Author

Palani Paranthaman

