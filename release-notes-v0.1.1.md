# CineLink Monitor v0.1.1

Patch release for the initial Sony A7C II USB-C UVC monitoring APK.

## Fixed

- Fixed a Pixel 6 Pro launch crash caused by setting a background on `TextureView`.
- Forced the monitor Activity into landscape orientation and added runtime landscape enforcement.
- Reworked the monitor surface into a landscape-first UI with the live preview on the left and controls in a right-side rail.
- Added a keep-screen-on flag for long monitoring sessions.

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
- Pixel 6 Pro install and launch: passed, process remained alive after launch and logcat showed no `FATAL EXCEPTION`.
- Pixel 6 Pro Activity orientation: `SCREEN_ORIENTATION_SENSOR_LANDSCAPE` reported by ActivityManager.
- Sony A7C II USB Streaming hardware feed: not yet verified in this session.

## Known Limitations

- Uses Sony A7C II USB Streaming/UVC, which Sony documents as movie recording mode. Still-photo live view/control needs a later Sony Remote Command/PTP module.
- LUT preview is an OpenGL approximation derived from the imported `.cube`; full 3D LUT sampling is not implemented in v0.1.
- AUSBC native libraries trigger Android 16 KB page-size compatibility warnings on the Pixel 6 Pro beta build, but the app installs and launches.
