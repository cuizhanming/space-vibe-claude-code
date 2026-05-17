---
allowed-tools: Bash(cat:*), Bash(ls:*), Bash(date:*)
description: Review stop-hook suggestions and apply approved improvements to CLAUDE.md files
---

## Pending Suggestions

!`cat CLAUDE.md.suggestions 2>/dev/null || echo "No suggestions yet. CLAUDE.md.suggestions doesn't exist."`

## Your Task

1. Read each suggestion block above
2. For each concrete improvement identified:
   - Show the user the proposed patch (old text → new text, file name)
   - Ask: "Apply this change? (yes / skip)"
3. After user approves, apply changes with the Edit tool
4. After all changes are applied, clear the processed sections from `CLAUDE.md.suggestions`
5. Confirm: "CLAUDE.md files updated. Here's what changed: [summary]"

**Do NOT modify code files.** Only CLAUDE.md files are in scope.
