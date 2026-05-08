---
allowed-tools: Bash(cat:*), Bash(ls:*)
description: Generate or review requirements for the current spec using EARS format
---

## Current Spec

!`cat spec/.current-spec 2>/dev/null || echo "No active spec. Run /spec:new <feature-name> first."`

## Requirements File

!`cat spec/$(cat spec/.current-spec 2>/dev/null)/requirements.md 2>/dev/null || echo "No requirements.md found for current spec."`

## Your Task

Review the requirements above (or create them if missing). Ensure:

1. **EARS format** — every requirement uses WHEN/IF/THEN/AND patterns
2. **Testable** — each requirement maps to a concrete acceptance criterion
3. **Complete** — covers happy path, error cases, edge cases
4. **Non-functional** — performance, security, compliance requirements are listed
5. **Scoped** — "Out of Scope" section exists and is realistic

Ask the user: "Are these requirements complete? Approve to proceed to design phase, or tell me what to change."

Do NOT proceed to design until the user explicitly approves.
