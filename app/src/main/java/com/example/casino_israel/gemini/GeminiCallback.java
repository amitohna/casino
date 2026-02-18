package com.example.casino_israel.gemini;

public interface GeminiCallback {
    public void onSuccess(String result);
    public void onError(Throwable error);

    void onError(Exception e);
}