package com.example.myapplication.LLM;


public interface LLMStrategy {
    void askQuestion(String question, LLMResponseCallback callback);
}
