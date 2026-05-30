# SPDX-FileCopyrightText: 2025 - 2026 Orange SA
# SPDX-License-Identifier: MIT

## =============================================================================
## Stage 1: Build React app and prepare server dependencies
## =============================================================================
FROM node:19-alpine AS builder

LABEL name="selfcare-ui" \
      description="Selfcare UI" \
      maintainer="discobole@orange.com" \
      org.opencontainers.image.licenses="MIT"

WORKDIR /app

# Install client dependencies (cached layer)
COPY package*.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

# Build React app
COPY . .
RUN npm run build

# Install server dependencies
WORKDIR /app/server
RUN --mount=type=cache,target=/root/.npm npm ci --only=production

## =============================================================================
## Stage 2: Production image with Nginx + Node.js + s6-overlay
## =============================================================================
FROM alpine:3.19

ARG S6_OVERLAY_VERSION=3.1.6.2

# Install s6-overlay (multi-arch support)
SHELL ["/bin/ash", "-eo", "pipefail", "-c"]
# hadolint ignore=DL3018,DL3047
RUN ARCH=$(uname -m) && \
    case "${ARCH}" in \
        x86_64)  S6_ARCH=x86_64  ;; \
        aarch64) S6_ARCH=aarch64 ;; \
        armv7l)  S6_ARCH=armhf   ;; \
        *) echo "Unsupported architecture: ${ARCH}" && exit 1 ;; \
    esac && \
    wget -qO- "https://github.com/just-containers/s6-overlay/releases/download/v${S6_OVERLAY_VERSION}/s6-overlay-noarch.tar.xz" | tar -C / -Jxpf - && \
    wget -qO- "https://github.com/just-containers/s6-overlay/releases/download/v${S6_OVERLAY_VERSION}/s6-overlay-${S6_ARCH}.tar.xz" | tar -C / -Jxpf -

# Install runtime dependencies and setup directories
# hadolint ignore=DL3018
RUN apk add --no-cache \
        bash nodejs npm wget tzdata gettext \
        nginx nginx-mod-http-headers-more && \
    # Create non-root user (OpenShift compatible)
    addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    # Setup nginx directories - create ALL subdirs and use 777 for OpenShift arbitrary UIDs
    mkdir -p /var/cache/nginx/client_temp \
             /var/cache/nginx/proxy_temp \
             /var/cache/nginx/fastcgi_temp \
             /var/cache/nginx/scgi_temp \
             /var/cache/nginx/uwsgi_temp \
             /var/lib/nginx/tmp/client_body \
             /var/lib/nginx/tmp/proxy \
             /var/lib/nginx/tmp/fastcgi \
             /var/lib/nginx/tmp/uwsgi \
             /var/lib/nginx/tmp/scgi \
             /var/lib/nginx/logs \
             /var/run && \
    chmod -R 777 /var/cache/nginx /var/log/nginx /var/lib/nginx /var/run && \
    # Redirect nginx logs to stdout/stderr
    ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log

# Copy React build
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --chown=appuser:appgroup --from=builder /app/build .
RUN chmod -R 755 /usr/share/nginx/html

# Copy Node.js server
RUN mkdir -p /app/server /app/server/.npm /app/server/env /app/server/sessions && \
    chown -R appuser:appgroup /app
COPY --chown=appuser:appgroup --from=builder /app/server /app/server
RUN chmod -R 775 /app && chmod 777 /app/server/sessions

# Copy nginx configuration - use 777 so init script can write default.conf
COPY --chown=appuser:appgroup nginx/nginx.conf.template /etc/nginx/conf.d/default.conf.template
COPY --chown=appuser:appgroup nginx/main.conf /etc/nginx/nginx.conf
RUN chmod -R 777 /etc/nginx/conf.d

# Environment variables
ENV HOME=/app/server \
    NODE_ENV=production \
    NPM_CONFIG_PREFIX=/app/server/.npm \
    LOG_LEVEL=info

## =============================================================================
## s6-overlay: Configure services with proper dependencies
## =============================================================================
RUN mkdir -p /etc/s6-overlay/s6-rc.d/nginx \
             /etc/s6-overlay/s6-rc.d/nginx/dependencies.d \
             /etc/s6-overlay/s6-rc.d/node-server \
             /etc/s6-overlay/s6-rc.d/user/contents.d && \
    # Nginx service - depends on legacy-cont-init (waits for envsubst)
    echo "longrun" > /etc/s6-overlay/s6-rc.d/nginx/type && \
    touch /etc/s6-overlay/s6-rc.d/nginx/dependencies.d/legacy-cont-init && \
    printf '#!/command/with-contenv sh\nexec nginx -g "daemon off;"\n' \
        > /etc/s6-overlay/s6-rc.d/nginx/run && \
    # Node.js service
    echo "longrun" > /etc/s6-overlay/s6-rc.d/node-server/type && \
    printf '#!/command/with-contenv sh\ncd /app/server && exec npm start\n' \
        > /etc/s6-overlay/s6-rc.d/node-server/run && \
    # Enable services
    touch /etc/s6-overlay/s6-rc.d/user/contents.d/nginx \
          /etc/s6-overlay/s6-rc.d/user/contents.d/node-server && \
    chmod +x /etc/s6-overlay/s6-rc.d/nginx/run \
             /etc/s6-overlay/s6-rc.d/node-server/run && \
    chmod -R 775 /etc/s6-overlay /run

## =============================================================================
## Container initialization: Validate env vars and configure nginx
## =============================================================================
# hadolint ignore=SC2016
RUN mkdir -p /etc/cont-init.d && \
    printf '%s\n' \
        '#!/command/with-contenv sh' \
        'set -e' \
        '' \
        'log() { echo "{\"timestamp\":\"$(date -u +%%Y-%%m-%%dT%%H:%%M:%%SZ)\",\"level\":\"$1\",\"message\":\"$2\"}" >&2; }' \
        '' \
        'log info "Starting container initialization"' \
        '' \
        'if [ -z "$GATEWAY" ]; then' \
        '    log error "GATEWAY environment variable is not set"' \
        '    exit 1' \
        'fi' \
        '' \
        'log info "Environment validation passed (GATEWAY=$GATEWAY)"' \
        '' \
        'envsubst '"'"'${INTERNAL_SECRET} ${GATEWAY}'"'"' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf' \
        '' \
        'if nginx -t 2>&1 | grep -q "syntax is ok"; then' \
        '    log info "Nginx configuration validated"' \
        'else' \
        '    log error "Nginx configuration validation failed"' \
        '    nginx -t' \
        '    exit 1' \
        'fi' \
        '' \
        'log info "Container initialization completed"' \
    > /etc/cont-init.d/01-envsubst && \
    chmod +x /etc/cont-init.d/01-envsubst

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
    CMD wget -qO/dev/null --spider --timeout=3 http://localhost:8080/health || \
        wget -qO/dev/null --spider --timeout=3 http://localhost:8080/ || exit 1

EXPOSE 8080 5000

USER 1001

ENTRYPOINT ["/init"]