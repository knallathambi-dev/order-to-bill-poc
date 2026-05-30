# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

kubectl apply -f event-service.component.yml
kubectl apply -f event-service.svc.yml
kubectl apply -f event-service.cm.yml
kubectl apply -f event-service.deploy.yml
