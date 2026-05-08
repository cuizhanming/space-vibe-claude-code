---
allowed-tools: Bash(cat:*), Bash(ls:*), Bash(echo:*)
description: Generate technical design with Mermaid diagrams for the current spec
---

## Current Spec

!`cat spec/.current-spec 2>/dev/null || echo "No active spec."`

## Approved Requirements

!`cat spec/$(cat spec/.current-spec 2>/dev/null)/requirements.md 2>/dev/null`

## Your Task

Generate a `design.md` for this spec in `spec/$(cat spec/.current-spec)/design.md`.

The design MUST include:

1. **Component Architecture** — Mermaid `graph TD` showing which classes/services/repos are involved and how they connect

2. **API Design** — Table or list of all new/modified endpoints:
   ```
   METHOD /path → description
   ```

3. **Data Flow** — Mermaid `sequenceDiagram` showing the happy-path request/response flow through layers

4. **Database Schema** — SQL DDL for any new tables or migrations needed

5. **Key Design Decisions** — Numbered list of significant choices made (and why alternatives were rejected)

After generating, ask: "Does this design look correct? Approve to generate task breakdown."

Do NOT generate tasks until user approves.
