# SPDX-FileCopyrightText: 2025, 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

FROM nginx:mainline-alpine

LABEL name="DISCOBOLE Order Management documentation" \
      description="DISCOBOLE Order Management documentation made with mkdocs" \
      maintainer="christophe.muzard@orange.com"

# copying website into image
COPY public /usr/share/nginx/html/
COPY static /usr/share/nginx/html/

# setting permissions in order to make the image compliant with openshift
# see https://torstenwalter.de/openshift/nginx/2017/08/04/nginx-on-openshift.html
#
# setting default port for nginx
RUN chmod g+rwx /var/cache/nginx /var/log/nginx /var/run \
 && chgrp -R root /var/cache/nginx \
 && sed -i.bak 's/listen\(.*\)80;/listen 8080;/' /etc/nginx/conf.d/default.conf
EXPOSE 8080

# setting default user for nginx
# see https://docs.openshift.org/latest/creating_images/guidelines.html#openshift-specific-guidelines
RUN sed -i.bak 's/^user/#user/' /etc/nginx/nginx.conf \
 && addgroup nginx root
USER nginx

CMD ["nginx", "-g", "daemon off;"]
