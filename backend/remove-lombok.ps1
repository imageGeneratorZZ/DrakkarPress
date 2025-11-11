# Script para remover anotaciones de Lombok y generar getters/setters

Write-Host "🔧 Removiendo Lombok del proyecto DrakkarPress Backend..." -ForegroundColor Cyan

$backendPath = "C:\Users\SuperUsuario\DrakkarPress.com\backend"

# Archivos Java que usan Lombok
$javaFiles = Get-ChildItem -Path "$backendPath\src\main\java" -Filter "*.java" -Recurse

Write-Host "`n📊 Encontrados $($javaFiles.Count) archivos Java" -ForegroundColor Yellow

# Contador
$processedFiles = 0

foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    
    # Verificar si usa Lombok
    if ($content -match '@Data|@Getter|@Setter|@Builder|@AllArgsConstructor|@NoArgsConstructor|@RequiredArgsConstructor') {
        Write-Host "  📝 Procesando: $($file.Name)" -ForegroundColor Gray
        
        # Remover imports de Lombok
        $content = $content -replace 'import lombok\.\*;', ''
        $content = $content -replace 'import lombok\..*;', ''
        
        # Remover anotaciones de Lombok pero mantener las clases
        $content = $content -replace '@Data\s*\n', ''
        $content = $content -replace '@Getter\s*\n', ''
        $content = $content -replace '@Setter\s*\n', ''
        $content = $content -replace '@Builder\s*\n', ''
        $content = $content -replace '@AllArgsConstructor\s*\n', ''
        $content = $content -replace '@NoArgsConstructor\s*\n', ''
        $content = $content -replace '@RequiredArgsConstructor\s*\n', ''
        
        # Guardar archivo modificado
        Set-Content -Path $file.FullName -Value $content -NoNewline
        
        $processedFiles++
    }
}

Write-Host "`n✅ Procesados $processedFiles archivos" -ForegroundColor Green
Write-Host "`n⚠️  NOTA: Los getters/setters deben generarse manualmente con IDE" -ForegroundColor Yellow
Write-Host "   - Opción 1: IntelliJ IDEA → Code → Generate → Getters/Setters" -ForegroundColor Yellow
Write-Host "   - Opción 2: VS Code con Extension Pack for Java → Source Action → Generate Getters/Setters" -ForegroundColor Yellow
Write-Host "`n📋 Próximo paso: Abrir proyecto en IDE y generar métodos" -ForegroundColor Cyan
