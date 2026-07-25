# ADR-006 — Definition of Done (DoD)

## Status
Proposed (v1.0) - July 24, 2026

## Context
Following the adoption of **Big Sprints (Epics)** as the primary development model (ADR-005), it is essential to establish objective and measurable criteria to determine when a Sprint is considered finished. The completion of a Sprint is not merely about coding features; it encompasses technical quality, stability, documentation, and production readiness to ensure a consistent path toward the V1 release.

## Decision
From Sprint 45 onwards, no Big Sprint shall be considered closed without full compliance with the **Definition of Done (DoD)** criteria defined in this document.

## Definition of Done
A Big Sprint is considered concluded only when ALL the following items are met:

### 1. Scope
*   All scope approved during Sprint Planning is implemented.
*   No mandatory functionality is left pending.
*   All acceptance criteria are satisfied.

### 2. Architecture
*   Hexagonal Architecture is preserved.
*   No violations of DDD, SOLID, or Clean Architecture principles.
*   No layer violations or improper dependencies introduced.

### 3. Code Quality
*   Code follows project standards and naming conventions.
*   No significant duplications.
*   No critical TODOs, temporary code, or provisional implementation comments.

### 4. Testing
*   All unit tests pass.
*   All integration tests pass.
*   No tests were removed to facilitate the Sprint.
*   New tests were added to cover new features and edge cases.

### 5. Build
*   The command `./mvnw clean verify` executes successfully without errors or failures.

### 6. CI/CD
*   GitHub Actions workflow is entirely green.
*   No regressions detected in the pipeline.

### 7. API
*   Swagger/OpenAPI documentation is updated.
*   Contracts are compatible with the implementation.
*   Endpoints function according to specifications.

### 8. Security
*   No known vulnerabilities introduced.
*   Permissions and RBAC remain intact.
*   JWT authentication is fully functional.
*   No sensitive information is exposed in logs or APIs.

### 9. Database
*   Flyway migrations are consistent and valid.
*   No incompatible changes or data loss risks.

### 10. Events (if applicable)
*   Outbox pattern is functioning correctly.
*   Event publication is validated.
*   Data consistency is preserved across the messaging flow.

### 11. Observability
*   Logs are coherent and follow established patterns.
*   Metrics are preserved and updated where applicable.
*   Health Checks are functioning correctly.

### 12. Documentation
*   Technical documentation and Obsidian notes are updated.
*   ADRs are created or updated as necessary.
*   README is updated if relevant impacts occur.

### 13. Product Readiness
*   The Sprint objectively moves the product closer to the V1 pilot.
*   No V2 items were implemented.

### 14. Technical Approval
*   Architecture Audit is performed and passed.
*   No critical technical issues remain open.

### 15. Final Approval
*   The Product Owner (PO) approves the functional delivery.
*   The Technical Architect (TA) approves the technical implementation.

## Official Checklist
At the end of every Big Sprint, a checklist must be presented with all items marked as:
*   [ ] Not Met
*   [x] Met

The Sprint can only be closed when ALL items are marked as **[x] Met**.

## Consequences
*   **Increased Quality**: Consistent adherence to standards.
*   **Reduced Regressions**: Stronger testing and CI/CD validation.
*   **Predictability**: Clearer understanding of "Finished".
*   **Stability**: Continuous preparation for production environments.
*   **Reliability**: Roadmap progress is based on truly completed features.
