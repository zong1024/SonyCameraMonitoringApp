# CineLink Monitor v0.1.2

Patch release for the Sony A7C II USB-C monitor preview path.

## Fixed

- Switched the camera backend from AUSBC's default phone-camera strategy to an explicit USB UVC strategy.
- Added the matching `libuvc` dependency so Sony A7C II USB Streaming devices are handled as external USB video sources.
- Removed unnecessary phone `CAMERA` and `RECORD_AUDIO` runtime permissions from the app manifest.
- Added USB attach, detach, authorization, and disconnect status messages to the landscape monitor UI.
- Set the UVC preview request to a 1280x720 USB Streaming-safe default.
- Lowered the sideload target SDK to 33 to avoid the AUSBC 3.2.7 legacy USB receiver crash on Android 14+ devices.

## Included

- Landscape-first live monitor UI.
- Sony A7C II USB Streaming/UVC monitor path.
- Video/photo monitor modes.
- 1.33x anamorphic desqueeze and additional preview stretch modes.
- `.cube` LUT import, enable/disable, and intensity control.
- Original adaptive launcher icon and standalone SVG logo.

## Verification

- `testDebugUnitTest`: passed.
- `lintDebug`: passed.
- `assembleDebug`: passed.
- Pixel 6 Pro install and launch: passed.
- Pixel 6 Pro process after launch: alive, with no `AndroidRuntime`, `FATAL EXCEPTION`, or receiver `SecurityException` in logcat.
- Installed package reported `versionName=0.1.2` and `targetSdk=33`.
- Sony A7C II hardware feed: code path is fixed for UVC, but live feed could not be re-verified while the Pixel was attached to the PC for ADB because the USB port was in device mode.

## Known Limitations

- Uses Sony A7C II USB Streaming/UVC, which Sony documents as a movie-recording streaming path. Still-photo remote live view/control needs a later Sony Remote Command/PTP module.
- LUT preview is an OpenGL approximation derived from the imported `.cube`; full 3D LUT texture sampling is not implemented in v0.1.
- AUSBC native libraries may still trigger Android 16 KB page-size compatibility warnings on the Pixel 6 Pro beta build.
