# Design Decisions

# Overview

This document explains the major architectural decisions, design patterns, trade-offs, and future scalability considerations adopted while building the Policy-Driven Message Routing System.

---

# 1. Why a Policy-Driven Architecture?

Instead of hardcoding delivery logic inside the service layer, a separate Routing Engine and Rules Engine were introduced.

Benefits:

- Separation of concerns
- Easy addition of new routing policies
- Centralized business rule management
- Better maintainability
- Improved extensibility

The routing logic remains independent of delivery channel implementations.

---

# 2. Why Separate Routing Engine and Rules Engine?

The system divides responsibilities into two layers.

## Routing Engine

Responsible for:

- Receiving the message
- Loading user preferences
- Invoking the Rules Engine
- Producing a routing decision

It coordinates the routing process but does not contain business rules.

---

## Rules Engine

Responsible for evaluating business policies such as:

- Message type
- User preferences
- Priority
- Time of day
- Fallback conditions

This separation allows routing rules to evolve without affecting the routing workflow.

---

# 3. Why Channel Abstraction?

Email and SMS delivery are implemented through a common Channel interface.

Benefits:

- Loose coupling
- Easy extension
- Open/Closed Principle
- New delivery channels can be added without modifying existing code

Current implementations include:

- EmailChannel
- SmsChannel

Future channels may include:

- WhatsApp
- Push Notification
- Slack
- Microsoft Teams

---

# 4. Why Strategy Pattern?

Different communication channels have different delivery mechanisms.

Each channel encapsulates its own implementation while exposing the same interface.

Benefits:

- Runtime channel selection
- Independent implementations
- Cleaner code
- Easier testing

---

# 5. Why Registry Pattern?

The ChannelRegistry maps channel types to their implementations.

Instead of multiple if-else or switch statements, the worker retrieves the required implementation dynamically.

Benefits:

- Eliminates conditional logic
- Improves scalability
- Simplifies channel management

---

# 6. Why Asynchronous Processing?

Message delivery is performed asynchronously using an in-memory BlockingQueue and a background worker thread.

Workflow:

Client Request

↓

Store Message

↓

Place into Queue

↓

Worker Thread

↓

Channel Delivery

Benefits:

- Faster API response
- Non-blocking processing
- Better scalability
- Improved user experience

---

# 7. Why Retry Mechanism?

External communication providers may experience temporary failures.

The worker retries failed deliveries before marking them as permanently failed.

Current Policy

- Maximum Retries: 3
- Retry Delay: 1 Second

Benefits:

- Handles transient failures
- Improves delivery success rate
- Reduces manual intervention

---

# 8. Why Dead Letter Queue (DLQ)?

If all retry attempts fail, the failed delivery is stored in the Dead Letter Queue.

Benefits:

- Prevents message loss
- Enables operational monitoring
- Supports manual replay
- Facilitates debugging

---

# 9. Why Email Fallback?

Business Requirement:

OTP messages should primarily use SMS.

If SMS delivery fails after all retry attempts:

SMS

↓

Retry

↓

Retry

↓

Retry

↓

Email Fallback

Benefits:

- Higher delivery reliability
- Better user experience
- Meets business continuity requirements

---

# 10. Why Message Tracking?

Every delivery attempt is recorded in the MessageDelivery table.

Tracking includes:

- Channel used
- Delivery status
- Retry count
- Failure reason
- Delivery timestamps

Benefits:

- Complete audit trail
- Operational visibility
- Easier troubleshooting

---

# 11. Database Design Decisions

The system separates message information from delivery information.

Messages table

Stores:

- Message content
- Subject
- Priority
- Type

MessageDeliveries table

Stores:

- Delivery channel
- Retry count
- Status
- Failure reason

Benefits:

- Normalized database design
- Supports multi-channel delivery
- Independent delivery tracking

---

# 12. Provider Integration

The system integrates with real communication providers.

Email

- Gmail SMTP

SMS

- Twilio

Using actual providers validates real-world integration rather than relying on mock implementations.

---

# 13. Design Patterns Used

## Strategy Pattern

Purpose

Allows interchangeable communication channels.

Used In

- EmailChannel
- SmsChannel

---

## Registry Pattern

Purpose

Resolves channel implementations dynamically.

Used In

- ChannelRegistry

---

## Builder Pattern

Purpose

Creates complex entity objects with improved readability.

Used For

- Message
- MessageDelivery
- DeadLetterMessage
- RoutingDecision

---

## Repository Pattern

Purpose

Separates persistence logic from business logic.

Used Through

- Spring Data JPA repositories

---

## Dependency Injection

Purpose

Manages object creation and dependencies.

Implemented using Spring Framework.

Benefits:

- Loose coupling
- Easier testing
- Better maintainability

---

## Producer-Consumer Pattern

Purpose

Supports asynchronous message processing.

Producer

MessageService

Consumer

MessageWorker

Queue

BlockingQueue

---

# Trade-offs

## Current Implementation

Queue

- In-memory BlockingQueue

Advantages

- Simple
- Lightweight
- Easy to implement

Limitations

- Messages are lost if the application stops
- Single JVM only

Future Improvement

- RabbitMQ
- Apache Kafka

---

## Rules Engine

Current

Business rules implemented in Java code.

Advantages

- Fast execution
- Easy debugging

Future Improvement

- Store rules in a database
- External rule configuration
- Dynamic rule updates

---

## Worker

Current

Single background worker thread.

Advantages

- Simple
- Predictable

Future Improvement

- Multiple worker threads
- Thread pool
- Horizontal scaling

---

## Tracking

Current

Database-driven tracking.

Future Improvement

- Real-time dashboard
- Live delivery monitoring
- Metrics and alerts

---



---

# Conclusion

The Policy-Driven Message Routing System was designed with extensibility, maintainability, and scalability in mind.

The architecture separates routing, rule evaluation, asynchronous processing, delivery channels, retry handling, failure recovery, and tracking into independent components, allowing the system to evolve with minimal impact on existing functionality.

