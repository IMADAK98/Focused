#!/usr/bin/env bash
# Idempotent dependency + database setup for the Focused (Spring AI) service.
# Runs after checkout. Must terminate; no long-running processes here.
set -euo pipefail

cd "$(dirname "$0")/.."

# --- PostgreSQL (server + client) ---------------------------------------
if ! command -v pg_ctlcluster >/dev/null 2>&1; then
  sudo apt-get update -qq
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq postgresql postgresql-contrib
fi

# Trust local connections so the app's postgres user (empty password, matching
# application.properties defaults) can connect over TCP without credentials.
# ponytail: dev-only trust auth; a real deployment would set a password secret.
for HBA in /etc/postgresql/*/main/pg_hba.conf; do
  sudo sed -i -E 's/^(host\s+all\s+all\s+(127\.0\.0\.1\/32|::1\/128)\s+)(md5|scram-sha-256|peer|ident)\s*$/\1trust/' "$HBA"
done

# --- Java toolchain -------------------------------------------------------
# Java 21 ships in the base image; fail loudly if it is ever missing.
java -version 2>&1 | head -1

# --- Build (downloads dependencies + compiles) ---------------------------
chmod +x ./mvnw
./mvnw -B -ntp -DskipTests clean package
