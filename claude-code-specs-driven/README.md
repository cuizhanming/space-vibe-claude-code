# Irish Payslips Management System

> A **specs-driven vibe coding template** demonstrating how to build production-grade Spring Boot applications with Claude Code using structured specifications.

[![Build & Test](https://github.com/cuizhanming/space-vibe-claude-code/actions/workflows/build.yml/badge.svg)](https://github.com/cuizhanming/space-vibe-claude-code/actions)

---

## What Is This?

This is a **template project** — not just a payroll system. The *Irish Payslips Management System* is the example domain, but the real value is the workflow:

> **Write specs first → get Claude Code to approve them → then implement**

This prevents the most common vibe coding failure modes:
- ❌ Scope creep mid-session
- ❌ Architectural drift (Claude ignores your structure)
- ❌ Half-baked implementations (missing error cases, no tests)
- ❌ Context window bloat causing degraded code quality

---

## The 4-Phase Workflow

```
💡 Idea → 📋 Requirements → 🏗️ Design → ✅ Tasks → ⚙️ Implementation
```

Each phase is documented in `spec/[ID]/` and must be **approved before proceeding**.

### Phase 1: Requirements (`requirements.md`)
Define WHAT needs to be built using **EARS format** (WHEN/IF/THEN/AND).
Every requirement maps to a testable acceptance criterion.

### Phase 2: Design (`design.md`)
Define HOW it will be built with:
- Mermaid component architecture diagrams
- Mermaid sequence diagrams for data flow
- SQL schema for new tables
- API endpoint contracts

### Phase 3: Tasks (`tasks.md`)
Break the design into **atomic tasks** ordered by dependency.
Each task is a single class, migration, or test suite — completable in one go.

### Phase 4: Implementation
Execute tasks in order. Compile after each. Commit after each phase.

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker (for PostgreSQL via Testcontainers)

### Run Locally

```bash
git clone https://github.com/cuizhanming/space-vibe-claude-code
cd space-vibe-claude-code/claude-code-specs-driven

# Run with in-memory H2 (dev profile)
mvn spring-boot:run

# API docs at:
# http://localhost:8080/swagger-ui.html
```

### Run Tests

```bash
mvn test              # Unit tests
mvn verify            # Integration tests (requires Docker for Testcontainers)
mvn jacoco:report     # Coverage report → target/site/jacoco/index.html
```

---

## Using This as a Template With Claude Code

### Start a New Feature

```bash
# In Claude Code, run:
/spec:new employee-self-service
```

Then follow the prompts through requirements → design → tasks → implement.

### Resume a Session

```bash
# Tell Claude Code:
"Read spec/.current-spec and tasks.md. Where did we leave off?"
```

### Browse the Prompt Library

See `PROMPTS.md` for 20+ proven prompts for every stage of development.

---

## Project Structure

```
claude-code-specs-driven/
├── .ai-rules/               # AI context files (always read by Claude Code)
│   ├── product.md           # Product vision, features, target users
│   ├── tech.md              # Technology stack + dependency map (Mermaid)
│   └── structure.md         # Architecture + request flow (Mermaid)
├── .claude/
│   └── commands/spec/       # Custom slash commands
│       ├── new.md           # /spec:new <feature>
│       ├── requirements.md  # /spec:requirements
│       ├── design.md        # /spec:design
│       ├── tasks.md         # /spec:tasks
│       └── implement.md     # /spec:implement
├── spec/                    # Living feature specifications
│   ├── .current-spec        # Tracks active spec
│   ├── 001-employee-management/
│   │   ├── requirements.md  # ✅ Approved
│   │   ├── design.md        # ✅ Approved
│   │   └── tasks.md         # 🔄 In Progress
│   └── 002-payroll-processing/
│       └── requirements.md  # 📝 Draft
├── src/                     # Spring Boot application
│   └── main/java/com/irish/payroll/
│       ├── controller/      # REST endpoints
│       ├── service/         # Business logic
│       │   └── tax/         # PAYE, PRSI, USC calculators
│       ├── repository/      # Data access
│       ├── entity/          # JPA entities
│       └── security/        # JWT auth
├── .github/workflows/
│   └── build.yml            # CI: compile + test + coverage
├── CLAUDE.md                # Claude Code session instructions
├── DECISIONS.md             # Architectural Decision Records (ADRs)
└── PROMPTS.md               # Starter prompts library
```

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.2 / Java 17 |
| Security | Spring Security 6 + JWT |
| Database | PostgreSQL 15 (prod), H2 (dev) |
| Migrations | Liquibase |
| ORM | Spring Data JPA + Hibernate 6 |
| Mapping | MapStruct |
| PDF | iText 8 |
| Excel | Apache POI |
| Testing | JUnit 5, Mockito, Testcontainers |
| Docs | OpenAPI 3 / Swagger UI |

---

## Key Concepts Demonstrated

- **EARS requirements format** — eliminates ambiguity in specs
- **Mermaid diagrams in `.ai-rules/`** — better AI comprehension than prose
- **Custom `/spec:*` slash commands** — structured workflow enforced by tooling
- **4-phase approval gates** — prevents Claude from over-building or going off-spec
- **Context window discipline** — commit checkpoints + compact guidance in CLAUDE.md
- **Architectural Decision Records** — log *why* choices were made, not just what

---

## Related Templates in This Repo

- [`claude-code-frontend-figma`](../claude-code-frontend-figma) — Figma → React frontend workflow
- [`claude-code-sub-agents`](../claude-code-sub-agents) — Multi-agent orchestration patterns
- [`flutter-payroll-scanner`](../flutter-payroll-scanner) — Flutter mobile with Claude Code

---

## License

MIT
