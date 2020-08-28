import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _MyAppState();
  }
}

class _MyAppState extends State<MyApp> {
  static const platform = const MethodChannel('samples.flutter.dev/battery');

// Get battery level.
  String _batteryLevel = 'Unknown battery level.';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await platform.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }
    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  Future<void> _openSetting() async {
    try {
      await platform.invokeMethod('openSetting');
    } on PlatformException catch (e) {}
  }

  Future<void> _openFloatingWindow() async {
    try {
      await platform.invokeMethod('openFloatingWindow');
    } on PlatformException catch (e) {}
  }

  Future<void> _changeText() async {
    try {
      await platform.invokeMethod('changeText');
    } on PlatformException catch (e) {}
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            RaisedButton(
              child: Text(
                'Get Battery Level',
                textDirection: TextDirection.ltr,
              ),
              onPressed: _getBatteryLevel,
            ),
            RaisedButton(
              child: Text(
                'Open Settings',
                textDirection: TextDirection.ltr,
              ),
              onPressed: _openSetting,
            ),
            RaisedButton(
              child: Text(
                'Open FloatingWindow',
                textDirection: TextDirection.ltr,
              ),
              onPressed: _openFloatingWindow,
            ),
            RaisedButton(
              child: Text(
                'Change Text',
                textDirection: TextDirection.ltr,
              ),
              onPressed: _changeText,
            ),
            Text(_batteryLevel, textDirection: TextDirection.ltr),
          ],
        ),
      ),
    );
  }
}
