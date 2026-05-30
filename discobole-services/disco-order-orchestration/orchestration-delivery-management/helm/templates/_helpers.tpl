{{/*
Expand the name of the chart.
*/}}
{{- define "delivery_management.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "delivery_management.fullname" -}}
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

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "delivery_management.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "delivery_management.labels" -}}
helm.sh/chart: {{ include "delivery_management.chart" . }}
{{ include "delivery_management.selectorLabels" . }}
{{ include "delivery_management.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "delivery_management.selectorLabels" -}}
app.kubernetes.io/name: {{ include "delivery_management.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Monitoring labels
*/}}
{{- define "delivery_management.monitoringLabels" -}}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default .Chart.Name .Values.monitoring.productName }}
setname: {{ include "delivery_management.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
env: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
application: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
extra fluentd labels for loki
*/}}
{{- define "delivery_management.lokiLabels" -}}
{{- printf "%s , \"instance\": \"%s\", \"name\": \"%s\", \"productname\": \"%s\", \"setname\": \"%s\", \"container_name\": \"disco-delivery_management\"" .Values.fluentd.sidecar.loki.extra_labels .Release.Name (include "delivery_management.name" .) (default .Chart.Name .Values.monitoring.productName) (include "delivery_management.fullname" .) }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "delivery_management.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "delivery_management.fullname" .) .Values.serviceAccount.name }}
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
{{- include "delivery_management.fullname" . | replace "delivery_management" "configuration" }}
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
{{- define "delivery_management.regcredData" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"pipeline-deployer-review@orange.com\",\"auth\":\"%s\"}}}" .Values.registryCredentials.url .Values.registryCredentials.token.userName .Values.registryCredentials.token.password (include "delivery_management.regcredAuth" .)) | b64enc }}
{{- end }}
{{- end }}

{{- define "delivery_management.regcredAuth" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "%s:%s" .Values.registryCredentials.token.userName .Values.registryCredentials.token.password ) | b64enc  }}
{{- end }}
{{- end }}

{{/*
Shutdown labels
*/}}
{{- define "delivery_management.shutdownLabels" -}}
stoppable: {{ default "none" .Values.scheduledshutdown }}
{{- end }}
