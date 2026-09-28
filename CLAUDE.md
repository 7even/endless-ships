# Endless Sky reference site generator

Parses the game's `data/*.txt` files (the game is a git submodule at
`resources/game`) and generates a static site into `build/`.

## Project Structure

- **Generator** (`src/clj`): `parser` (instaparse over `resources/parser.bnf`) →
  `ships` / `outfits` / `outfitters` / `images` → `core` builds `data.edn` + the site
- **Frontend** (`src/cljs`): ClojureScript + Re-frame + Reagent, built by shadow-cljs
- **Images**: not copied into the build — `images.clj` resolves sprite names to file
  paths the way the game does, and the frontend links them on raw.githubusercontent.com
  at the pinned game commit

## Essential Commands

- **Build the site**: `clojure -X:clj:build` (~2 minutes, output in `build/`). The heap
  limit lives in the `:build` alias in `deps.edn` — parsing needs more than 2 GB
- **Frontend dev server**: `shadow-cljs watch main` (port 3000, serves `public/`)
- **Generate data for frontend dev**: `(spit "public/data.edn" endless-ships.core/edn)`

## Tool Preferences

- **REPL eval**: `bin/eval "CODE"`
  - Always use double quotes around the code argument
  - The Bash tool runs non-interactive bash where `!` is not special, so use `!`
    directly without escaping (e.g. `"(reset! atom true)"`). In contrast, the user's
    interactive zsh requires `\!` to escape history expansion
  - Claude Code collapses tool calls, making code and results hard to read. Always
    show the Clojure code as a formatted code block before running it, and show the
    result as a formatted code block after
  - `bin/eval` reads the port from `.nrepl-port`, which CIDER jack-in writes. If there
    is no REPL, start one: `clojure -J-Xmx4g -Sdeps '{:deps {nrepl/nrepl {:mvn/version
    "1.3.1"}}}' -M:clj -m nrepl.cmdline --port 7888` (it writes `.nrepl-port` itself).
    The `-Xmx` flag is needed because the `:build` alias, which carries it in `deps.edn`,
    is not in play here
- **Keep the REPL warm**: `(require 'endless-ships.parser)` parses every game file and
  takes ~65 seconds. Load it once and query `endless-ships.parser/data` afterwards
  instead of running throwaway `clojure -M` scripts
- **Querying game data**: `endless-ships.parser/data` is a seq of `[type [args] attrs]`
  entries covering everything in the game — systems, planets, missions, governments —
  not just what ends up in `data.edn`. The source file is in `attrs` under `"file"`

## Viewing the Built Site

The site is a SPA with client-side routing, so a plain static server returns 404 on
routes like `/ships/...`. Serve it with a fallback to `index.html`, mirroring
`try_files` in `docker/nginx.conf`.

## Game Submodule

`resources/game` is pinned to a release tag. A `git pull` only updates the recorded
pointer — run `git submodule update` afterwards to move the working tree. Note that
release tags are lightweight, so plain `git describe` (and `git submodule status`)
reports a stale older tag; use `git describe --tags`.
