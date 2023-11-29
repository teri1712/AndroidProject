package com.example.socialmediaapp.application.session;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

public class SessionHandler {
    private Executor sessionExecutor = ApplicationContainer.getInstance().dataLayerExecutor;
    protected boolean isInvalidated;
    protected boolean isInterrupted;

    protected SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private String state;
    private Integer id;
    private boolean retain;

    public Integer getId() {
        return id;
    }

    public SessionHandler() {
        sessionRegistry = new SessionRegistry();
        sessionState = new MutableLiveData<>();
        isInvalidated = false;
        isInterrupted = false;
        retain = false;
        state = "null";
    }

    protected void init() {
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public SessionHandler.SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    protected void clean() {
        sessionRegistry.clear();
    }

    protected void interrupt() {
        sessionState.postValue("interrupted");
        SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
        for (Integer i : sessionRegistry.branches) {
            sessionRepository.getSession(i).interrupt();
        }
    }

    protected void resume() {
        sessionState.postValue("resume");
        state = "resume";
        SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
        for (Integer i : sessionRegistry.branches) {
            sessionRepository.getSession(i).resume();
        }
    }

    protected void invalidate() {
        clean();
        isInvalidated = true;
        sessionExecutor = null;
        setState("invalidated");
    }

    public void setRetain(final boolean retain) {
        post(() -> {
            if (!SessionHandler.this.retain) {
                SessionHandler.this.retain = retain;
            }
        });
    }
    private void setState(String state) {
        this.state = state;
        sessionState.postValue(state);
    }

    protected void post(Runnable action) {
        Executor executor = sessionExecutor;
        if (executor == null) return;
        executor.execute(() -> {
            if (isInvalidated || isInterrupted) return;
            action.run();
        });
    }
    public class SessionRegistry {
        private Set<Integer> branches;
        private SessionRegistry() {
            branches = new HashSet<>();
        }
        protected <T extends SessionHandler> Integer bind(T t) {
            SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;

            SessionHandler s = t;
            Integer sessionId = sessionRepository.createSession(s);
            s.id = sessionId;
            s.init();
            branches.add(sessionId);
            s.setState("started");
            return sessionId;
        }

        public <T extends SessionHandler> MutableLiveData<Integer> bindSession(T t) {
            MutableLiveData<Integer> callBack = new MutableLiveData<>();
            post(() -> callBack.postValue(bind(t)));
            return callBack;
        }

        public void unBindSession(Integer sessionId) {
            post(() -> {
                SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
                branches.remove(sessionId);
                SessionHandler t = sessionRepository.removeSession(sessionId);
                t.invalidate();
            });
        }

        private void clear() {
            SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
            for (Integer i : branches) {
                SessionHandler t = sessionRepository.getSession(i);
                if (t.retain) {
                    t.setState("detached");
                } else {
                    sessionRepository.removeSession(i);
                    t.invalidate();
                }
            }
            branches.clear();
        }
    }

    public class SessionRepository {
        private HashMap<Integer, SessionHandler> sessionHandlerHashMap;
        private int cnt;

        protected SessionRepository() {
            sessionHandlerHashMap = new HashMap<>();
            cnt++;
        }

        public MutableLiveData<SessionHandler> getSessionById(Integer sessionId) {
            assert sessionId != null;
            MutableLiveData<SessionHandler> callBack = new MutableLiveData<>();
            sessionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    callBack.postValue(sessionHandlerHashMap.get(sessionId));
                }
            });
            return callBack;
        }

        protected SessionHandler getSession(Integer sessionId) {
            return sessionHandlerHashMap.get(sessionId);
        }

        protected Integer createSession(SessionHandler sessionHandler) {
            Integer id = cnt++;
            sessionHandlerHashMap.put(id, sessionHandler);
            return id;
        }

        protected SessionHandler removeSession(Integer id) {
            return sessionHandlerHashMap.remove(id);
        }
    }

}
