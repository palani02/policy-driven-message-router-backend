# Database Schema Documentation

## Overview

The Policy-Driven Message Routing System uses a relational database (MySQL) to store users, their communication preferences, messages, delivery history, and permanently failed messages (Dead Letter Queue).

The database design separates user information, routing configuration, message lifecycle, and delivery tracking to improve maintainability, extensibility, and scalability.

---

# Entity Relationship Diagram

```text
+-------------------+
|      USERS        |
+-------------------+
| id (PK)           |
| name              |
| email             |
| phone_number      |
| created_at        |
| updated_at        |
+---------+---------+
          |
          | 1 : 1
          |
          ▼
+-------------------------+
|   USER_PREFERENCES      |
+-------------------------+
| id (PK)                |
| user_id (FK)           |
| email_enabled          |
| sms_enabled            |
+------------------------+

          |
          | 1 : N
          |
          ▼
+-----------------------+
|      MESSAGES         |
+-----------------------+
| id (PK)              |
| user_id (FK)         |
| subject              |
| content              |
| message_type         |
| priority             |
| status               |
| created_at           |
| updated_at           |
+-----------+----------+
            |
            | 1 : N
            |
            ▼
+-----------------------------+
|     MESSAGE_DELIVERIES      |
+-----------------------------+
| id (PK)                    |
| message_id (FK)            |
| channel_type               |
| status                     |
| attempt_count              |
| failure_reason             |
| created_at                 |
| updated_at                 |
+---------------+------------+
                |
                | 1 : 1
                |
                ▼
+----------------------------+
|    DEAD_LETTER_MESSAGES    |
+----------------------------+
| id (PK)                   |
| message_delivery_id (FK)  |
| reason                    |
| failed_at                 |
+---------------------------+
```

---

# Table Documentation

## 1. USERS

### Purpose

Stores all registered users who can receive notifications through Email and SMS.

### Columns

| Column       | Type      | Description          |
| ------------ | --------- | -------------------- |
| id           | BIGINT    | Primary Key          |
| name         | VARCHAR   | User name            |
| email        | VARCHAR   | Email address        |
| phone_number | VARCHAR   | Mobile number        |
| created_at   | TIMESTAMP | Record creation time |
| updated_at   | TIMESTAMP | Last update time     |

### Relationships

* One User has one User Preference.
* One User can have many Messages.

---

# 2. USER_PREFERENCES

### Purpose

Stores the preferred communication channels for each user.

This table allows the routing engine to determine whether Email or SMS should be used when business rules depend on user preferences.

### Columns

| Column        | Type    | Description         |
| ------------- | ------- | ------------------- |
| id            | BIGINT  | Primary Key         |
| user_id       | BIGINT  | Foreign Key → USERS |
| email_enabled | BOOLEAN | Email preference    |
| sms_enabled   | BOOLEAN | SMS preference      |

### Relationships

* One-to-One with USERS.

---

# 3. MESSAGES

### Purpose

Stores every message submitted to the system before routing begins.

A message represents the business request received from the client application.

### Columns

| Column       | Type      | Description                                         |
| ------------ | --------- | --------------------------------------------------- |
| id           | BIGINT    | Primary Key                                         |
| user_id      | BIGINT    | Foreign Key → USERS                                 |
| subject      | VARCHAR   | Message subject                                     |
| content      | TEXT      | Message body                                        |
| message_type | ENUM      | OTP, PROMOTION, CRITICAL, REMINDER                  |
| priority     | ENUM      | LOW, MEDIUM, HIGH                                   |
| status       | ENUM      | CREATED, PROCESSING, SENT, FAILED, PARTIALLY_FAILED |
| created_at   | TIMESTAMP | Created time                                        |
| updated_at   | TIMESTAMP | Updated time                                        |

### Relationships

* Many Messages belong to one User.
* One Message can generate multiple Message Deliveries.

---

# 4. MESSAGE_DELIVERIES

### Purpose

Tracks every delivery attempt for every communication channel.

If one message is delivered through both Email and SMS, two delivery records are created.

This table provides complete delivery history, retry count, and failure information.

### Columns

| Column         | Type      | Description              |
| -------------- | --------- | ------------------------ |
| id             | BIGINT    | Primary Key              |
| message_id     | BIGINT    | Foreign Key → MESSAGES   |
| channel_type   | ENUM      | EMAIL or SMS             |
| status         | ENUM      | PROCESSING, SENT, FAILED |
| attempt_count  | INTEGER   | Retry count              |
| failure_reason | TEXT      | Failure details          |
| created_at     | TIMESTAMP | Created time             |
| updated_at     | TIMESTAMP | Updated time             |

### Relationships

* Many delivery records belong to one Message.
* One failed delivery may create one Dead Letter Message.

---

# 5. DEAD_LETTER_MESSAGES

### Purpose

Stores messages that permanently failed after exhausting all retry attempts.

These records can later be reprocessed or manually investigated by administrators.

### Columns

| Column              | Type      | Description                      |
| ------------------- | --------- | -------------------------------- |
| id                  | BIGINT    | Primary Key                      |
| message_delivery_id | BIGINT    | Foreign Key → MESSAGE_DELIVERIES |
| reason              | TEXT      | Failure reason                   |
| failed_at           | TIMESTAMP | Failure timestamp                |

### Relationships

* One-to-One with MESSAGE_DELIVERIES.

---

# Database Flow

The following sequence describes how data moves through the database.

```
Client Request
      │
      ▼
Create Message
      │
      ▼
Save into MESSAGES
      │
      ▼
Evaluate Routing Rules
      │
      ▼
Queue Message
      │
      ▼
Worker Processes Message
      │
      ▼
Create MESSAGE_DELIVERIES
      │
      ▼
Send Email / SMS
      │
      ▼
Update Delivery Status
      │
      ▼
Retries (if needed)
      │
      ▼
Permanent Failure
      │
      ▼
Store in DEAD_LETTER_MESSAGES
```

---

# Design Rationale

The database has been normalized into separate entities to achieve the following goals:

* Separate user information from communication preferences.
* Maintain a complete history of every delivery attempt.
* Support multiple communication channels for a single message.
* Enable retry tracking without modifying the original message.
* Isolate permanently failed messages in a dedicated Dead Letter Queue.
* Allow future expansion to additional channels such as Push Notifications, WhatsApp, or Slack without changing the existing schema.

---

# Summary

The current database schema supports all core functional requirements of the Policy-Driven Message Routing System, including:

* User management
* Preference-based routing
* Message lifecycle tracking
* Multi-channel delivery
* Retry management
* Dead Letter Queue (DLQ)
* Delivery history and auditability
