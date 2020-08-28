# A Simple Flutter Floating Window Demo
之前没有用过 Flutter 和原生交互的东西，先练习了官网的 batterylevel sample（通过原生获取电量），然后增加了悬浮窗的功能

## Key Files
- `main.dart` flutter 布局文件，通过 MethodChannel 调用原生方法
- `MainActivity.kt` Android 主入口，通过 MethodChannel 与 Flutter 通讯
- `FloatingService.kt` 悬浮窗的 Service