package com.yahir.ratelimitedapi;


import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimiterTest {

    @Test
    void testAllowsRequestsUnderLimit() {
        RateLimiter rl = new RateLimiter();
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
    }

    @Test
    void testBlocksAfterLimit() {
        RateLimiter rl = new RateLimiter();
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertFalse(rl.allow("abc"));
    }

    @Test
    void testTracksClientsIndependently() {
        RateLimiter rl = new RateLimiter();
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertFalse(rl.allow("abc"));
        assertTrue(rl.allow("xyz"));
    }

    @Test
    void testReturnsFalseIfNull() {
        RateLimiter rl = new RateLimiter();
        assertFalse(rl.allow(null));
    }

    @Test
    void testTimeWindowResets() {
        Supplier<Long> clock = () -> 0L;
        RateLimiter rl = new RateLimiter(clock);
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertFalse(rl.allow("abc"));
        Supplier<Long> clock2 = () -> 60_001L;
        rl.setClock(clock2);
        assertTrue(rl.allow("abc"));
    }

    @Test
    void testTimeWindowBlocksForRequestsLessThanTime() {
        Supplier<Long> clock = () -> 0L;
        RateLimiter rl = new RateLimiter(clock);
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertTrue(rl.allow("abc"));
        assertFalse(rl.allow("abc"));
        Supplier<Long> clock2 = () -> 30_001L;
        rl.setClock(clock2);
        assertFalse(rl.allow("abc"));
        Supplier<Long> clock3 = () -> 70_001L;
        rl.setClock(clock3);
        assertTrue(rl.allow("abc"));
    }

}
