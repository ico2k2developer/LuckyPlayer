package it.developing.ico2k2.luckyplayer.tasks;

import androidx.annotation.Nullable;

public interface ThreadCallback
{
	public void onThreadCreated();
	public void onProgressResult(Object result);
	public void onThreadEnded(@Nullable Object results);
}
