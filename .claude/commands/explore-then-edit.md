---
allowed-tools: Bash(cat:*), Bash(ls:*), Bash(grep:*), Bash(find:*)
description: Explore a subsystem with a read-only subagent, write findings, then edit
argument-hint: <subsystem-path-or-description>
---

## Subsystem to Explore

**Target:** $ARGUMENTS

## Phase 1 — Explore (read-only)

Your job right now is **mapping only**. No edits.

1. `ls -R $ARGUMENTS` to get the full file tree
2. Read entry points (index files, main classes, routing files)
3. Follow the top 3–5 most important call chains
4. Note: public API surface, key data models, existing tests, anything missing

Write your findings to `specs/exploration-$(date +%Y%m%d).md`:

```markdown
# Exploration: $ARGUMENTS

## Entry Points
...

## Call Chains
...

## Data Models
...

## Tests Coverage
...

## Gaps / Red Flags
...

## Proposed Approach
...
```

## Phase 2 — Review

Present the findings summary to the user.
Ask: "Does this analysis look correct? Any corrections before I start editing?"

Wait for approval.

## Phase 3 — Edit

Only after approval: implement what was discussed.
One task at a time. Compile/test after each change.
