// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package library;

import android.os.AsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TaskUtils {

    public static void execute(final Runnable runnable) {
        execute(AsyncTask.THREAD_POOL_EXECUTOR, runnable);
    }

    public static <V> Future<V> submit(final Callable<V> callable) {
        return submit(AsyncTask.THREAD_POOL_EXECUTOR, callable);
    }

    public static <V> Future<V> submit(Executor executor, final Callable<V> callable) {
        return ((ExecutorService) executor).submit(callable);
    }

    public static <V> void submit(final FutureTask<V> task) {
        submit(AsyncTask.THREAD_POOL_EXECUTOR, task);
    }

    public static <V> void submit(Executor executor, final FutureTask<V> task) {
        ((ExecutorService) executor).submit(task);
    }

    public static void execute(final Executor executor, final Runnable runnable) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                runnable.run();
                return null;
            }
        }.executeOnExecutor(executor);
    }
}
