# DrakkarPress Backend - Database Setup Script
# PowerShell script to initialize the database

Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host "DrakkarPress Database Setup" -ForegroundColor Cyan
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host ""

# Check if PostgreSQL is installed
Write-Host "Checking PostgreSQL installation..." -ForegroundColor Yellow
$pgService = Get-Service -Name postgresql* -ErrorAction SilentlyContinue

if (-not $pgService) {
    Write-Host "[ERROR] PostgreSQL service not found!" -ForegroundColor Red
    Write-Host "Please install PostgreSQL 12+ from: https://www.postgresql.org/download/" -ForegroundColor Red
    exit 1
}

# Check if PostgreSQL is running
Write-Host "Checking PostgreSQL service status..." -ForegroundColor Yellow
if ($pgService.Status -ne "Running") {
    Write-Host "Starting PostgreSQL service..." -ForegroundColor Yellow
    Start-Service $pgService.Name -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 3
    
    $pgService = Get-Service -Name $pgService.Name
    if ($pgService.Status -ne "Running") {
        Write-Host "❌ Failed to start PostgreSQL service!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "[OK] PostgreSQL is running" -ForegroundColor Green
Write-Host ""

# Check if psql is available
Write-Host "Checking psql command..." -ForegroundColor Yellow
$psqlPath = (Get-Command psql -ErrorAction SilentlyContinue).Source

if (-not $psqlPath) {
    Write-Host "[WARNING] psql command not found in PATH" -ForegroundColor Yellow
    Write-Host "Searching for PostgreSQL installation..." -ForegroundColor Yellow
    
    # Common PostgreSQL installation paths
    $possiblePaths = @(
        "C:\Program Files\PostgreSQL\*\bin\psql.exe",
        "C:\Program Files (x86)\PostgreSQL\*\bin\psql.exe"
    )
    
    foreach ($path in $possiblePaths) {
        $found = Get-ChildItem $path -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $psqlPath = $found.FullName
            break
        }
    }
    
    if (-not $psqlPath) {
        Write-Host "[ERROR] Could not find psql.exe!" -ForegroundColor Red
        Write-Host "Please add PostgreSQL bin directory to your PATH" -ForegroundColor Red
        exit 1
    }
}

Write-Host "[OK] Found psql at: $psqlPath" -ForegroundColor Green
Write-Host ""

# Prompt for database credentials
Write-Host "Enter PostgreSQL credentials:" -ForegroundColor Cyan
$dbUser = Read-Host "PostgreSQL username [default: postgres]"
if ([string]::IsNullOrWhiteSpace($dbUser)) {
    $dbUser = "postgres"
}

$dbPassword = Read-Host "PostgreSQL password" -AsSecureString
$dbPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassword)
)

Write-Host ""

# Set environment variable for password
$env:PGPASSWORD = $dbPasswordPlain

# Check if database exists
Write-Host "Checking if database 'drakkarpress' exists..." -ForegroundColor Yellow
$checkDb = & $psqlPath -U $dbUser -h localhost -p 5432 -lqt 2>&1 | Select-String "drakkarpress"

if ($checkDb) {
    Write-Host "[WARNING] Database 'drakkarpress' already exists" -ForegroundColor Yellow
    $overwrite = Read-Host "Do you want to reinitialize it? (y/N)"
    
    if ($overwrite -eq "y" -or $overwrite -eq "Y") {
        Write-Host "Dropping existing database..." -ForegroundColor Yellow
        & $psqlPath -U $dbUser -h localhost -p 5432 -c "DROP DATABASE IF EXISTS drakkarpress;" 2>&1 | Out-Null
    } else {
        Write-Host "Using existing database..." -ForegroundColor Yellow
        $skipCreate = $true
    }
}

if (-not $skipCreate) {
    # Create database
    Write-Host "Creating database 'drakkarpress'..." -ForegroundColor Yellow
    $result = & $psqlPath -U $dbUser -h localhost -p 5432 -c "CREATE DATABASE drakkarpress;" 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Failed to create database!" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
        exit 1
    }
    
    Write-Host "[OK] Database created successfully" -ForegroundColor Green
    Write-Host ""
}

# Run initialization script
if (Test-Path "init-db.sql") {
    Write-Host "Running initialization script..." -ForegroundColor Yellow
    $result = & $psqlPath -U $dbUser -h localhost -p 5432 -d drakkarpress -f "init-db.sql" 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Failed to run initialization script!" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
    } else {
        Write-Host "[OK] Initialization script executed successfully" -ForegroundColor Green
    }
} else {
    Write-Host "[WARNING] init-db.sql not found, skipping initialization" -ForegroundColor Yellow
}

Write-Host ""

# Create .env file if it doesn't exist
if (-not (Test-Path ".env")) {
    Write-Host "Creating .env file..." -ForegroundColor Yellow
    
    if (Test-Path ".env.example") {
        Copy-Item ".env.example" ".env"
        
        # Update database credentials in .env
        $envContent = Get-Content ".env" -Raw
        $envContent = $envContent -replace "DATABASE_USERNAME=postgres", "DATABASE_USERNAME=$dbUser"
        $envContent = $envContent -replace "DATABASE_PASSWORD=postgres", "DATABASE_PASSWORD=$dbPasswordPlain"
        Set-Content ".env" $envContent
        
        Write-Host "[OK] .env file created with database credentials" -ForegroundColor Green
    } else {
        Write-Host "[WARNING] .env.example not found" -ForegroundColor Yellow
    }
} else {
    Write-Host "[INFO] .env file already exists" -ForegroundColor Cyan
}

# Clear password from environment
$env:PGPASSWORD = $null

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host "[SUCCESS] Database setup completed!" -ForegroundColor Green
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Database Information:" -ForegroundColor Cyan
Write-Host "  Host: localhost" -ForegroundColor White
Write-Host "  Port: 5432" -ForegroundColor White
Write-Host "  Database: drakkarpress" -ForegroundColor White
Write-Host "  Username: $dbUser" -ForegroundColor White
Write-Host ""
Write-Host "Default Admin Credentials:" -ForegroundColor Cyan
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Email: admin@drakkarpress.com" -ForegroundColor White
Write-Host "  Password: admin123" -ForegroundColor Yellow
Write-Host "  [WARNING] CHANGE THIS PASSWORD IN PRODUCTION!" -ForegroundColor Red
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Review and update .env file with your configuration" -ForegroundColor White
Write-Host "  2. Build the project: mvn clean install" -ForegroundColor White
Write-Host "  3. Run the application: mvn spring-boot:run" -ForegroundColor White
Write-Host "  4. Test connection: http://localhost:8080/api/health" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
