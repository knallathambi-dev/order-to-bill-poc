{{/*
Expand the name of the chart.
*/}}
{{- define "catalog.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "catalog.fullname" -}}
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
Create the name of the service account to use
*/}}
{{- define "catalog.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "catalog.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}



{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "catalog.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "catalog.labels" -}}
helm.sh/chart: {{ include "catalog.chart" . }}
{{ include "catalog.selectorLabels" . }}
{{ include "catalog.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: catalog-product-offering-price
{{- end }}

{{/*
Selector labels
*/}}
{{- define "catalog.selectorLabels" -}}
app.kubernetes.io/name: {{ include "catalog.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: catalog-product-offering-price
{{- end }}


{{/*
Monitoring labels
*/}}
{{- define "catalog.monitoringLabels" -}}
application: {{ default "catalog-product-offering-price" .Values.monitoring.application }}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default "disco" .Values.monitoring.productName }}
setname: {{ include "catalog.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
environment: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
{{- end }}


{{/*
Shutdown labels
*/}}
{{- define "catalog.shutdownLabels" -}}
stoppable: {{ default "none" .Values.dailyshutdown }}
{{- end }}


{{/*
extra fluentd labels for loki
*/}}
{{- define "catalog.lokiLabels" -}}
{{- printf "%s , \"instance\": \"%s\", \"name\": \"%s\", \"productname\": \"%s\", \"setname\": \"%s\", \"container_name\": \"disco-catalog\"" .Values.fluentd.sidecar.loki.extra_labels .Release.Name (include "catalog.name" .) (default .Chart.Name .Values.monitoring.productName) (include "catalog.fullname" .) }}
{{- end -}}