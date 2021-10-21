package it.developing.ico2k2.luckyplayer.tasks;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncTask<R> {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface OnStart {
        void onStart();
    }

    public interface OnFinish<R> {
        void onComplete(@Nullable R result);
    }

    public void executeAsync(@Nullable OnStart start, Callable<R> callable, @NonNull OnFinish<R> stop) {
        if (start != null)
            start.onStart();
        executor.execute(() -> {
            R result = null;
            try {
                result = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            R finalResult = result;
            handler.post(() -> {
                stop.onComplete(finalResult);
            });
        });
    }
}