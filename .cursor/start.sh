#!/usr/bin/env bash
# Per-boot reconciliation: start PostgreSQL and ensure the app database exists.
# Must be idempotent, avoid duplicate processes, check readiness, then return.
set -euo pipefail

CLUSTER_VERSION="$(ls /etc/postgresql 2>/dev/null | sort -n | tail -1)"

# Start the cluster if it is not already running.
sudo pg_ctlcluster "${CLUSTER_VERSION}" main start 2>/dev/null || true

# Wait for readiness before touching the database.
for _ in $(seq 1 30); do
  if pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    break
  fi
  sleep 1
done
pg_isready -h localhost -p 5432

# Create the app database if it does not exist (name matches
# spring.datasource.url in application.properties).
if ! psql "postgresql://postgres@localhost:5432/postgres" -tAc \
    "SELECT 1 FROM pg_database WHERE datname='spring-ai'" | grep -q 1; then
  psql "postgresql://postgres@localhost:5432/postgres" -c 'CREATE DATABASE "spring-ai"'
fi

echo "PostgreSQL ready; database 'spring-ai' present."
