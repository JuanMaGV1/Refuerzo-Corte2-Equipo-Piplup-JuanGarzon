# commit-msg.ps1
#
# Hook de pre-commit que valida el formato de Conventional Commits.
#
# Uso esperado:
#   <tipo>(<scope>): <descripción>
#
# Tipos permitidos:
#   feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert
#
# Ejemplos válidos:
#   feat(drones): agregar tipo SUMERGIBLE_PROFUNDO
#   fix(asignador): corregir filtro de batería mínima
#   docs: actualizar README con nivel Empoleon
#
# Para instalar: ejecuta .githooks/install-hooks.ps1 una vez.

param(
    [Parameter(Mandatory=$true)]
    [string]$CommitMessageFile
)

$ErrorActionPreference = "Stop"

# Lee la primera línea del mensaje de commit (el asunto)
$firstLine = Get-Content $CommitMessageFile -First 1 -ErrorAction SilentlyContinue

if (-not $firstLine) {
    Write-Host "[commit-msg] ERROR: mensaje de commit vacío." -ForegroundColor Red
    exit 1
}

# Patrón Conventional Commits
# - tipo: feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert
# - scope opcional entre paréntesis
# - dos puntos obligatorios
# - espacio obligatorio tras los dos puntos
# - descripción de al menos 3 caracteres
$pattern = '^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\([a-z0-9\-]+\))?: .{3,}$'

if ($firstLine -notmatch $pattern) {
    Write-Host ""
    Write-Host "[commit-msg] ERROR: el mensaje no cumple Conventional Commits." -ForegroundColor Red
    Write-Host ""
    Write-Host "  Mensaje recibido: $firstLine" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  Formato esperado:" -ForegroundColor Cyan
    Write-Host "    <tipo>(<scope>): <descripción>" -ForegroundColor White
    Write-Host ""
    Write-Host "  Tipos permitidos:" -ForegroundColor Cyan
    Write-Host "    feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert" -ForegroundColor White
    Write-Host ""
    Write-Host "  Ejemplos:" -ForegroundColor Cyan
    Write-Host "    feat(drones): agregar tipo SUMERGIBLE_PROFUNDO" -ForegroundColor White
    Write-Host "    fix(asignador): corregir filtro de batería mínima" -ForegroundColor White
    Write-Host "    docs: actualizar README con nivel Empoleon" -ForegroundColor White
    Write-Host ""
    exit 1
}

Write-Host "[commit-msg] OK: mensaje cumple Conventional Commits." -ForegroundColor Green
exit 0