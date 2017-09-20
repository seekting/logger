package com.example.logger;

import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.seekting.logger.DateUtil;
import com.seekting.logger.TimingLoggers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {


    public static final String TAG = "MainActivity1";
    public static final int INT_TIME = 20;
    public static final int INT_TIMES = 1000;
    public static String sss = "protected void onCreate(Bundle savedInstanceState),protected void onCreate(Bundle savedInstanceState),protected void onCreate(Bundle savedInstanceState),protected void onCreate(Bundle savedInstanceState),protected void onCreate(Bundle savedInstanceState)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        setContentView(R.layout.demo_layout);


        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.clear();
            }
        });
        findViewById(R.id.begin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyService.startService(MainActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < INT_TIMES; i++) {
                            String str = DateUtil.format();
                            try {
                                Date k = DateUtil.parse(str);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

//                            Log.d("seekting", "MainActivity.run1()" + str);

//                            SystemClock.sleep((long) (Math.random() * INT_TIME));
                        }
                        Log.d("seekting", "MainActivity.run() over");

                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < INT_TIMES; i++) {
                            String str = DateUtil.format();
                            try {
                                Date k = DateUtil.parse(str);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            SystemClock.sleep((long) (Math.random() * INT_TIME));
                        }
                        Log.d("seekting", "MainActivity.run()1 over");
                    }
                }).start();
            }
        });

        findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> ui = new ArrayList<String>();
                List<String> p = new ArrayList<String>();
                BufferedReader reader = null;
                File dir = Environment.getExternalStorageDirectory();
                File myDir = new File(dir, "logger_demo");
                File[] files = myDir.listFiles();

                try {
                    reader = new BufferedReader(new FileReader(files[0]));

                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        if (line.contains("com.example.logger:ui D/MainActivity: i=")) {
                            ui.add(line);

                        } else if (line.contains("com.example.logger D/MyService: i=")) {
                            p.add(line);
                        }

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("seekting", "MainActivity.onClick()" + ui.size());
                Log.d("seekting", "MainActivity.onClick()" + p.size());
                check("ui", ui);
                check("p", p);
            }
        });

    }


    private void check(String str, List<String> p) {
        for (int i = 0; i < p.size(); i++) {
            if (!p.get(i).contains("i=" + i)) {

                Log.e("seekting", str + "check()i=" + i + " error!");
                break;
            }
        }
        Log.d("seekting", "MainActivity.check() check suc!");
    }

}
