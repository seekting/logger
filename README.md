# Wellcome to LoggerLib

## gradle dependencies

```gradle
compile 'com.seekting:loggerlib:1.0.1'

```

## init in Application
```java

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        File dir = Environment.getExternalStorageDirectory();
        //this is log file dir
        File myDir = new File(dir, "logger_demo");

        int pid = android.os.Process.myPid();
        String processName = "processName";

        LoggerEvent loggerEvent = new LoggerEvent() {
            @Override
            public void onPreWrite(File f, String tag, String msg) {
                Log.d("seekting", "App.onPreWrite()" + f + msg);


            }

            @Override
            public void onWrite(File f, String tag, String msg) {

            }

            @Override
            public void onPostWrite(File f, String tag, String msg) {

            }

            @Override
            public void onException(File f, Throwable t) {

                Log.d("seekting", "App.onException()", t);

            }


            @Override
            public void onWriteText(File file, String text) {

            }

            @Override
            public void onRecordLogcat(String realFileName) {

            }

            @Override
            public void onClear(String s) {
                Log.d("seekting", "App.onClear()" + s);
            }
        };
        LoggerOutputUtil.init(myDir.getAbsolutePath(), pid + "", processName, loggerEvent);


    }


```
## logEvent

```java
LoggerOutputUtil.d(TAG, "hello world!");

```

## dumpLogcat

```java
LoggerOutputUtil.recordLogcat("test");
```

## pull log file from your device

you can save log in customer dir by LoggerOutputUtil.init method,but only invoke once in process lifecycle

and log file will save in customer dir.you can cmd:
```java
adb pull customer dir
```
 get your log files.


 ## TimingLoggers

You can analyze the wast time each operate, by TimingLoggers
 ```java
         TimingLoggers.begin("seekting", "onCreate");
         TimingLoggers.addSplit("onCreate", "1");
         SystemClock.sleep(120);
         TimingLoggers.addSplit("onCreate", "2");
         SystemClock.sleep(14);
         TimingLoggers.addSplit("onCreate", "3");
         SystemClock.sleep(12);
         TimingLoggers.addSplit("onCreate", "4");
         SystemClock.sleep(11);
         TimingLoggers.dumpToLog("onCreate");

 ```


 ```
09-14 21:13:47.747 9614-9614/com.example.logger:ui D/seekting: onCreate: begin
09-14 21:13:47.747 9614-9614/com.example.logger:ui D/seekting: onCreate:      0 ms, 1
09-14 21:13:47.747 9614-9614/com.example.logger:ui D/seekting: onCreate:      120 ms, 2
09-14 21:13:47.747 9614-9614/com.example.logger:ui D/seekting: onCreate:      15 ms, 3
09-14 21:13:47.747 9614-9614/com.example.logger:ui D/seekting: onCreate:      12 ms, 4
09-14 21:13:47.747 9614-9614/com.example.logger:ui D/seekting: onCreate: end, 147 ms

 ```