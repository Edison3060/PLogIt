#!/bin/sh
# Run after Caddy has obtained the production certificate.
set -eu

: "${APP_DOMAIN:?Set APP_DOMAIN to the public hostname (without https://)}"
case "$APP_DOMAIN" in
    *[!a-zA-Z0-9.-]* | .* | *.) echo "APP_DOMAIN must be a hostname" >&2; exit 1 ;;
esac

tmpdir="$(mktemp -d)"
trap 'rm -rf "$tmpdir"' EXIT HUP INT TERM

curl -fsS --max-time 20 -D "$tmpdir/headers" -o "$tmpdir/health" "https://$APP_DOMAIN/api/health"
grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"' "$tmpdir/health"

for name in strict-transport-security content-security-policy x-content-type-options x-frame-options referrer-policy; do
    grep -Eiq "^$name:" "$tmpdir/headers"
done
grep -Eiq '^strict-transport-security: max-age=31536000' "$tmpdir/headers"
grep -Eiq '^x-content-type-options: nosniff' "$tmpdir/headers"
grep -Eiq '^content-security-policy: .*script-src ' "$tmpdir/headers"

csrf="$(grep -i '^set-cookie: XSRF-TOKEN=' "$tmpdir/headers")"
printf '%s' "$csrf" | grep -Eiq ';[[:space:]]*secure([;[:space:]]|$)'
printf '%s' "$csrf" | grep -Eiq ';[[:space:]]*samesite=lax([;[:space:]]|$)'
if printf '%s' "$csrf" | grep -Eiq ';[[:space:]]*httponly([;[:space:]]|$)'; then
    echo "XSRF-TOKEN must be readable by the SPA" >&2
    exit 1
fi

curl -fsS --max-time 20 -o "$tmpdir/spa" "https://$APP_DOMAIN/engagements"
grep -q '<div id="root"></div>' "$tmpdir/spa"

curl -sS --max-time 20 -D "$tmpdir/redirect" -o /dev/null "http://$APP_DOMAIN/api/health"
grep -Eiq '^HTTP/[^ ]+ 30[18]' "$tmpdir/redirect"
grep -Fiq "location: https://$APP_DOMAIN/api/health" "$tmpdir/redirect"

echo "HTTPS, /api routing, SPA fallback, cookies, redirects and security headers passed."
