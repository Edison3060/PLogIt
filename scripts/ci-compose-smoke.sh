#!/usr/bin/env bash
set -euo pipefail

docker compose --profile app config --quiet
docker compose --profile app config --format json | python3 -c '
import json, sys
services = json.load(sys.stdin)["services"]
assert services["db"]["healthcheck"]["test"]
for service, target in (("db", "/var/lib/postgresql/data"), ("backend", "/data/attachments")):
    assert any(volume.get("type") == "volume" and volume.get("target") == target
               for volume in services[service]["volumes"]), service
'

docker compose --profile app up --detach --build --wait --wait-timeout 240
docker compose --profile app ps

for service in plogit-db plogit-backend plogit-frontend; do
    status="$(docker inspect -f '{{.State.Health.Status}}' "$service")"
    test "$status" = healthy
done

health="$(curl -fsS --max-time 10 --retry 10 --retry-delay 2 --retry-connrefused http://127.0.0.1:8080/api/health)"
python3 -c 'import json,sys; result=json.loads(sys.argv[1]); assert result["status"] == "UP" and result["app"] == "plogit"' "$health"
curl -fsS --max-time 10 --retry 10 --retry-delay 2 --retry-connrefused http://127.0.0.1:5173/ | grep -Fq '<div id="root"></div>'

docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1 \
    -c 'CREATE TABLE ci_persistence_probe (id INTEGER PRIMARY KEY); INSERT INTO ci_persistence_probe(id) VALUES(1);'
docker compose stop db
docker compose up --detach --wait --wait-timeout 90 db
persisted="$(docker compose exec -T db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Atc 'SELECT count(*) FROM ci_persistence_probe WHERE id=1')"
test "$persisted" = 1
test "$(docker inspect -f '{{.State.Health.Status}}' plogit-db)" = healthy
curl -fsS --max-time 10 --retry 10 --retry-delay 2 --retry-connrefused http://127.0.0.1:8080/api/health | grep -Fq '"UP"'
printf 'Compose PostgreSQL, backend/frontend health, API, SPA and volume-restart smoke passed.\n'
