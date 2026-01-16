package com.yahir.ratelimitedapi;

import com.yahir.ratelimitedapi.controller.PingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
public class MyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimiter rateLimiter;

    @Test
    void missingAPIKeyReturns400() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error: missing API key"));
    }

    @Test
    void allowedAPIKeyReturns200() throws Exception {
        when(rateLimiter.allow("abc")).thenReturn(true);
        mockMvc.perform(get("/ping").header("X-API-KEY", "abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void rateLimiterReturns429() throws Exception {
        when(rateLimiter.allow("abc")).thenReturn(false);
        mockMvc.perform(get("/ping").header("X-API-KEY", "abc"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("error: rate limit exceeded"));
    }
}
