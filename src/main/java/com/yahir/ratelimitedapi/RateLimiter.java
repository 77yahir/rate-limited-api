package com.yahir.ratelimitedapi;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RateLimiter {

    private final Map<String, Deque<Long>> requestsTimesByKey = new HashMap<>();
    private final int MAX_REQUESTS = 5;
    private final long WINDOW_MS = 60_000;
    private Supplier<Long> clock = System::currentTimeMillis;

    public RateLimiter() {}

    public RateLimiter(Supplier<Long> clock) {
        this.clock = clock;
    }

    public boolean allow(String key) {
        if (key == null) return false;

        long now = clock.get();

        if (!requestsTimesByKey.containsKey(key)) {
            Deque<Long> dqList = new ArrayDeque<>();
            dqList.add(now);
            requestsTimesByKey.put(key, dqList);
            return true;
        }

        Deque<Long> dq = requestsTimesByKey.get(key);

        while (!(dq.peekLast() == null) && now - dq.peekLast() >= WINDOW_MS) {
            dq.removeLast();
        }

        if (dq.size() < MAX_REQUESTS) {
            dq.addFirst(now);
            return true;
        }
        return false;
    }

    public void setClock(Supplier<Long> clock) {
        this.clock = clock;
    }
}
