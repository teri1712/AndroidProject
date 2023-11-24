package com.example.socialmediaapp.container.session;

import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SessionHandler {
    private Executor sessionExecutor = Executors.newSingleThreadExecutor();
    private boolean isInvalidated;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;

    public SessionHandler() {
        sessionRegistry = new SessionRegistry();
        sessionState = new MutableLiveData<>();
        isInvalidated = false;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public SessionHandler.SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    protected void clean() {
        isInvalidated = true;
        sessionRegistry.clear();
        sessionExecutor = null;
        sessionState.postValue("Invalidated");
    }

    public void interrupt() {
    }

    protected Future<String> invalidate() {
        return ((ExecutorService) sessionExecutor).submit(new Callable<String>() {
            @Override
            public String call() {
                if (isInvalidated) return "Invalidated";
                clean();
                return "Finished";
            }
        });
    }

    protected void post(Runnable action) {
        Executor executor = sessionExecutor;
        if (executor == null) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (isInvalidated) return;
                action.run();
            }
        });
    }

    protected void addFutureCallBack(ListenableFuture<?> future, Runnable action) {
        future.addListener(new Runnable() {
            @Override
            public void run() {
                if (isInvalidated) return;
                action.run();
            }
        }, sessionExecutor);
    }

    public class SessionRegistry {
        private HashSet<SessionHandler> branches;

        private SessionRegistry() {
            branches = new HashSet<>();
        }

        public <T extends SessionHandler> MutableLiveData<T> register(T t) {
            MutableLiveData<T> callBack = new MutableLiveData<>();
            sessionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SessionHandler s = t;
                    s.sessionExecutor = sessionExecutor;
                    callBack.postValue(t);
                }
            });
            return callBack;
        }

        public MutableLiveData<String> unregister(SessionHandler t) {
            MutableLiveData<String> callBack = new MutableLiveData<>();
            sessionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!branches.contains(t)) {
                        callBack.postValue("Session does not exist");
                        return;
                    }
                    String status = "error";
                    try {
                        status = t.invalidate().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    callBack.postValue(status);
                }
            });
            return callBack;
        }

        private void clear() {
            for (SessionHandler e : branches) {
                try {
                    String status = e.invalidate().get();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            branches.clear();
        }
    }
}
