package com.example.administrator.goldaappnew.utils.httpsupport;

import java.lang.ref.WeakReference;

import android.os.AsyncTask;

@SuppressWarnings("hiding")
public abstract class CommonAsyncTask<Params, Progress, Result, AsyncTaskOwner> extends AsyncTask<Params, Progress, Result> {

    private WeakReference<AsyncTaskOwner> asyncTaskReference;

    public CommonAsyncTask(AsyncTaskOwner owner){
        asyncTaskReference = new WeakReference<AsyncTaskOwner>(owner);
    }

    @Override
    public void onPreExecute() {
        final AsyncTaskOwner owner = asyncTaskReference.get();
        if (owner != null) onPreExecute(owner);
    }

    @Override
    public Result doInBackground(Params... params) {
        final AsyncTaskOwner owner = asyncTaskReference.get();
        if(owner != null){
            return doInBackground(owner, params);
        }
        return null;
    }

    @Override
    public void onPostExecute(Result result) {
        final AsyncTaskOwner owner = asyncTaskReference.get();
        if (owner != null) {
            onPostExecute(owner, result);
            onTaskComplete(owner);
        }
    }

    @Override
    public void onCancelled() {
        super.onCancelled();
        final AsyncTaskOwner owner = asyncTaskReference.get();
        if (owner != null) {
            onTaskComplete(owner);
        }
    }

    public void onPreExecute(AsyncTaskOwner owner){}

    public abstract Result doInBackground(AsyncTaskOwner owner, Params... params);

    public void onPostExecute(AsyncTaskOwner owner, Result result){}

    public void onTaskComplete(AsyncTaskOwner owner){};
}
