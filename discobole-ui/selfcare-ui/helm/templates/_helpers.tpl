{{/*
Expand the name of the chart.
*/}}
{{- define "selfcareui.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "selfcareui.fullname" -}}
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
{{- define "selfcareui.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "selfcareui.labels" -}}
helm.sh/chart: {{ include "selfcareui.chart" . }}
{{ include "selfcareui.selectorLabels" . }}
{{ include "selfcareui.monitoringLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "selfcareui.selectorLabels" -}}
app.kubernetes.io/name: {{ include "selfcareui.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Monitoring labels
*/}}
{{- define "selfcareui.monitoringLabels" -}}
domain: {{ default "appli" .Values.monitoring.domain }}
productname: {{ default .Chart.Name .Values.monitoring.productName }}
setname: {{ include "selfcareui.fullname" . }}
topology: {{ default "none" .Values.monitoring.topology }}
role: {{ default "none" .Values.monitoring.role }}
env: {{ default "default" .Values.monitoring.environment }}
internal-elasticsearch: 'true'
application: {{ default .Chart.Name .Values.monitoring.application }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "selfcareui.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "selfcareui.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the content for the diod token regcred secret to use
*/}}
{{- define "selfcareui.regcredData" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"pipeline-deployer-review@orange.com\",\"auth\":\"%s\"}}}" .Values.registryCredentials.url .Values.registryCredentials.token.userName .Values.registryCredentials.token.password (include "selfcareui.regcredAuth" .)) | b64enc }}
{{- end }}
{{- end }}

{{- define "selfcareui.regcredAuth" -}}
{{- if .Values.registryCredentials.create }}
{{- (printf "%s:%s" .Values.registryCredentials.token.userName .Values.registryCredentials.token.password ) | b64enc  }}
{{- end }}
{{- end }}

{{/*
Shutdown labels
*/}}
{{- define "selfcareui.shutdownLabels" -}}
stoppable: {{ default "none" .Values.scheduledshutdown }}
{{- end }}

{{/*
Ingress scheme helper.
Determines http vs https based on OpenShift route.openshift.io/termination annotation.
*/}}
{{- define "selfcareui.ingressScheme" -}}
{{- $term := default "" (index .Values.ingress.annotations "route.openshift.io/termination") -}}
{{- if or (eq $term "edge") (eq $term "reencrypt") (eq $term "passthrough") -}}
https
{{- else -}}
http
{{- end -}}
{{- end }}

{{/*
Allowed origins helper.
Builds a comma-separated list of allowed origins for CORS configuration.
Examples: "https://example.com,https://sub.example.com"

Supports hosts as objects with host and paths fields.
Requires ingress.enabled=true and at least one host configured.
*/}}
{{- define "selfcareui.allowedOrigins" -}}
{{- if not .Values.ingress.enabled -}}
{{- fail "ERROR: Ingress must be enabled. ALLOWED_ORIGINS cannot be generated without ingress configuration." -}}
{{- end -}}
{{- $scheme := include "selfcareui.ingressScheme" . -}}
{{- $origins := list -}}
{{- range .Values.ingress.hosts }}
  {{- $h := . -}}
  {{- if kindIs "map" . }}{{- $h = .host -}}{{- end -}}
  {{- if $h }}
    {{- $origins = append $origins (printf "%s://%s" $scheme $h) -}}
  {{- end -}}
{{- end -}}
{{- if not $origins -}}
{{- fail "ERROR: No ingress hosts configured. ALLOWED_ORIGINS cannot be empty. Please configure at least one host in ingress.hosts." -}}
{{- end -}}
{{- $origins | uniq | join "," -}}
{{- end }}