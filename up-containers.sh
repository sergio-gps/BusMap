#!/usr/bin/env bash
set -euo pipefail

# Change to repo root (directory of this script)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Ensure persistent volume folder exists
mkdir -p docker_volume

COMPOSE_CMD=(docker compose)
if ! command -v docker >/dev/null 2>&1; then
  echo "Error: Docker not found in PATH." >&2
  exit 1
fi

echo "Using: ${COMPOSE_CMD[*]}"
"${COMPOSE_CMD[@]}" up -d --build

# Show status
"${COMPOSE_CMD[@]}" ps
