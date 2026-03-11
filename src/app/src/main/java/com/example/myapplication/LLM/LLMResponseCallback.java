package com.example.myapplication.LLM;


public interface LLMResponseCallback {
    void onSuccess(String response);
    void onError(String error);
}
