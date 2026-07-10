# CineLink Monitor v0.1.3

Android 16 emulator compatibility release for the Sony A7C II USB-C monitor app.

## Fixed

- Prevented an Android 16 x86_64 startup crash caused by eagerly loading AUSBC native UVC libraries that are bundled only for ARM Android devices.
- Native UVC debug initialization now follows the process primary ABI, including mixed x86_64/translated-arm emulator ABI lists.
- ARM64 Pixel devices keep the Sony USB UVC camera path unchanged.

## Verification

- Android Studio Pixel 6 Pro AVD: Android 16 / API 36 / x86_64.
- AVD display: 1440x3120 at 560 dpi; app screenshot: 3120x1440 landscape.
- Cold start: passed; app remained the top resumed activity with a live process.
- Video/photo mode switch: passed.
- Anamorphic preview switch from 1.33x to 1.50x: passed.
- `testDebugUnitTest`, `lintDebug`, and `assembleDebug`: passed.

## Known Limitation

- Android Emulator does not validate the Pixel USB-host-to-Sony UVC hardware path. Live A7C II video still requires an ARM Android phone connected directly to the camera.
