param(
  [Parameter(Mandatory=$true)][string]$Input,
  [string]$Title = "Libro",
  [string]$Author = "Autor",
  [string]$Cover,
  [string]$Out = "book.pdf",
  [string]$Css
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$mvnPom = Join-Path $root "tools/pdfgen/pom.xml"

Write-Host "Compilando pdfgen..." -ForegroundColor Cyan
mvn -q -DskipTests -f $mvnPom package

$jar = Join-Path $root "tools/pdfgen/target/pdfgen-jar-with-dependencies.jar"
if (!(Test-Path $jar)) { throw "Jar no encontrado: $jar" }

$argsList = @("--title", $Title, "--author", $Author, "--input", $Input, "--out", $Out)
if ($Cover) { $argsList += @("--cover", $Cover) }
if ($Css)   { $argsList += @("--css", $Css) }

Write-Host "Generando PDF..." -ForegroundColor Cyan
& java -jar $jar @argsList

Write-Host "Listo: $Out" -ForegroundColor Green
