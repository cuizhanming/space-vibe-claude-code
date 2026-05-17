# CLAUDE.md — image-to-3d-printer

Static web app that converts 2D images into 3D printable STL files.
Runs entirely in the browser — no backend, no build step.

## Session Start

Read `QUICKSTART.md` first, then serve and verify:
```bash
./serve.sh   # or: python -m http.server 8000
```

## How It Works

1. User uploads image → grayscale height map
2. Brightness → height: light = raised, dark = recessed
3. Triangulated mesh generated client-side (Three.js)
4. ASCII STL exported for any slicer (Cura, PrusaSlicer, etc.)

## Parameters

| Param | Range | Default |
|---|---|---|
| Height scale | 1–50 mm | 10 mm |
| Base thickness | 1–10 mm | 2 mm |
| Model width | 10–200 mm | 100 mm |
| Resolution | 64–512 px | 128 px |

## Architecture

```
index.html      # Single-page app
js/             # Height map → STL conversion logic (Three.js)
css/            # Responsive UI styles
serve.sh        # Quick local dev server
```

## Coding Rules

- No frameworks, no bundler — vanilla JS + Three.js from CDN
- All processing must stay client-side (no server uploads)
- STL export must produce valid ASCII STL (test with PrusaSlicer)
- Mobile-responsive: test at 375px viewport width

## Quality Gate

Open in Chrome and:
1. Upload a test PNG → 3D preview renders
2. Download STL → file is valid (non-zero size, readable by slicer)
