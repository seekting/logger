package com.seekting.logger;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/13.
 */

public class TimingLoggers {


    private static Map<String, ArrayList<Split>> mSplits = new HashMap<>();

    private static Map<String, String> mTagsTable = new HashMap<>();

    private static class Split {
        long time;
        String label;

        public Split(String label) {
            this.label = label;
            time = SystemClock.elapsedRealtime();
        }
    }

    public static void begin(String tag, String label) {
        ArrayList<Split> list = null;
        if (mSplits.containsKey(label)) {
            list = mSplits.get(label);
            list.clear();

        } else {
            list = new ArrayList<>();
            mSplits.put(label, list);
        }
        mTagsTable.put(label, tag);
        list.add(new Split("begin"));

    }

    public static void begin(String label) {
        begin(label, label);
    }


    public static void addSplit(String splitLabel, String subSplitLabel) {
        if (mSplits.containsKey(splitLabel)) {
            ArrayList<Split> list = mSplits.get(splitLabel);
            list.add(new Split(subSplitLabel));

        } else {
            throw new IllegalAccessError("have you invoked begin(" + splitLabel + ") method?");
        }
    }

    public static void dumpToLog(String splitLabel) {
        if (mSplits.containsKey(splitLabel)) {
            String tag = mTagsTable.get(splitLabel);
            Log.d(tag, splitLabel + ": begin");
            final ArrayList<Split> splits = mSplits.get(splitLabel);
            Split split0 = splits.get(0);
            long first = split0.time;
            long now = first;
            for (int i = 1; i < splits.size(); i++) {
                Split split = splits.get(i);
                now = split.time;
                final long prev = splits.get(i - 1).time;
                Log.d(tag, splitLabel + ":      " + (now - prev) + " ms, " + split.label);
            }
            Log.d(tag, splitLabel + ": end, " + (now - first) + " ms");
            splits.clear();
            mSplits.remove(splitLabel);
            mTagsTable.remove(splitLabel);
        } else {
            throw new IllegalAccessError("have you invoked begin(" + splitLabel + ") method?");
        }

    }
}
