package it.developing.ico2k2.luckyplayer.tasks;

import android.os.AsyncTask;

public class AsyncThread extends AsyncTask<Object,Object,Object>
{
    private AsyncThreadCallbacks callbacks;

    public interface AsyncThreadBaseCallbacks
    {
        void onPreExecute();
        void onProgressUpdate(Object... progress);
        void onPostExecute(Object result);
    }

    public interface AsyncThreadCallbacks extends AsyncThreadBaseCallbacks
    {
        Object doInBackground(Object... objects);
    }

    public AsyncThread(AsyncThreadCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    protected Object doInBackground(Object... objects)
    {
        return callbacks.doInBackground(objects);
    }

    @Override
    protected void onPreExecute()
    {
        callbacks.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Object... progress)
    {
        callbacks.onProgressUpdate((Object[])progress);
    }

    @Override
    protected void onPostExecute(Object result)
    {
        callbacks.onPostExecute(result);
    }

    public void publishUpdate(Object... values)
    {
        publishProgress(values);
    }
}
