package com.handstandtech.timetap.task;

public interface AsyncTaskCallback<T> {
	public void onTaskComplete(T result);
}