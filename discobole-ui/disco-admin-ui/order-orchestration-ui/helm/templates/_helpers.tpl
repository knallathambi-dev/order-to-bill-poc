{{/*
Expand the name of the chart.
*/}}
{{- define "orderorchestrationui.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "orderorchestrationui.fullname" -}}
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
{{- define "orderorchestrationui.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "orderorchestrationui.labels" -}}
helm.sh/chart: {{ include "orderorchestrationui.chart" . }}
{{ include "orderorchestrationui.selectorLabels" . }}
{{ include "orderorchestrationui.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: productinventoryui
{{- end }}

{{/*
Selector labels
*/}}
{{- define "orderorchestrationui.selectorLabels" -}}
app.kubernetes.io/name: {{ include "orderorchestrationui.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: productinventoryui
{{- end }}

{{/*
Monitoring labels
*/}}
{{- define "orderorchestrationui.monitoringLabels" -}}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default .Chart.Name .Values.monitoring.productName }}
setname: {{ include "orderorchestrationui.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
env: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
application: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "orderorchestrationui.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "orderorchestrationui.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the content for the diod token regcred secret to use
*/}}
{{- define "orderorchestrationui.regcredData" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"pipeline-deployer-review@orange.com\",\"auth\":\"%s\"}}}" .Values.registryCredentials.url .Values.registryCredentials.token.userName .Values.registryCredentials.token.password (include "orderorchestrationui.regcredAuth" .)) | b64enc }}
{{- end }}
{{- end }}

{{- define "orderorchestrationui.regcredAuth" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "%s:%s" .Values.registryCredentials.token.userName .Values.registryCredentials.token.password ) | b64enc  }}
{{- end }}
{{- end }}


{{/*
Shutdown labels
*/}}
{{- define "orderorchestrationui.shutdownLabels" -}}
stoppable: {{ default "none" .Values.scheduledshutdown }}
{{- end }}