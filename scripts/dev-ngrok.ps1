Param(
  [int]$Port = 8080
)

Write-Host "[*] Lanzando backend (si no corre) y ngrok para puerto $Port"

# Iniciar backend si el puerto no está en uso
$inUse = (Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue)
if (-not $inUse) {
  Write-Host "[*] Iniciando backend Spring Boot"
  Start-Process -FilePath "./mvnw.cmd" -ArgumentList "-Dspring.profiles.active=dev","spring-boot:run" -WindowStyle Minimized
  Start-Sleep -Seconds 5
}

if (-not (Get-Command ngrok -ErrorAction SilentlyContinue)) {
  Write-Host "[!] ngrok no instalado"; exit 1
}

Write-Host "[*] Iniciando ngrok"
Start-Process -FilePath "ngrok" -ArgumentList "http $Port" -WindowStyle Minimized

# Esperar túnel
$publicUrl = $null
for ($i=0; $i -lt 20; $i++) {
  Start-Sleep -Seconds 1
  try {
    $resp = Invoke-RestMethod -Uri "http://127.0.0.1:4040/api/tunnels" -ErrorAction SilentlyContinue
    $publicUrl = ($resp.tunnels | Where-Object {$_.public_url -like "https://*"} | Select-Object -First 1).public_url
    if ($publicUrl) { break }
  } catch {}
}

if (-not $publicUrl) {
  Write-Host "[!] No se obtuvo URL ngrok"; exit 1
}

"ALLOWED_ORIGINS=$publicUrl,http://localhost:3000,http://localhost:5173" | Out-File -Encoding utf8 .env.ngrok
Write-Host "[*] URL pública: $publicUrl"
Write-Host "Archivo .env.ngrok generado."
Write-Host "`nNEXT_PUBLIC_API_ORIGIN=$publicUrl`n"
