---
allowed-tools: Bash(cat:*), Bash(ls:*), Bash(grep:*), Bash(git:*)
description: Begin systematic implementation of approved tasks in the current spec
---

## Current Spec

!`cat spec/.current-spec 2>/dev/null || echo "No active spec."`

## Task List

!`cat spec/$(cat spec/.current-spec 2>/dev/null)/tasks.md 2>/dev/null`

## Implementation Rules

Follow these rules on every task:

1. **One task at a time** — complete and verify before moving to next
2. **Check off tasks** — update `tasks.md` checkbox to `[x]` after each completion
3. **Commit after each phase** — run `git add -A && git commit -m "spec/[ID] phase N: description"`
4. **Validate before continuing** — run `mvn compile` after each task; fix errors before proceeding
5. **Tests must pass** — run `mvn test` before marking a phase complete
6. **Context warning** — if context is getting long (>50 messages), compact and resume

## Starting Point

Find the first unchecked task `- [ ]` in tasks.md and begin there.

Before writing any code, state:
- "Currently implementing: [Task X.X - description]"
- "Expected output: [what file/class will be created/modified]"

After completing all tasks, run `mvn clean verify` and report the final test results.
