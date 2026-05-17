# CLAUDE.md — claude-code-frontend-figma

Next.js 15 frontend with Figma-to-code integration.

## Session Start

1. Check active spec: `cat ../spec/.current-spec 2>/dev/null || echo "none"`
2. Run `npm run dev` to verify the dev server starts.

## Commands

```bash
npm run dev          # Dev server (Turbopack, port 3000)
npm run build        # Production build
npm run start        # Production server
npm run lint         # ESLint check
cd claude-code-frontend-figma
```

## Tech Stack

- Next.js 15.5.2 App Router
- React 19.1.0
- TypeScript 5 (strict mode, `@/*` path aliases)
- Tailwind CSS 4
- Turbopack

## Architecture

```
app/
  layout.tsx      # Root layout
  page.tsx        # Home page
```

## Figma Integration

- Use MCP tool `framelink-figma-mcp` to pull designs
- Inspect `figma-assets/` for locally exported tokens/SVGs
- Map Figma components → Tailwind classes; avoid hardcoded pixel values

## Coding Rules

- Components: one per file, named exports preferred
- No `any` — use proper TS types
- Tailwind-first styling; CSS modules only for complex animations
- Accessibility: all interactive elements must have accessible labels (WCAG 2.1 AA)

## Quality Gate

```bash
npm run lint && npm run build    # Must pass before marking done
```
