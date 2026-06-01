# Reglas de la práctica

- Trabaja SOLO dentro de la carpeta de tu módulo en `feature/<tu-modulo>/`.
- NO edites `:app`, `:core/*` ni `gradle/libs.versions.toml`.
- NO agregues dependencias. Si falta algo, solicítalo a un revisor.
- Consume las interfaces de `:core:domain`. Prohibido importar otro `feature/`.
- Toda la UI en Jetpack Compose. Botones de acción ≥ 56dp.
- Tu módulo debe COMPILAR y PASAR sus tests por sí solo (usa los fakes de `:core:testing`).
- Offline-first: nunca bloquees la operación por falta de red.
- Nunca muestres al chofer códigos HTTP, endpoints ni stack traces.
- El token JWT va solo en EncryptedSharedPreferences. No subas secretos.

## Git
- Rama: `feat/<modulo>-<grupo>` (ej. `feat/sync-worker-grupo07`).
- Commits pequeños y descriptivos.
- Abre un Pull Request a `main` con la plantilla. Requiere aprobación de 2 revisores.
