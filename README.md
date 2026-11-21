# LUKKA Television — proyecto Android

## Archivo principal
`app/src/main/java/com/grm/lukka/MainActivity.java`

## Compilar la APK en GitHub (paso a paso)
1. Descomprime este ZIP.
2. Sube **todo** el contenido (incluida la carpeta oculta `.github`) a la raíz de tu repositorio, rama `main`.
3. Entra a **Actions → LUKKA Television - Build APK**. Se ejecuta solo con cada push; también puedes pulsar **Run workflow**.
4. Al terminar (≈ 5–8 min la primera vez), descarga el artefacto **LUKKA-Television-debug** (ZIP con la APK) al final de la página de la ejecución.
5. Instala la APK en el Android/TV Box (permitir "orígenes desconocidos").

## APK release firmada (opcional)
Crea estos *Secrets* en `Settings → Secrets and variables → Actions`:
`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
Si existen, el workflow también genera el artefacto **LUKKA-Television-release**.

Generar la llave (una sola vez, guárdala bien):
```
keytool -genkeypair -v -keystore lukka.jks -alias lukka -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 lukka.jks      # pega el resultado en KEYSTORE_BASE64
```

## Publicar un Release con la APK
```
git tag v0.2.0
git push origin v0.2.0
```

## Base técnica
- Java 17 · Android Gradle Plugin 8.13.2 · Gradle Wrapper 8.13 (`gradlew` incluido)
- compileSdk 36 / targetSdk 35 / minSdk 23
- AndroidX Media3 1.11.1 (ExoPlayer + HLS)
- versionName `0.1.<n.º de ejecución>`; versionCode = n.º de ejecución

## Estado
Esqueleto con reproducción de una URL introducida manualmente (HLS / MPEG-TS, http y https). Pendiente: splash final, parser M3U, canal inicial, OSD, EPG y cambio real entre canales.
