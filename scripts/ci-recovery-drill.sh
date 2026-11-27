#!/usr/bin/env bash
set -euo pipefail

if [[ "${GITHUB_ACTIONS:-}" != true || "${COMPOSE_PROJECT_NAME:-}" != plogit-ci-* ]]; then
    printf 'This drill requires the disposable CI Compose project.\n' >&2
    exit 1
fi
test "$(docker inspect -f '{{index .Config.Labels "com.docker.compose.project"}}' plogit-backend)" = "$COMPOSE_PROJECT_NAME"
test "$(docker inspect -f '{{index .Config.Labels "com.docker.compose.project"}}' plogit-db)" = "$COMPOSE_PROJECT_NAME"

umask 077
scratch="$(mktemp -d)"
trap 'rm -rf -- "$scratch"' EXIT
base=http://127.0.0.1:8080/api
jar="$scratch/cookies"
restored_db=plogit_restore_drill

login() {
    curl -fsS --max-time 10 -c "$jar" "$base/health" > /dev/null
    csrf="$(awk '$6 == "XSRF-TOKEN" {token=$7} END {print token}' "$jar")"
    test -n "$csrf"
    python3 -c 'import json,os; print(json.dumps({"email": os.environ["SEED_USER_EMAIL"], "password": os.environ["SEED_USER_PASSWORD"]}))' |
        curl -fsS --max-time 10 -b "$jar" -c "$jar" -H "X-XSRF-TOKEN: $csrf" \
            -H 'Content-Type: application/json' --data-binary @- "$base/auth/login" > /dev/null
}

pg() {
    local database="$1"
    shift
    docker compose exec -T db psql -X -U "$POSTGRES_USER" -d "$database" -v ON_ERROR_STOP=1 "$@"
}

manifest() {
    pg "$1" -At -v engagement="$engagement" -v log="$log" <<'SQL'
SELECT jsonb_build_object(
    'engagements', (SELECT jsonb_agg(to_jsonb(e)) FROM engagements e WHERE id = :'engagement'),
    'logs', (SELECT jsonb_agg(to_jsonb(l)) FROM logs l WHERE engagement_id = :'engagement'),
    'versions', (SELECT jsonb_agg(to_jsonb(v) ORDER BY version_number) FROM log_versions v WHERE log_id = :'log'),
    'attachments', (SELECT jsonb_agg(to_jsonb(a)) FROM attachments a WHERE log_id = :'log'),
    'members', (SELECT jsonb_agg(to_jsonb(m) ORDER BY id) FROM engagement_members m WHERE engagement_id = :'engagement'),
    'audit', (SELECT jsonb_agg(to_jsonb(a) ORDER BY id) FROM audit_log a),
    'migrations', (SELECT jsonb_agg(jsonb_build_array(version, checksum, success) ORDER BY installed_rank)
                   FROM flyway_schema_history)
);
SQL
}

check_media() {
    local root="$1"
    if [[ ! -f "$root/$storage_path" ]]; then
        printf 'Restore rejected: attachment bytes missing.\n' >&2
        return 1
    fi
    if ! cmp -s "$scratch/image.png" "$root/$storage_path"; then
        printf 'Restore rejected: attachment bytes differ.\n' >&2
        return 1
    fi
}

login
write_json() {
    curl -fsS --max-time 10 -b "$jar" -H "X-XSRF-TOKEN: $csrf" \
        -H 'Content-Type: application/json' "$@"
}

engagement="$(write_json -d '{"name":"Recovery drill","description":"Synthetic evidence"}' "$base/engagements" |
    python3 -c 'import json,sys; print(int(json.load(sys.stdin)["id"]))')"
log="$(write_json -d '{"activityType":"RECONNAISSANCE","title":"Recovery drill","description":"Initial version","result":"Synthetic","outcome":"IN_PROGRESS"}' "$base/engagements/$engagement/logs" |
    python3 -c 'import json,sys,uuid; print(uuid.UUID(json.load(sys.stdin)["id"]))')"
write_json -X PUT -d '{"description":"Updated version"}' "$base/logs/$log" > /dev/null
printf '%s' 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+Zk9sAAAAASUVORK5CYII=' |
    base64 -d > "$scratch/image.png"
attachment="$(curl -fsS --max-time 10 -b "$jar" -H "X-XSRF-TOKEN: $csrf" \
    -F "file=@$scratch/image.png;type=image/png" "$base/logs/$log/attachments" |
    python3 -c 'import json,sys; print(int(json.load(sys.stdin)["id"]))')"
media_url="$base/logs/$log/attachments/$attachment"
curl -fsS --max-time 10 -b "$jar" "$media_url" -o "$scratch/http-image.png"
cmp "$scratch/image.png" "$scratch/http-image.png"

# The backend is the only application writer in the disposable stack.
docker compose stop backend
manifest "$POSTGRES_DB" > "$scratch/source.json"
storage_path="$(pg "$POSTGRES_DB" -At -v attachment="$attachment" <<'SQL'
SELECT storage_path FROM attachments WHERE id = :'attachment';
SQL
)"
[[ "$storage_path" == "$engagement/$log/"* && "${storage_path##*/}" == *-image.png ]]
docker compose exec -T db pg_dump -U "$POSTGRES_USER" -Fc "$POSTGRES_DB" > "$scratch/database.dump"
mkdir "$scratch/captured" "$scratch/restored" "$scratch/missing"
docker cp plogit-backend:/data/attachments/. "$scratch/captured"
tar -cf "$scratch/media.tar" -C "$scratch/captured" .
(cd "$scratch" && sha256sum database.dump media.tar > SHA256SUMS && sha256sum -c SHA256SUMS)

docker compose exec -T db createdb -U "$POSTGRES_USER" "$restored_db"
docker compose exec -T db pg_restore --exit-on-error --single-transaction -U "$POSTGRES_USER" \
    -d "$restored_db" < "$scratch/database.dump"
tar -xf "$scratch/media.tar" -C "$scratch/restored"
manifest "$restored_db" > "$scratch/restored.json"
cmp "$scratch/source.json" "$scratch/restored.json"
check_media "$scratch/restored"
python3 - "$scratch/restored.json" <<'PY'
import json
import sys

with open(sys.argv[1]) as file:
    fixture = json.load(file)
assert len(fixture["engagements"]) == len(fixture["logs"]) == len(fixture["attachments"]) == len(fixture["members"]) == 1
assert [row["version_number"] for row in fixture["versions"]] == [1, 2]
assert fixture["audit"] and all(row[2] for row in fixture["migrations"])
print("Recovered engagement=1 log=1 versions=2 attachment=1 membership=1 and preserved audit/Flyway records.")
PY
sha256sum "$scratch/restored/$storage_path" | cut -d ' ' -f 1
if check_media "$scratch/missing" 2>"$scratch/missing.log"; then
    printf 'A restore without its media volume passed verification.\n' >&2
    exit 1
fi
grep -Fx 'Restore rejected: attachment bytes missing.' "$scratch/missing.log"
manifest "$restored_db" > "$scratch/after-denial.json"
cmp "$scratch/restored.json" "$scratch/after-denial.json"
printf 'Missing-media denial left restored database records unchanged.\n'

docker compose --profile app up --detach --wait --wait-timeout 120 backend
