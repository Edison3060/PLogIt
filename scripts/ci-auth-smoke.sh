#!/usr/bin/env bash
set -euo pipefail

base="http://127.0.0.1:8080/api"
scratch="$(mktemp -d)"
trap 'rm -rf "$scratch"' EXIT
jar="$scratch/cookies.txt"
user_email="ci-auth-$(date +%s)-$$@invalid.test"
password="SyntheticSession05Password123"

curl -fsS --max-time 10 -c "$jar" -b "$jar" "$base/health" >"$scratch/health.json"
csrf="$(awk '$6 == "XSRF-TOKEN" {token=$7} END {print token}' "$jar")"
test -n "$csrf"

register_status="$(curl -sS --max-time 10 -o "$scratch/register.json" -w '%{http_code}' \
    -c "$jar" -b "$jar" -H "Content-Type: application/json" -H "X-XSRF-TOKEN: $csrf" \
    -d "{\"email\":\"$user_email\",\"password\":\"$password\",\"displayName\":\"CI Auth Probe\"}" \
    "$base/auth/register")"
test "$register_status" = 202

login_status="$(curl -sS --max-time 10 -o "$scratch/login.json" -w '%{http_code}' \
    -c "$jar" -b "$jar" -H "Content-Type: application/json" -H "X-XSRF-TOKEN: $csrf" \
    -d "{\"email\":\"$user_email\",\"password\":\"$password\"}" \
    "$base/auth/login")"
test "$login_status" = 200
python3 -c 'import json,sys; assert json.load(open(sys.argv[1]))["email"] == sys.argv[2]' "$scratch/login.json" "$user_email"

session_id="$(awk '$6 == "JSESSIONID" {value=$7} END {print value}' "$jar")"
test -n "$session_id"
curl -fsS --max-time 10 -c "$jar" -b "$jar" "$base/auth/me" >"$scratch/me.json"
python3 -c 'import json,sys; assert json.load(open(sys.argv[1]))["email"] == sys.argv[2]' "$scratch/me.json" "$user_email"

engagement_status="$(curl -sS --max-time 10 -o "$scratch/engagement.json" -w '%{http_code}' \
    -c "$jar" -b "$jar" -H "Content-Type: application/json" -H "X-XSRF-TOKEN: $csrf" \
    -d '{"name":"CI Session 05","description":"CSRF protected synthetic engagement"}' \
    "$base/engagements")"
test "$engagement_status" = 200
python3 -c 'import json,sys; assert json.load(open(sys.argv[1]))["name"] == "CI Session 05"' "$scratch/engagement.json"

list_before="$(curl -fsS --max-time 10 -c "$jar" -b "$jar" "$base/engagements" | python3 -c 'import json,sys; print(len(json.load(sys.stdin)))')"
for token_mode in missing invalid; do
    csrf_args=()
    if [ "$token_mode" = invalid ]; then
        csrf_args=(-H "X-XSRF-TOKEN: definitely-invalid-token")
    fi
    denied_status="$(curl -sS --max-time 10 -o "$scratch/denied-$token_mode.json" -w '%{http_code}' \
        -c "$jar" -b "$jar" -H "Content-Type: application/json" "${csrf_args[@]}" \
        -d '{"name":"Should Never Persist","description":"CSRF bypass attempt"}' \
        "$base/engagements")"
    test "$denied_status" = 403
done
list_after="$(curl -fsS --max-time 10 -c "$jar" -b "$jar" "$base/engagements" | python3 -c 'import json,sys; print(len(json.load(sys.stdin)))')"
test "$list_before" = "$list_after"

denied_logout_status="$(curl -sS --max-time 10 -o "$scratch/denied-logout.json" -w '%{http_code}' \
    -c "$jar" -b "$jar" -X POST "$base/auth/logout")"
test "$denied_logout_status" = 403
curl -fsS --max-time 10 -c "$jar" -b "$jar" "$base/auth/me" >"$scratch/still-authenticated.json"

logout_status="$(curl -sS --max-time 10 -o "$scratch/logout.json" -w '%{http_code}' \
    -c "$jar" -b "$jar" -H "X-XSRF-TOKEN: $csrf" -X POST "$base/auth/logout")"
test "$logout_status" = 200
replay_status="$(curl -sS --max-time 10 -o "$scratch/replay.json" -w '%{http_code}' \
    -H "Cookie: JSESSIONID=$session_id" "$base/auth/me")"
test "$replay_status" = 403

printf 'Real HTTP registration/login/authenticated me/logout/old-session replay smoke passed.\n'
