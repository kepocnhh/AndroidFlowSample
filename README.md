#### Android kotlinx.coroutines.flow sample.

Build:
```
$ gradle app:assembleDebug
```

Install and run:
```
$ adb install app/build/outputs/apk/debug/AndroidFlowSample-${VERSION_NAME}.apk
$ adb shell am start -n 'test.android.flow.debug/test.android.flow.MainActivity'
```
