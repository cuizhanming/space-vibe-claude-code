# CLAUDE.md — Workspace Root

> **Rule:** This file stays lean. Big-picture only.
> Each sub-project has its own CLAUDE.md with full detail.

## What Lives Here

| Project | Language / Framework | CLAUDE.md |
|---|---|---|
| `claude-code-specs-driven/` | Spring Boot 3.2 / Java 17 | [link](claude-code-specs-driven/CLAUDE.md) |
| `claude-code-frontend-figma/` | Next.js 15 / React 19 / TS | [link](claude-code-frontend-figma/CLAUDE.md) |
| `claude-code-sub-agents/` | Vanilla JS / HTML | [link](claude-code-sub-agents/CLAUDE.md) |
| `flutter-payroll-scanner/` | Flutter 3.2 / Dart | [link](flutter-payroll-scanner/CLAUDE.md) |
| `image-to-3d-printer/` | Static HTML / JS | [link](image-to-3d-printer/CLAUDE.md) |

## Session Start Protocol

1. Read the sub-project CLAUDE.md for the area you're working in.
2. For spec-driven projects, run: `cat spec/.current-spec`
3. State what you found and what you'll do next before writing a single line of code.

## Cross-Cutting Conventions

- **No implementation without a spec.** Specs live in `spec/` directories.
- **Commit after every phase** — never leave broken state.
- **One task at a time.** Complete → verify → commit → next.
- **Context fills fast.** Read files, don't paste them. Use `/compact` before 50+ messages.

## MCP Configuration

- Figma MCP server: `framelink-figma-mcp` (requires `FIGMA_API_KEY`)
- Configured in `.claude/settings.local.json`

## Self-Improvement Hook

A stop hook fires after every session. It reflects on what was unclear and
proposes CLAUDE.md improvements. Review `CLAUDE.md.suggestions` if it exists.
