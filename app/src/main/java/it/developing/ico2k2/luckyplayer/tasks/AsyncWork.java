package it.developing.ico2k2.luckyplayer.tasks;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncWork {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface OnStart {
        void onStart();
    }

    public interface OnFinish {
        void onComplete();
    }

    public void executeAsync(@Nullable OnStart start, Callable<Void> callable, @Nullable OnFinish stop) {
        if(start != null)
            start.onStart();
        executor.execute(() -> {
            try
            {
                callable.call();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if(stop != null)
            {
                handler.post(stop::onComplete);
            }
        });
    }
}