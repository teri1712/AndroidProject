package com.example.socialmediaapp.application.session;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.ApplicationContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class SessionHandler {
    private Executor sessionExecutor = ApplicationContainer.getInstance().dataLayerExecutor;
    protected boolean isInvalidated;
    private Object mutex;
    private Integer cntQ;

    protected SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    protected String state;
    protected Integer id;
    private boolean retain;
    protected Executor worker;

    public Integer getId() {
        return id;
    }

    public SessionHandler() {
        sessionRegistry = new SessionRegistry();
        sessionState = new MutableLiveData<>();
        isInvalidated = false;
        retain = false;
        state = "null";
        mutex = new Object();
        cntQ = 0;
    }

    protected void init() {
        ArrayList<ExecutorService> workers = ApplicationContainer.getInstance().workers;
        worker = workers.get(id % workers.size());
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public SessionHandler.SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    protected void clean() {
    }

    protected void interrupt() {
        setState("interrupted");
        SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
        for (Integer i : sessionRegistry.branches) {
            sessionRepository.getSession(i).interrupt();
        }
    }

    protected void resume() {
        setState("resume");
        SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
        for (Integer i : sessionRegistry.branches) {
            sessionRepository.getSession(i).resume();
        }
    }

    protected void waitTillWorkerFinish() {
        synchronized (mutex) {
            while (cntQ != 0) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void invalidate() {
        waitTillWorkerFinish();
        isInvalidated = true;
        sessionRegistry.clear();
        sessionExecutor = null;
        setState("invalidated");
        worker.execute(this::clean);
    }

    public void setRetain(final boolean retain) {
        post(() -> {
            if (!SessionHandler.this.retain) {
                SessionHandler.this.retain = retain;
            }
        });
    }

    protected void postToWorker(Runnable runnable) {
        Runnable frunnable = () -> {
            runnable.run();
            synchronized (mutex) {
                cntQ--;
                mutex.notify();
            }
        };
        synchronized (mutex) {
            cntQ++;
        }
        worker.execute(frunnable);
    }

    protected void setState(String state) {
        this.state = state;
        sessionState.postValue(state);
    }

    protected void post(Runnable action) {
        Executor executor = sessionExecutor;
        if (executor == null) return;
        executor.execute(() -> {
            if (isInvalidated) return;
            action.run();
        });
    }


    public class SessionRegistry {
        private Set<Integer> branches;

        private SessionRegistry() {
            branches = new HashSet<>();
        }

        public <T extends SessionHandler> MutableLiveData<Integer> bindSession(T t) {
            MutableLiveData<Integer> callBack = new MutableLiveData<>();
            post(() -> callBack.postValue(bind(t)));
            return callBack;
        }

        protected <T extends SessionHandler> Integer bind(T t) {
            SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;

            SessionHandler s = t;
            Integer sessionId = sessionRepository.createSession(SessionHandler.this, s);
            branches.add(sessionId);

            s.id = sessionId;
            s.init();
            s.setState("started");

            return sessionId;
        }

        protected void unBind(Integer sessionId) {
            branches.remove(sessionId);
        }

        protected void clear() {
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
        private Map<Integer, SessionHandler> sessionHandlerHashMap;
        private Map<Integer, Integer> childParentMapping;
        private int cnt;

        protected SessionRepository() {
            sessionHandlerHashMap = new TreeMap<>();
            childParentMapping = new TreeMap<>();
            cnt++;
        }

        public MutableLiveData<SessionHandler> getSessionById(Integer sessionId) {
            assert sessionId != null;
            MutableLiveData<SessionHandler> callBack = new MutableLiveData<>();
            sessionExecutor.execute(() -> callBack.postValue(sessionHandlerHashMap.get(sessionId)));
            return callBack;
        }

        protected SessionHandler getSession(Integer sessionId) {
            return sessionHandlerHashMap.get(sessionId);
        }

        protected Integer createSession(SessionHandler parent, SessionHandler sessionHandler) {
            Integer id = cnt++;
            sessionHandlerHashMap.put(id, sessionHandler);
            childParentMapping.put(id, parent.getId());
            return id;
        }

        protected SessionHandler removeSession(Integer id) {
            return sessionHandlerHashMap.remove(id);
        }

        public void deleteSession(final Integer id) {
            sessionExecutor.execute(() -> {
                SessionHandler parent = getSession(childParentMapping.get(id));
                parent.sessionRegistry.unBind(id);
                SessionHandler t = removeSession(id);
                t.invalidate();
            });
        }

        public void deleteIfDetached(final Integer id) {
            sessionExecutor.execute(() -> {
                SessionHandler t = getSession(id);
                if (Objects.equals(t.state, "detached")) {
                    removeSession(id);
                    t.invalidate();
                }
            });
        }

    }
}
