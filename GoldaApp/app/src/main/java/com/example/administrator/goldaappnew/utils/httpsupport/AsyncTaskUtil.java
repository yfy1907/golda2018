package com.example.administrator.goldaappnew.utils.httpsupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class AsyncTaskUtil {

    //private static final String TAG = AsyncTaskUtil.class.getSimpleName();

    private static AsyncTaskUtil mInstance;

    private Map<WeakReference<AsyncTaskOwner>, List<WeakReference<CommonAsyncTask>>> ownerMap;

    private AsyncTaskUtil(){
//		Log.d(TAG, "create AsyncTaskUtil");
        ownerMap = new HashMap<WeakReference<AsyncTaskOwner>, List<WeakReference<CommonAsyncTask>>>();
    }

    public static AsyncTaskUtil getInstance(){
        if (mInstance == null) {
            mInstance = new AsyncTaskUtil();
        }
        return mInstance;
    }

    /**
     * 执行异步线程
     * @param callable 异步执行方法
     * @param callback 异步回调方法
     * @param owner 异步任务所有者
     */
    public <T> void execute(final Callable<T> callable, final Callback<T> callback, AsyncTaskOwner owner){

        CommonAsyncTask<Void, Void, T, AsyncTaskOwner> asyncTask = new CommonAsyncTask<Void, Void, T, AsyncTaskOwner>(owner) {

            @Override
            public void onPreExecute(AsyncTaskOwner owner) {
                super.onPreExecute(owner);
            }

            @Override
            public T doInBackground(AsyncTaskOwner owner, Void... params) {
                if (callable != null) {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }

            @Override
            public void onPostExecute(AsyncTaskOwner owner, T result) {
                if (callback != null) {
                    callback.onCallback(result);
                }
            }

            @Override
            public void onTaskComplete(AsyncTaskOwner owner) {
                super.onTaskComplete(owner);
//				Log.d(TAG, "线程执行 onTaskComplete");
            }
        };
        //Log.d(TAG, "add task ---->" + owner);
        getTaskCache(owner).add(new WeakReference<CommonAsyncTask>(asyncTask));
        asyncTask.execute();
    }

    public void cancelAllTask(AsyncTaskOwner owner){
//		Log.d(TAG, "cancelAllTask");
        if (owner == null || ownerMap == null) return;
        List<WeakReference<CommonAsyncTask>> taskWorkInOwner = getTaskCache(owner);
        if (taskWorkInOwner != null) {
            for (WeakReference<CommonAsyncTask> taskRef : taskWorkInOwner) {
                CommonAsyncTask task = taskRef.get();
                if (task != null) {
                    task.cancel(true);
                    //Log.d(TAG, "控制器销毁，停止线程");
                }
            }

            for (WeakReference<AsyncTaskOwner> key : ownerMap.keySet()){
                if (key != null && key.get() == owner) {
                    ownerMap.remove(key);
                    //Log.d(TAG, "移除一个owner --->" + owner.toString());
                    break;
                }
            }
        }
    }

    private List<WeakReference<CommonAsyncTask>> getTaskCache(AsyncTaskOwner owner){
        List<WeakReference<CommonAsyncTask>> taskWorkInOwner = null;
        Set<WeakReference<AsyncTaskOwner>> set = ownerMap.keySet();
        for (WeakReference<AsyncTaskOwner> key : set){
            if (key != null && key.get() == owner){
                taskWorkInOwner = ownerMap.get(key);
                break;
            }
        }
        if (taskWorkInOwner == null) {
            taskWorkInOwner = new ArrayList<WeakReference<CommonAsyncTask>>();
            ownerMap.put(new WeakReference<AsyncTaskOwner>(owner), taskWorkInOwner);
        }
        return taskWorkInOwner;
    }
}