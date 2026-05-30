# SPDX-FileCopyrightText: 2025 - 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

FROM alpine:3.19

# hadolint ignore=DL3018
RUN apk add --no-cache bash gettext nginx nginx-mod-http-headers-more && \
    mkdir -p /var/cache/nginx \
             /var/log/nginx \
             /var/lib/nginx/logs /var/lib/nginx/tmp \
             /etc/nginx/conf.d && \
    chmod -R 777 /var/cache/nginx /var/log/nginx /var/lib/nginx

WORKDIR /usr/share/nginx/html
RUN rm -rf ./*

COPY dist/ .
COPY env/env.template.js ./env.template.js
COPY nginx/main.conf /etc/nginx/nginx.conf
COPY nginx/nginx.conf.template /etc/nginx/conf.d/default.conf.template

RUN chmod -R 777 /usr/share/nginx/html /etc/nginx/conf.d

USER nginx

ENTRYPOINT ["/bin/sh", "-c", "\
envsubst < /usr/share/nginx/html/env.template.js > /usr/share/nginx/html/env.js && \
envsubst '${GATEWAY}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && \
nginx -g 'daemon off;'"]