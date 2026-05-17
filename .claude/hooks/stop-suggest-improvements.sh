#!/usr/bin/env bash
# .claude/hooks/stop-suggest-improvements.sh
#
# Stop hook: fires after every Claude Code session.
# Reflects on the session and proposes CLAUDE.md improvements.
# Writes suggestions to CLAUDE.md.suggestions (human reviews and merges).
#
# Claude Code passes the stop event JSON on stdin.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
SUGGESTIONS_FILE="$REPO_ROOT/CLAUDE.md.suggestions"

# Read the stop event from stdin (JSON with transcript summary)
STOP_EVENT=$(cat)

# Append a timestamped suggestion prompt to the suggestions file.
# This gives Claude Code material to propose edits next session.
cat >> "$SUGGESTIONS_FILE" <<EOF

---
## Session ended: $(date -u +"%Y-%m-%d %H:%M UTC")

### What to review

Based on the session that just ended, look at:
- Any commands that failed (wrong flags, missing env vars)?
- Any directory or file paths Claude had to guess?
- Any project-specific conventions that had to be explained during the session?
- Any quality gates that were skipped or unclear?

If yes → propose a patch to the relevant CLAUDE.md file and present it to the user.
If nothing unclear → no action needed.

### Stop event summary
$STOP_EVENT
EOF

echo "stop hook: suggestions appended to $SUGGESTIONS_FILE"
