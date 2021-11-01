package it.developing.ico2k2.luckyplayer.tasks;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncTask<P,R>
{

    public interface OnStart {
        void onStart();
    }

    public interface OnCall<P,R> {
        R call(@NonNull PublishProgress<P> progress) throws Exception;
    }

    public interface OnProgress<P> {
        void onProgress(@NonNull P progress);
    }

    public interface PublishProgress<P> {
        void publishProgress(@NonNull P progress);
    }

    public interface OnFinish<R> {
        void onComplete(@Nullable R result);
    }

    private final Executor executor;
    private final Handler handler;
    private InternalHandler internalHandler;
    private OnProgress<P> progress;

    public AsyncTask()
    {
        executor = Executors.newSingleThreadExecutor();
        handler = new InternalHandler(Looper.getMainLooper(),this);
    }

    public void executeProgressAsync(@Nullable OnStart start, OnCall<P,R> callable,@NonNull OnProgress<P> progress,@NonNull OnFinish<R> stop)
    {
        if (start != null)
            start.onStart();
        this.progress = progress;
        executor.execute(() -> {
            R result = null;
            try {
                result = callable.call(new PublishProgress<P>() {
                    @Override
                    public void publishProgress(@NonNull P progress) {
                        AsyncTask.this.publishProgress(progress);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            R finalResult = result;
            handler.post(() -> stop.onComplete(finalResult));
        });
    }

    private static final int MESSAGE_PROGRESS = 0x10;

    private static class InternalHandler extends Handler
    {
        private final WeakReference<AsyncTask> reference;
        public InternalHandler(Looper looper,AsyncTask task) {
            super(looper);
            reference = new WeakReference<>(task);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_PROGRESS:
                {
                    reference.get().progress.onProgress(msg.obj);
                    break;
                }
            }
        }
    }

    private void publishProgress(@NonNull P progress)
    {
        handler.obtainMessage(MESSAGE_PROGRESS,progress).sendToTarget();
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
            handler.post(() -> stop.onComplete(finalResult));
        });
    }
}