{{/*
Expand the name of the chart.
*/}}
{{- define "productInventory.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "productInventory.fullname" -}}
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
{{- define "productInventory.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "productInventory.labels" -}}
helm.sh/chart: {{ include "productInventory.chart" . }}
{{ include "productInventory.selectorLabels" . }}
{{ include "productInventory.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "productInventory.selectorLabels" -}}
app.kubernetes.io/name: {{ include "productInventory.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Monitoring labels
*/}}
{{- define "productInventory.monitoringLabels" -}}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default .Chart.Name .Values.monitoring.productName }}
setname: {{ include "productInventory.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
env: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
application: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
extra fluentd labels for loki
*/}}
{{- define "productInventory.lokiLabels" -}}
{{- printf "%s , \"instance\": \"%s\", \"name\": \"%s\", \"productname\": \"%s\", \"setname\": \"%s\", \"container_name\": \"disco-product-inventory\"" .Values.fluentd.sidecar.loki.extra_labels .Release.Name (include "productInventory.name" .) (default .Chart.Name .Values.monitoring.productName) (include "productInventory.fullname" .) }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "productInventory.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "productInventory.fullname" .) .Values.serviceAccount.name }}
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
{{- include "productInventory.fullname" . | replace "productInventory" "configuration" }}
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
{{- define "productInventory.regcredData" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"pipeline-deployer-review@orange.com\",\"auth\":\"%s\"}}}" .Values.registryCredentials.url .Values.registryCredentials.token.userName .Values.registryCredentials.token.password (include "productInventory.regcredAuth" .)) | b64enc }}
{{- end }}
{{- end }}

{{- define "productInventory.regcredAuth" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "%s:%s" .Values.registryCredentials.token.userName .Values.registryCredentials.token.password ) | b64enc  }}
{{- end }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "mongo.fullname" -}}
{{- if .Values.mongo.enabled }}
{{- if .Values.mongo.fullnameOverride }}
{{- printf "mongo-%s-%s" .Values.mongo.fullnameOverride "productInventory-service" | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "mongo-%s-%s" .Release.Name "productInventory-service" | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end -}}


{{/*
Shutdown labels
*/}}
{{- define "productInventory.shutdownLabels" -}}
stoppable: {{ default "none" .Values.scheduledshutdown }}
{{- end }}
