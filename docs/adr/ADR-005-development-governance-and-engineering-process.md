# ADR-005 — Development Governance and Engineering Process

## Status
Proposed (v1.0) - July 24, 2026

## Context
After the consolidation of the base architecture and infrastructure in Sprint 44.4, the Circulou Backend project has transitioned into a new development phase. While previous sprints focused on building a solid architectural foundation (Hexagonal, DDD, Outbox, etc.), starting from Sprint 45, the primary objective is the delivery of a complete V1 (Version 1.0) of the product for a controlled pilot.

This shift necessitates a formal governance process to ensure that rapid feature development does not compromise architectural integrity, stability, and predictability, while minimizing technical debt and rework.

## Decision
The project officially adopts a **Big Sprint (Epic-based)** development model. Each sprint has a locked scope, and any changes must be explicitly approved by the Product Owner. The process is governed by a dual-gate validation system and clearly defined engineering roles.

### Roles and Responsibilities

#### Product Owner (PO)
*   Responsible for product vision, priorities, and the official roadmap.
*   Defines the scope of V1 vs. V2.
*   Final authority on functional decisions and business requirements.

#### Technical Architect (TA)
*   Guardian of the system's architecture, technical quality, and standards.
*   Responsible for technical reviews, audits, and Big Sprint planning.
*   Oversees AWS infrastructure, production readiness, and CI/CD strategy.

#### Senior Software Engineer (Implementation)
*   Responsible for implementation, testing, bug fixes, and technical validation.
*   Must preserve the existing architecture and follow established specifications.
*   **Constraints:** Does not redefine roadmaps, does not alter product priorities, does not modify architecture without approval, and does not propose strategic changes unless requested.

### Official Big Sprint Workflow
The following sequence is mandatory for every Big Sprint:
1.  **Architecture Review Gate**: Technical feasibility and integrity check.
2.  **Product Readiness Gate**: Functional alignment and V1 scope check.
3.  **Sprint Planning**: Detailed task breakdown and estimation.
4.  **Implementation**: Coding, unit testing, and integration testing.
5.  **Architecture Audit**: Post-implementation check for pattern compliance.
6.  **Validation**: Functional testing and acceptance criteria verification.
7.  **Git / CI/CD**: Merging and automated deployment pipeline.
8.  **Documentation**: Updating ADRs, Swagger, and technical docs.

### Architecture Review Gate (Mandatory)
Before starting, the implementation must answer:
1.  Is the current domain truly prepared to receive this Sprint?
2.  Is there any technical debt that must be resolved before starting this Sprint?
3.  Is there any risk of breaking existing functionality during this Sprint?
4.  What is the validation, testing, and acceptance plan to close this Sprint safely?

### Product Readiness Gate (Mandatory)
Before starting, the implementation must answer:
1.  Do current entities fully support the V1 of Circulou?
2.  Are there any entities that will likely need remodeling before V1 completion?
3.  Are there any major business rules missing that impact user experience?
4.  Will planned V1 features require structural database changes?
5.  Will any endpoint contracts likely change during upcoming Sprints?
6.  Are there incomplete functional flows for Client, Driver, or Merchant roles?
7.  Are there any functional decisions needed now to avoid future rework?
8.  Should any planned feature be deferred to V2 to prioritize V1 value?
9.  Does the current architecture support V1 without major remodeling?
10. Will the product be objectively closer to a controlled pilot after this Sprint?

### Prioritization Categories
Every future implementation must be classified:
*   **Category A**: Mandatory for V1.
*   **Category B**: Important for V1.
*   **Category C**: Deferred to V2.
*   **Category D**: Technical Improvement (Purely technical).

### Engineering Principles
Decisions must prioritize:
*   Simplicity, stability, and scalability.
*   Minimal rework and focus on V1 delivery.
*   Production and AWS readiness.
*   Preservation of DDD, SOLID, and Hexagonal Architecture.

## Consequences
*   **Predictability**: Clearer timelines and deliverables.
*   **Stability**: Reduced risk of regressions and architectural degradation.
*   **Onboarding**: Faster integration of new developers through standardized processes.
*   **Consistency**: A unified path towards the Sprint 50 Release Candidate V1.
