# CineLink Monitor v0.1.0

Initial debug APK for Sony A7C II USB-C UVC monitoring on Android.

## Included

- Live monitor shell using AUSBC/UVC.
- Fullscreen monitor UI.
- Video/photo monitor modes.
- 1.33x anamorphic desqueeze and additional preview stretch modes.
- `.cube` LUT import, enable/disable, and intensity control.
- Original adaptive launcher icon and standalone SVG logo.

## Verification

- `testDebugUnitTest`: passed.
- `lintDebug`: passed.
- `assembleDebug`: passed.
- `adb devices -l`: no Android device attached in this session, so Pixel 6 Pro installation and A7C II hardware streaming are not yet verified.

## Known Limitations

- Uses Sony A7C II USB Streaming/UVC, which Sony documents as movie recording mode. Still-photo live view/control needs a later Sony Remote Command/PTP module.
- LUT preview is an OpenGL approximation derived from the imported `.cube`; full 3D LUT sampling is not implemented in v0.1.

