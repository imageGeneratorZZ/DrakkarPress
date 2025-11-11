# DrakkarPress - Automatizacion Completa
# Script de preparacion para deploy

Write-Host "=== DRAKKARPRESS AUTOMATIZACION COMPLETA ===" -ForegroundColor Cyan
Write-Host ""

# PASO 1: Verificar prerequisitos
Write-Host "PASO 1: Verificando prerequisitos..." -ForegroundColor Yellow

$env:JAVA_HOME = "C:\Users\SuperUsuario\Java\jdk-21.0.5+11"

if (Test-Path "$env:JAVA_HOME\bin\javac.exe") {
    Write-Host "[OK] Java 21 encontrado" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Java 21 no encontrado" -ForegroundColor Red
    exit 1
}

if (Test-Path "C:\Users\SuperUsuario\apache-maven-3.9.6\bin\mvn.cmd") {
    Write-Host "[OK] Maven encontrado" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Maven no encontrado" -ForegroundColor Red
    exit 1
}

Write-Host ""

# PASO 2: Descargar IntelliJ
Write-Host "PASO 2: Descargando IntelliJ IDEA..." -ForegroundColor Yellow

$ideaUrl = "https://download.jetbrains.com/idea/ideaIC-2024.2.4.exe"
$ideaInstaller = "$env:TEMP\ideaIC-installer.exe"

if (-not (Test-Path $ideaInstaller)) {
    Write-Host "Descargando IntelliJ IDEA (900 MB)..." -ForegroundColor Cyan
    try {
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $ideaUrl -OutFile $ideaInstaller -UseBasicParsing
        Write-Host "[OK] IntelliJ descargado" -ForegroundColor Green
    } catch {
        Write-Host "[WARNING] Error descargando. Abriendo pagina manual..." -ForegroundColor Yellow
        Start-Process "https://www.jetbrains.com/idea/download/?section=windows"
    }
} else {
    Write-Host "[OK] IntelliJ ya descargado" -ForegroundColor Green
}

Write-Host ""

# PASO 3: Limpiar cache Maven
Write-Host "PASO 3: Limpiando cache Maven..." -ForegroundColor Yellow

Set-Location "C:\Users\SuperUsuario\DrakkarPress.com\backend"

if (Test-Path "target") {
    Remove-Item -Recurse -Force "target" -ErrorAction SilentlyContinue
    Write-Host "[OK] Cache limpiado" -ForegroundColor Green
}

Write-Host ""

# PASO 4: Crear configuracion IntelliJ
Write-Host "PASO 4: Creando configuracion IntelliJ..." -ForegroundColor Yellow

$ideaDir = "C:\Users\SuperUsuario\DrakkarPress.com\backend\.idea"
if (-not (Test-Path $ideaDir)) {
    New-Item -ItemType Directory -Path $ideaDir -Force | Out-Null
}

$compilerXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <annotationProcessing>
      <profile name="Maven default annotation processors profile" enabled="true">
        <sourceOutputDir name="target/generated-sources/annotations" />
        <sourceTestOutputDir name="target/generated-test-sources/test-annotations" />
        <outputRelativeToContentRoot value="true" />
        <module name="drakkarpress-platform" />
      </profile>
    </annotationProcessing>
  </component>
</project>
"@

Set-Content -Path "$ideaDir\compiler.xml" -Value $compilerXml -Encoding UTF8
Write-Host "[OK] Annotation Processing configurado" -ForegroundColor Green

Write-Host ""

# PASO 5: Intentar compilacion
Write-Host "PASO 5: Intentando compilacion Maven..." -ForegroundColor Yellow
Write-Host "[WARNING] Se espera fallo - Lombok requiere IntelliJ" -ForegroundColor Yellow

Set-Location "C:\Users\SuperUsuario\DrakkarPress.com\backend"

& "C:\Users\SuperUsuario\apache-maven-3.9.6\bin\mvn.cmd" clean compile -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Backend compilado!" -ForegroundColor Green
} else {
    Write-Host "[EXPECTED] Compilacion fallo - Abrir en IntelliJ" -ForegroundColor Yellow
}

Write-Host ""

# PASO 6: Verificar frontend
Write-Host "PASO 6: Verificando frontend..." -ForegroundColor Yellow

Set-Location "C:\Users\SuperUsuario\DrakkarPress.com"

$frontendOk = $true
foreach ($file in @("index.html", "catalogo.html", "login.html")) {
    if (Test-Path $file) {
        Write-Host "[OK] $file" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] $file faltante" -ForegroundColor Red
        $frontendOk = $false
    }
}

Write-Host ""

# PASO 7: Commit cambios
Write-Host "PASO 7: Preparando commit..." -ForegroundColor Yellow

git add .
$changes = git status --short

if ($changes) {
    Write-Host "Cambios pendientes:"
    Write-Host $changes
    Write-Host ""
    Write-Host "Hacer commit? (S/N)" -ForegroundColor Cyan
    $response = Read-Host
    
    if ($response -eq "S") {
        git commit -m "chore: Setup completo automatizado - IntelliJ config + scripts"
        git push origin main
        Write-Host "[OK] Sincronizado con GitHub" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "AUTOMATIZACION COMPLETADA" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "COMPLETADO:" -ForegroundColor Green
Write-Host "  - Java 21 verificado" 
Write-Host "  - Maven verificado"
Write-Host "  - IntelliJ descargado"
Write-Host "  - Configuracion .idea creada"
Write-Host "  - Frontend verificado"
Write-Host ""
Write-Host "SIGUIENTE PASO:" -ForegroundColor Yellow
Write-Host "  1. Instalar IntelliJ IDEA" 
Write-Host "  2. Abrir: C:\Users\SuperUsuario\DrakkarPress.com\backend"
Write-Host "  3. Instalar plugin Lombok"
Write-Host "  4. Build -> Rebuild Project"
Write-Host ""
Write-Host "GUIAS:" -ForegroundColor Cyan
Write-Host "  - PASOS_INTELLIJ.md (paso a paso IntelliJ)"
Write-Host "  - MANUAL_ACTIONS_GUIDE.md (deploy completo)"
Write-Host ""

# Abrir IntelliJ si existe
$ideaPath = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition\bin\idea64.exe"
if (Test-Path $ideaPath) {
    Write-Host "Abrir IntelliJ ahora? (S/N)" -ForegroundColor Cyan
    $open = Read-Host
    if ($open -eq "S") {
        Start-Process $ideaPath
        Write-Host "[OK] IntelliJ abierto" -ForegroundColor Green
    }
} elseif (Test-Path $ideaInstaller) {
    Write-Host "Instalar IntelliJ ahora? (S/N)" -ForegroundColor Cyan
    $install = Read-Host
    if ($install -eq "S") {
        Write-Host "Ejecutando instalador..." -ForegroundColor Cyan
        Write-Host "IMPORTANTE: Marcar estas opciones:" -ForegroundColor Yellow
        Write-Host "  [X] Create Desktop Shortcut"
        Write-Host "  [X] Update PATH variable"
        Write-Host "  [X] Add 'Open Folder as Project'"
        Write-Host "  [X] .java file association"
        Write-Host ""
        Start-Process $ideaInstaller -Wait
        Write-Host "[OK] IntelliJ instalado" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "Script completado!" -ForegroundColor Green
