# 🚀 SCRIPT MAESTRO DE DEPLOYMENT - DrakkarPress
# Ejecuta todo el proceso de deployment desde tu PC local hacia Internet

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  DRAKKARPRESS - DEPLOYMENT AUTOMATICO" -ForegroundColor Cyan
Write-Host "  De tu PC Local → DrakkarPress.com" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$ErrorActionPreference = "Continue"

# ============================================
# FASE 1: VERIFICAR HERRAMIENTAS
# ============================================
Write-Host "FASE 1: Verificando herramientas necesarias..." -ForegroundColor Yellow
Write-Host ""

# Node.js
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js no encontrado. Instalalo desde: https://nodejs.org/" -ForegroundColor Red
    exit 1
}

# Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java no encontrado" -ForegroundColor Red
    exit 1
}

# Maven
try {
    $mvnVersion = mvn --version 2>&1 | Select-Object -First 1
    Write-Host "✅ Maven: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Maven no encontrado (se puede usar wrapper)" -ForegroundColor Yellow
}

# Git
try {
    $gitVersion = git --version
    Write-Host "✅ Git: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Git no encontrado" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan

# ============================================
# FASE 2: COMPILAR BACKEND
# ============================================
Write-Host ""
Write-Host "FASE 2: Compilando Backend (Spring Boot)..." -ForegroundColor Yellow
Write-Host ""

if (Test-Path "backend\pom.xml") {
    Write-Host "Backend encontrado. Compilando..." -ForegroundColor Gray
    cd backend
    
    if (Test-Path "mvnw.cmd") {
        Write-Host "Usando Maven Wrapper..." -ForegroundColor Gray
        .\mvnw.cmd clean package -DskipTests
    } else {
        Write-Host "Usando Maven global..." -ForegroundColor Gray
        mvn clean package -DskipTests
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Backend compilado exitosamente" -ForegroundColor Green
        $backendJar = Get-ChildItem -Path target -Filter "*.jar" -Exclude "*-sources.jar","*-javadoc.jar" | Select-Object -First 1
        Write-Host "   JAR: $($backendJar.Name)" -ForegroundColor Gray
    } else {
        Write-Host "❌ Error compilando backend" -ForegroundColor Red
    }
    
    cd ..
} else {
    Write-Host "⚠️ Backend no encontrado en ./backend" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan

# ============================================
# FASE 3: COMPILAR DESKTOP APP
# ============================================
Write-Host ""
Write-Host "FASE 3: Compilando Desktop App (Electron)..." -ForegroundColor Yellow
Write-Host ""

if (Test-Path "desktop-app\package.json") {
    Write-Host "Desktop App encontrada. Compilando..." -ForegroundColor Gray
    cd desktop-app
    
    Write-Host "Instalando dependencias..." -ForegroundColor Gray
    npm install --silent
    
    Write-Host "Compilando TypeScript..." -ForegroundColor Gray
    npm run build:main
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Desktop App compilada" -ForegroundColor Green
    } else {
        Write-Host "❌ Error compilando Desktop App" -ForegroundColor Red
    }
    
    cd ..
} else {
    Write-Host "⚠️ Desktop App no encontrada" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan

# ============================================
# FASE 4: PREPARAR FRONTEND
# ============================================
Write-Host ""
Write-Host "FASE 4: Preparando Frontend para deployment..." -ForegroundColor Yellow
Write-Host ""

if (Test-Path "index.html") {
    Write-Host "✅ Frontend HTML encontrado" -ForegroundColor Green
    
    # Verificar netlify.toml
    if (Test-Path "netlify.toml") {
        Write-Host "✅ netlify.toml configurado" -ForegroundColor Green
    } else {
        Write-Host "⚠️ netlify.toml no encontrado" -ForegroundColor Yellow
    }
    
    # Contar archivos HTML
    $htmlFiles = Get-ChildItem -Filter "*.html" | Measure-Object
    Write-Host "   Archivos HTML: $($htmlFiles.Count)" -ForegroundColor Gray
    
} else {
    Write-Host "❌ index.html no encontrado" -ForegroundColor Red
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan

# ============================================
# FASE 5: OPCIONES DE DEPLOYMENT
# ============================================
Write-Host ""
Write-Host "FASE 5: Opciones de Deployment" -ForegroundColor Yellow
Write-Host ""

Write-Host "Elige una opcion:" -ForegroundColor White
Write-Host "1. Ejecutar Backend LOCAL (http://localhost:8080)" -ForegroundColor Cyan
Write-Host "2. Ejecutar Desktop App LOCAL" -ForegroundColor Cyan
Write-Host "3. Deploy Frontend a NETLIFY" -ForegroundColor Cyan
Write-Host "4. Deploy Backend a RAILWAY" -ForegroundColor Cyan
Write-Host "5. Generar Instalador Windows (Desktop App)" -ForegroundColor Cyan
Write-Host "6. TODO: Local + Deploy completo" -ForegroundColor Cyan
Write-Host "7. Salir" -ForegroundColor Gray
Write-Host ""

$opcion = Read-Host "Selecciona (1-7)"

switch ($opcion) {
    "1" {
        Write-Host ""
        Write-Host "Ejecutando Backend..." -ForegroundColor Cyan
        cd backend
        $jar = Get-ChildItem -Path target -Filter "*.jar" -Exclude "*-sources.jar","*-javadoc.jar" | Select-Object -First 1
        if ($jar) {
            Write-Host "Iniciando $($jar.Name)..." -ForegroundColor Gray
            java -jar "target\$($jar.Name)"
        } else {
            Write-Host "❌ JAR no encontrado. Compila primero." -ForegroundColor Red
        }
        cd ..
    }
    "2" {
        Write-Host ""
        Write-Host "Ejecutando Desktop App..." -ForegroundColor Cyan
        cd desktop-app
        npx electron .
        cd ..
    }
    "3" {
        Write-Host ""
        Write-Host "Deployando Frontend a Netlify..." -ForegroundColor Cyan
        Write-Host ""
        
        # Verificar si netlify-cli está instalado
        try {
            netlify --version | Out-Null
            Write-Host "✅ Netlify CLI instalado" -ForegroundColor Green
        } catch {
            Write-Host "Instalando Netlify CLI..." -ForegroundColor Yellow
            npm install -g netlify-cli
        }
        
        Write-Host ""
        Write-Host "Iniciando login a Netlify..." -ForegroundColor Cyan
        netlify login
        
        Write-Host ""
        Write-Host "Deployando sitio..." -ForegroundColor Cyan
        netlify deploy --prod --dir=.
        
        Write-Host ""
        Write-Host "✅ Frontend deployado!" -ForegroundColor Green
        Write-Host "Configura tu dominio en: https://app.netlify.com" -ForegroundColor Gray
    }
    "4" {
        Write-Host ""
        Write-Host "Deployando Backend a Railway..." -ForegroundColor Cyan
        Write-Host ""
        
        # Verificar si railway-cli está instalado
        try {
            railway --version | Out-Null
            Write-Host "✅ Railway CLI instalado" -ForegroundColor Green
        } catch {
            Write-Host "Instalando Railway CLI..." -ForegroundColor Yellow
            npm install -g @railway/cli
        }
        
        Write-Host ""
        Write-Host "Iniciando login a Railway..." -ForegroundColor Cyan
        railway login
        
        Write-Host ""
        Write-Host "Deployando proyecto..." -ForegroundColor Cyan
        railway up
        
        Write-Host ""
        Write-Host "✅ Backend deployado!" -ForegroundColor Green
        Write-Host "Configura variables en: https://railway.app" -ForegroundColor Gray
    }
    "5" {
        Write-Host ""
        Write-Host "Generando Instalador Windows..." -ForegroundColor Cyan
        cd desktop-app
        npm run build:win
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "✅ Instalador generado!" -ForegroundColor Green
            Write-Host "Ubicacion: desktop-app\release\" -ForegroundColor Gray
            
            # Mostrar archivos generados
            if (Test-Path "release") {
                $releaseFiles = Get-ChildItem release -Recurse -Include "*.exe" | Select-Object -First 3
                foreach ($file in $releaseFiles) {
                    Write-Host "   - $($file.Name) ($([math]::Round($file.Length/1MB, 2)) MB)" -ForegroundColor Gray
                }
            }
        }
        cd ..
    }
    "6" {
        Write-Host ""
        Write-Host "Ejecutando TODO..." -ForegroundColor Cyan
        Write-Host ""
        
        # Backend en segundo plano
        Write-Host "1. Iniciando Backend..." -ForegroundColor Yellow
        cd backend
        $jar = Get-ChildItem -Path target -Filter "*.jar" -Exclude "*-sources.jar","*-javadoc.jar" | Select-Object -First 1
        if ($jar) {
            Start-Process powershell -ArgumentList "-NoExit", "-Command", "java -jar target\$($jar.Name)"
            Write-Host "✅ Backend iniciado en nueva ventana" -ForegroundColor Green
        }
        cd ..
        
        Start-Sleep -Seconds 3
        
        # Desktop App
        Write-Host "2. Iniciando Desktop App..." -ForegroundColor Yellow
        cd desktop-app
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "npx electron ."
        Write-Host "✅ Desktop App iniciada en nueva ventana" -ForegroundColor Green
        cd ..
        
        Start-Sleep -Seconds 2
        
        # Frontend local
        Write-Host "3. Iniciando Frontend local..." -ForegroundColor Yellow
        Start-Process "http://localhost:8080"
        
        Write-Host ""
        Write-Host "✅ Todo ejecutandose!" -ForegroundColor Green
        Write-Host "   Backend: http://localhost:8080" -ForegroundColor Gray
        Write-Host "   Desktop App: Ventana de Electron" -ForegroundColor Gray
    }
    "7" {
        Write-Host ""
        Write-Host "Saliendo..." -ForegroundColor Gray
        exit 0
    }
    default {
        Write-Host ""
        Write-Host "❌ Opcion invalida" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "✅ Proceso completado" -ForegroundColor Green
Write-Host ""
Write-Host "Documentacion completa: DEPLOYMENT_MASTER_PLAN.md" -ForegroundColor Gray
Write-Host "=====================================================" -ForegroundColor Cyan
