#!/usr/bin/env bash
# Falla si el directorio publicado no tiene index.html o contiene algo
# distinto de .html, .css y .js. Lo usan CI (job site) y CD (job pages).
set -euo pipefail

cd "${1:-site}"
test -f index.html
extras=$(find . -type f ! -name '*.html' ! -name '*.css' ! -name '*.js')
if [ -n "$extras" ]; then
  echo "Archivos que no deben publicarse:"
  echo "$extras"
  exit 1
fi
