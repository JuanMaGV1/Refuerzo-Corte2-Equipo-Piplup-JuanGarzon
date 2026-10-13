# install-hooks.ps1
#
# Instala los hooks de git en .git/hooks/ del repo actual.
#
# Ejecuta desde la raíz del repositorio:
#   .\.githooks\install-hooks.ps1
#
# Si PowerShell bloquea la ejecución por política de seguridad, ejecuta:
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
# y vuelve a intentar.

$ErrorActionPreference = "Stop"

$root = git rev-parse --show-toplevel
$hookSource = Join-Path $root ".githooks\commit-msg.ps1"
$hookTarget = Join-Path $root ".git\hooks\commit-msg"

if (-not (Test-Path $hookSource)) {
    Write-Host "ERROR: no se encuentra $hookSource" -ForegroundColor Red
    exit 1
}

# En Windows, git ejecuta los hooks como scripts sh.
# Usamos un wrapper sh que invoca powershell.exe con el script.
$wrapperContent = @"
#!/bin/sh
# Wrapper para ejecutar el hook de PowerShell en Windows.
powershell.exe -ExecutionPolicy Bypass -File "$($hookSource -replace '\\', '/')" "`$1"
"@

# Crea el directorio .git/hooks si no existe (normalmente existe)
$hooksDir = Join-Path $root ".git\hooks"
if (-not (Test-Path $hooksDir)) {
    New-Item -ItemType Directory -Path $hooksDir | Out-Null
}

# Escribe el wrapper con codificación UTF-8 sin BOM
[System.IO.File]::WriteAllText($hookTarget, $wrapperContent, [System.Text.UTF8Encoding]::new($false))

Write-Host ""
Write-Host "Hook instalado en: $hookTarget" -ForegroundColor Green
Write-Host ""
Write-Host "Prueba el hook con un commit de ejemplo:" -ForegroundColor Cyan
Write-Host "  git commit --allow-empty -m 'test: probar hook'" -ForegroundColor White
Write-Host ""
Write-Host "Prueba con un commit inválido (debe fallar):" -ForegroundColor Cyan
Write-Host "  git commit --allow-empty -m 'mensaje malo'" -ForegroundColor White
Write-Host ""