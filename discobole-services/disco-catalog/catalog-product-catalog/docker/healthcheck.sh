# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

!/bin/bash


## Specify the address
ADDRESS="http://localhost:8001/actuator/health"


## Specify the service name
SERVICE="Email"


## Check the health status of container
STATUS=$(curl --location --silent --max-time 10 --output /dev/null --write-out %{http_code} ${ADDRESS})

if [ "${STATUS}" -ge "200" ] && [ "${STATUS}" -le "399" ]; then
  echo "${SERVICE} is healthy (${STATUS})." && exit 0
else
  echo "${SERVICE} is unhealthy (${STATUS})." && exit 1
fi
