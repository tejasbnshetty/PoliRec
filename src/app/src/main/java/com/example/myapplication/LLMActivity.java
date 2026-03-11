package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.LLM.HuggingFaceLLMStrategy;
import com.example.myapplication.LLM.LLMResponseCallback;
import com.example.myapplication.LLM.LLMStrategy;


public class LLMActivity extends AppCompatActivity {

    private LLMStrategy llmStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llmactivity);

        // Mapping UI
        EditText questionInput = findViewById(R.id.question_input);
        Button askButton = findViewById(R.id.ask_button);
        TextView resultView = findViewById(R.id.result_view);

        llmStrategy = (LLMStrategy) new HuggingFaceLLMStrategy(getApplicationContext());

        //Listener for controlling the button's UI
        questionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    askButton.setBackgroundTintList(ContextCompat.getColorStateList(LLMActivity.this, R.color.primary_blue));
                } else {
                    askButton.setBackgroundTintList(ContextCompat.getColorStateList(LLMActivity.this, R.color.light_blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Listener for submitting query - uses interfaces to interact with the code for connecting to LLM(maintains modularity - LLM can be switched out easily)
        askButton.setOnClickListener(v -> {
            String question = questionInput.getText().toString().trim();
            if (!question.isEmpty()) {
                resultView.setText("Loading...");
                llmStrategy.askQuestion(question, new LLMResponseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> resultView.setText(response));
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> resultView.setText("Error: " + error));
                    }
                });
            }
        });
    }
}