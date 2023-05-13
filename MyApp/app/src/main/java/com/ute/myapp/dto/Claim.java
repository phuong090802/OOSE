package com.ute.myapp.dto;

import java.io.Serializable;

public class Claim implements Serializable {
    private final String OTP;
    private final long time;

    public Claim(String OTP, long time) {
        this.OTP = OTP;
        this.time = time;
    }

    public String getOTP() {
        return OTP;
    }

    public long getTime() {
        return time;
    }
}
