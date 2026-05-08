---
allowed-tools: Bash(cat:*), Bash(ls:*)
description: Break the approved design into atomic implementation tasks
---

## Current Spec

!`cat spec/.current-spec 2>/dev/null || echo "No active spec."`

## Approved Design

!`cat spec/$(cat spec/.current-spec 2>/dev/null)/design.md 2>/dev/null`

## Your Task

Generate a `tasks.md` for this spec in `spec/$(cat spec/.current-spec)/tasks.md`.

Rules for task breakdown:

1. **Atomic** — each task is one logical unit of work (one class, one migration, one test suite)
2. **Layered order** — Data layer → Service layer → Controller layer → Tests
3. **Dependencies explicit** — note which tasks must complete before others
4. **Test tasks included** — unit test task for every service, integration test for every controller endpoint
5. **Checkbox format** — use `- [ ]` for each task so progress is trackable

Group tasks by phase:
- Phase 1: Data Layer (entities, migrations, repositories)
- Phase 2: Service Layer (business logic, calculations)
- Phase 3: Controller Layer (REST endpoints, DTOs)
- Phase 4: Validation (validators, annotations)
- Phase 5: Tests (unit, integration, edge cases)

After generating, show a summary: "X total tasks across Y phases. Ready to implement?"
