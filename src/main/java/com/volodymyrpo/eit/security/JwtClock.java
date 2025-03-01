package com.volodymyrpo.eit.security;

import io.jsonwebtoken.Clock;

import java.util.Date;

public class JwtClock implements Clock {
    @Override
    public Date now() {
        // Return a custom or fixed date/time
        return new Date();
    }
}
