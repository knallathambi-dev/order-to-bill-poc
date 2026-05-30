{{/*
Expand the name of the chart.
*/}}
{{- define "order_capture_mock_server.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "order_capture_mock_server.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- define "order_capture_mock_server.fullnameMobile" -}}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s-mobile" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "order_capture_mock_server.fullnameFiber" -}}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s-fiber" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "order_capture_mock_server.fullnamePartner" -}}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s-partner" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "order_capture_mock_server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "order_capture_mock_server.labels" -}}
helm.sh/chart: {{ include "order_capture_mock_server.chart" . }}
{{ include "order_capture_mock_server.selectorLabels" . }}
{{ include "order_capture_mock_server.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "order_capture_mock_server.selectorLabels" -}}
app.kubernetes.io/name: {{ include "order_capture_mock_server.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Monitoring labels
*/}}
{{- define "order_capture_mock_server.monitoringLabels" -}}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default .Chart.Name .Values.monitoring.productName }}
setname: {{ include "order_capture_mock_server.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
env: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
application: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
extra fluentd labels for loki
*/}}
{{- define "order_capture_mock_server.lokiLabels" -}}
{{- printf "%s , \"instance\": \"%s\", \"name\": \"%s\", \"productname\": \"%s\", \"setname\": \"%s\", \"container_name\": \"disco-order_capture_mock_server\"" .Values.fluentd.sidecar.loki.extra_labels .Release.Name (include "order_capture_mock_server.name" .) (default .Chart.Name .Values.monitoring.productName) (include "order_capture_mock_server.fullname" .) }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "order_capture_mock_server.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "order_capture_mock_server.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "redis.service" -}}
{{- if .Values.redis.enabled }}
{{- $baseName := include "redis.fullname" . -}}
{{- printf "%s-tcp" $baseName }}
{{- else if .Values.redis.host }}
{{- .Values.redis.host }}
{{- end }}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "configuration.fullname" -}}
{{- include "order_capture_mock_server.fullname" . | replace "order_capture_mock_server" "configuration" }}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "configuration.url" -}}
{{- if .Values.configuration }}
{{- if .Values.configuration.host }}
{{- printf "%s%s" .Values.configuration.host .Values.configuration.contextPath }}
{{- else }}
{{- printf "http://%s:8080%s" (include "configuration.fullname" .) .Values.configuration.contextPath | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}


{{/*
Create the content for the gitlab token regcred secret to use
*/}}
{{- define "order_capture_mock_server.regcredData" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"pipeline-deployer-review@orange.com\",\"auth\":\"%s\"}}}" .Values.registryCredentials.url .Values.registryCredentials.token.userName .Values.registryCredentials.token.password (include "order_capture_mock_server.regcredAuth" .)) | b64enc }}
{{- end }}
{{- end }}

{{- define "order_capture_mock_server.regcredAuth" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "%s:%s" .Values.registryCredentials.token.userName .Values.registryCredentials.token.password ) | b64enc  }}
{{- end }}
{{- end }}

{{/*
Shutdown labels
*/}}
{{- define "order_capture_mock_server.shutdownLabels" -}}
stoppable: {{ default "none" .Values.scheduledshutdown }}
{{- end }}