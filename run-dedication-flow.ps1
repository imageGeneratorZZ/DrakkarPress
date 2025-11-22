<#
Wrapper: run-dedication-flow.ps1
Permite ejecutar el flujo de dedicación desde la raíz del repo.
Redirige al script original en backend/dedication-flow.ps1.
#>
param(
  [string]$Email = "demo@drakkarpress.com",
  [string]$Password = "DemoPass123",
  [string]$Username = "demo",
  [string]$Prompt = "Historia épica de dragones",
  [int]$Chapters = 3,
  [string]$Dedication = "Para Ana, con toda mi inspiración",
  [double]$Price = 5.99,
  [int]$PollSeconds = 4,
  [int]$PollTimeoutSeconds = 240
)

$backendScript = Join-Path $PSScriptRoot "backend" | Join-Path -ChildPath "dedication-flow.ps1"
if(!(Test-Path $backendScript)){
  Write-Error "No se encontró el script backend en: $backendScript"
  exit 1
}

# Reenvía parámetros conservando nombres
& $backendScript @PSBoundParameters
