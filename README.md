# CineLink Monitor

Android USB-C monitoring app for Sony A7C II USB Streaming workflows, tuned for Google Pixel 6 Pro.

## Features

- UVC live monitoring through Sony A7C II `USB Streaming` / `Live Stream (USB Streaming)`.
- Video and photo monitor UI modes.
- 1.00x, 1.33x, 1.50x, and 1.80x desqueeze display for anamorphic monitoring.
- `.cube` LUT import with enable/disable and intensity control.
- Fullscreen dark monitor UI with connection guidance and status.
- Debug-signed APK build for direct sideload testing.

## Camera Setup

1. On the A7C II, set USB mode to `USB Streaming` or start `Live Stream (USB Streaming)`.
2. Connect the camera to the Pixel 6 Pro with a USB-C data cable.
3. Open CineLink Monitor and approve the USB device permission prompt.
4. Use the bottom controls to switch monitor mode, desqueeze, and LUT preview.

## Current Limitations

- v0.1 uses the A7C II UVC/USB Streaming path. Sony documents this path as movie-recording-mode streaming, so true still-photo remote live view/control is left for a future Sony Remote Command/PTP module.
- LUT import parses `.cube` files and applies an OpenGL preview lift derived from the LUT profile. Full 3D LUT texture sampling is planned after hardware validation.
- This build has been verified by local unit tests, lint, and debug APK assembly. Pixel 6 Pro + A7C II hardware validation requires both devices to be connected.

## Build

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME='C:\Users\zongrui\AppData\Local\Android\Sdk'
$env:GRADLE_USER_HOME='C:\Users\zongrui\.gradle'
.\gradlew.bat --no-daemon --console=plain testDebugUnitTest
.\gradlew.bat --no-daemon --console=plain lintDebug
.\gradlew.bat --no-daemon --console=plain assembleDebug
```

