---
allowed-tools: Bash(mkdir:*), Bash(echo:*), Bash(date:*), Bash(ls:*), Bash(cat:*)
description: Create a new feature specification directory
argument-hint: <feature-name>
---

## Current Spec Status

!`ls spec/ 2>/dev/null | grep -v '^\.' | wc -l | xargs -I {} echo "Total specs: {}"`

## Your Task

Create a new specification for: **$ARGUMENTS**

1. Determine the next ID (format: 001, 002, 003...)
2. Create `spec/[ID]-$ARGUMENTS/` directory
3. Update `spec/.current-spec` with `[ID]-$ARGUMENTS`
4. Create `spec/[ID]-$ARGUMENTS/requirements.md` using the EARS template below
5. Confirm creation and tell the user to run `/spec:requirements` next

### Requirements Template to Use

```markdown
# Spec [ID]: [Feature Name] — Requirements

**Status**: 📝 Draft
**Created**: [today's date]
**Phase**: Requirements → Design → Tasks → Implementation

---

## Feature Summary

[One paragraph describing what this feature does and why]

---

## Requirements (EARS Format)

### Functional Requirements

**WHEN** [trigger event]
**THEN** the system SHALL [expected behaviour]
**AND** [additional constraint]

**IF** [precondition]
**THEN** the system SHALL [behaviour]

### Non-Functional Requirements

- [Performance, security, compliance requirements]

---

## Acceptance Criteria

- [ ] [Testable criterion 1]
- [ ] [Testable criterion 2]

---

## Out of Scope

- [What this spec deliberately does NOT cover]
```
