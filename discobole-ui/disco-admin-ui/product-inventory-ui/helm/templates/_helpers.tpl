{{/*
Expand the name of the chart.
*/}}
{{- define "productinventoryui.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "productinventoryui.fullname" -}}
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
{{- define "productinventoryui.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "productinventoryui.labels" -}}
helm.sh/chart: {{ include "productinventoryui.chart" . }}
{{ include "productinventoryui.selectorLabels" . }}
{{ include "productinventoryui.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: productinventoryui
{{- end }}

{{/*
Selector labels
*/}}
{{- define "productinventoryui.selectorLabels" -}}
app.kubernetes.io/name: {{ include "productinventoryui.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: productinventoryui
{{- end }}

{{/*
Monitoring labels
*/}}
{{- define "productinventoryui.monitoringLabels" -}}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default .Chart.Name .Values.monitoring.productName }}
setname: {{ include "productinventoryui.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
env: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
application: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "productinventoryui.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "productinventoryui.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the content for the diod token regcred secret to use
*/}}
{{- define "productinventoryui.regcredData" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"pipeline-deployer-review@orange.com\",\"auth\":\"%s\"}}}" .Values.registryCredentials.url .Values.registryCredentials.token.userName .Values.registryCredentials.token.password (include "productinventoryui.regcredAuth" .)) | b64enc }}
{{- end }}
{{- end }}

{{- define "productinventoryui.regcredAuth" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "%s:%s" .Values.registryCredentials.token.userName .Values.registryCredentials.token.password ) | b64enc  }}
{{- end }}
{{- end }}


{{/*
Shutdown labels
*/}}
{{- define "productinventoryui.shutdownLabels" -}}
stoppable: {{ default "none" .Values.scheduledshutdown }}
{{- end }}