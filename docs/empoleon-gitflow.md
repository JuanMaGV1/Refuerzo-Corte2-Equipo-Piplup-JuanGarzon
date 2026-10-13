# GitFlow — Empoleon (v3.0.0)

Documento que describe el flujo GitFlow de Empoleon, los hooks de
validación de commits y la protección de ramas.

---

## 1. Ramas del repositorio

| Rama | Propósito | Tipo | ¿Protegida? |
|---|---|---|---|
| `main` | Código en producción | Permanente | ✅ Sí |
| `develop` | Integración continua | Permanente | ✅ Sí |
| `feature/*` | Una por funcionalidad | Temporal | — |
| `release/*` | Preparación de release | Temporal | — |
| `hotfix/*` | Correcciones urgentes | Temporal | — |

---

## 2. Protección de ramas en GitHub

### 2.1 `main`

Configuración en **Settings → Branches → Add rule** con el nombre `main`:

- ✅ Require a pull request before merging.
  - ✅ Require approvals: 1.
  - ✅ Dismiss stale pull request approvals when new commits are pushed.
- ✅ Require status checks to pass before merging.
  - ✅ Require branches to be up to date before merging.
  - Checks requeridos: `ci/tests`, `ci/coverage`.
- ✅ Require conversation resolution before merging.
- ✅ Include administrators (nadie puede saltarse las reglas).
- ✅ Do not allow bypassing the above settings.
- ❌ Allow force pushes (deshabilitado).
- ❌ Allow deletions (deshabilitado).

### 2.2 `develop`

Misma configuración que `main`, con la excepción de:

- ✅ Require approvals: 1.
- Se permite un poco más de flexibilidad para merges rápidos de
  `feature/*` pequeños.

---

## 3. Hook de pre-commit

El repositorio incluye un hook de validación de Conventional Commits
en `.githooks/commit-msg.ps1`.

### Instalación

```powershell
# Desde la raíz del repo, en PowerShell
.\.githooks\install-hooks.ps1