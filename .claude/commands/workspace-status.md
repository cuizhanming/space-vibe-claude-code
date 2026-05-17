---
allowed-tools: Bash(cat:*), Bash(ls:*)
description: Show current workspace status — active specs, recent commits, pending suggestions
---

## Active Specs

### specs-driven project
!`cat claude-code-specs-driven/spec/.current-spec 2>/dev/null && echo "" && tail -5 claude-code-specs-driven/spec/$(cat claude-code-specs-driven/spec/.current-spec 2>/dev/null)/tasks.md 2>/dev/null || echo "No active spec"`

## Pending CLAUDE.md Suggestions

!`wc -l CLAUDE.md.suggestions 2>/dev/null | awk '{print $1 " lines pending review"}' || echo "No suggestions file"`

## Recent Git Activity

!`git log --oneline -10 2>/dev/null || echo "Not a git repo or no commits"`

## Sub-project Status

!`for d in claude-code-frontend-figma claude-code-specs-driven claude-code-sub-agents flutter-payroll-scanner image-to-3d-printer; do echo "### $d"; ls "$d" 2>/dev/null | head -5; echo ""; done`

---

Present a concise summary to the user. Flag anything that needs attention.
