package com.example.socialmediaapp.container.session;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SessionHandler {
    private Executor sessionExecutor = ApplicationContainer.getInstance().dataLayerExecutor;
    protected boolean isInvalidated;
    protected boolean isInterrupted;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private String state;
    private Integer id;
    private boolean isActive;
    private SessionRegistry parentSessionRegistry;

    public SessionHandler() {
        sessionRegistry = new SessionRegistry();
        sessionState = new MutableLiveData<>();
        isInvalidated = false;
        isInterrupted = false;
        isActive = false;
        state = "null";
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
        setState("invalidated");
    }

    protected void interrupt() {
        sessionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sessionState.postValue("interrupted");
                for (Map.Entry<Integer, SessionHandler> e : sessionRegistry.branches.entrySet()) {
                    e.getValue().interrupt();
                }
            }
        });
    }

    protected void resume() {
        sessionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sessionState.postValue("resume");
                state = "resume";
                for (Map.Entry<Integer, SessionHandler> e : sessionRegistry.branches.entrySet()) {
                    e.getValue().resume();
                }
            }
        });
    }

    protected void invalidate() {
        clean();
    }

    public void setActive() {
        post(new Runnable() {
            @Override
            public void run() {
                if (!isActive) {
                    isActive = true;
                }
            }
        });
    }

    public void setInActive() {
        post(new Runnable() {
            @Override
            public void run() {
                if (isActive) {
                    isActive = false;
                    if (state == "detached") clean();
                }
            }
        });
    }

    private void setState(String state) {
        this.state = state;
        sessionState.postValue(state);
    }

    public void delete() {
        if (parentSessionRegistry != null) {
            parentSessionRegistry.unRegister(id);
        }
    }

    protected void post(Runnable action) {
        Executor executor = sessionExecutor;
        if (executor == null) return;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (isInvalidated || isInterrupted) return;
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
        private HashMap<Integer, SessionHandler> branches;

        private SessionRegistry() {
            branches = new HashMap<>();
        }

        public <T extends SessionHandler> MutableLiveData<Integer> register(T t) {
            MutableLiveData<Integer> callBack = new MutableLiveData<>();
            sessionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SessionHandler s = t;
                    s.parentSessionRegistry = SessionRegistry.this;
                    s.setState("started");
                    OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;
                    Integer sessionId = onlineSessionHandler.createSession(s);
                    callBack.postValue(sessionId);
                    s.id = sessionId;
                    branches.put(sessionId, s);
                }
            });
            return callBack;
        }

        public void unRegister(Integer sessionId) {
            sessionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;
                    branches.remove(sessionId);
                    onlineSessionHandler.removeSessionId(sessionId);
                    SessionHandler t = onlineSessionHandler.removeSessionId(sessionId);
                    t.invalidate();
                }
            });
        }

        private void clear() {
            OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;
            for (Map.Entry<Integer, SessionHandler> e : branches.entrySet()) {
                SessionHandler t = e.getValue();
                if (t.state != "active") {
                    t.state = "detached";
                    t.sessionState.postValue("detached");
                } else {
                    onlineSessionHandler.removeSessionId(e.getKey());
                    t.invalidate();
                }
            }
            branches.clear();
        }
    }
}
