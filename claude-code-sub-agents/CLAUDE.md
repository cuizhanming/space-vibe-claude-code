# CLAUDE.md — claude-code-sub-agents

Vanilla JS demo showing Claude Code sub-agents collaboration.
A to-do list app with no build step — just open `index.html`.

## Session Start

Check the test reports first: `cat TEST_REPORT.md | head -30`

## Commands

```bash
python -m http.server 8000   # Serve locally
# or
npx serve .
```

## Architecture

```
index.html          # Entry point
css/
  reset.css
  variables.css     # CSS custom properties
  components.css
  themes.css
js/
  app.js            # All logic (CRUD, localStorage, keyboard a11y)
specs/              # Requirements & design docs
```

## Key Features

- Task CRUD with priority levels
- localStorage persistence
- Dark / light theme
- Keyboard accessible (WCAG 2.1 AA)

## Sub-Agent Pattern Used Here

When implementing new features, prefer the **explore → plan → implement** split:
1. Spin a read-only subagent to map the codebase and write findings to `specs/analysis.md`
2. Review findings with the user
3. Main agent implements based on the analysis

This avoids bloated context and gives clean separation between exploration and editing.

## Coding Rules

- No frameworks, no build tools — raw HTML/CSS/JS only
- CSS: use variables from `variables.css`, never hardcode colours
- JS: ES6+ modules, no global namespace pollution
- Every PR must have a corresponding `specs/` doc update

## Quality Gate

Open `diagnostic.html` in a browser and confirm all checks pass.
