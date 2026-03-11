package com.example.myapplication.LLM;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HuggingFaceLLMStrategy implements LLMStrategy {

    private final OkHttpClient client = new OkHttpClient();
    private final String apiUrl;
    private final String hfToken;

    //obtain apiurl and token for hugging face from json file
    public HuggingFaceLLMStrategy(Context context) {
        JSONObject config = loadLocalJson(context,"LlmConfig.json");
        this.apiUrl = config.optString("api_url", "");
        this.hfToken = config.optString("hf_token", "");
    }

    //load the local json config file
    private JSONObject loadLocalJson(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
            String json = scanner.useDelimiter("\\A").next();
            scanner.close();
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    //Takes the query, sends a request and obtains the response
    @Override
    public void askQuestion(String question, LLMResponseCallback callback) {
        MediaType mediaType = MediaType.parse("application/json");

        //working query which also contains the system prompt which maintains the domain constraint
        String json = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"You are a helpful chatbot called PolyRecBot, and you are an assistant for a government user and vehicle details management app. You help registered users understand and use the app. Do not answer questions about admin functionality. Only answer questions related to the following features:\\n\\n1. Logging in and navigating the app as a user.\\n2. Viewing their vehicle details via the dashboard and search.\\n3. Updating their personal details using the 'Change Details' form in the dashboard.\\n4. Viewing their submitted requests and understanding statuses like approved, rejected, pending, appealed, and appeal rejected.\\n5. Using the appeal option when a request is rejected.\\n6. Viewing insurance policies and searching through them.\\n7. Receiving notifications when requests are updated or new policies are added.\\n8. Logging out via the user profile page. \\n9. General information about driving in australia, such as speed limits or general rules and also policies. Also you can answer questions related to driving and other related rules \\n\\nDo not answer any questions unrelated to these features. Politely refuse if asked about admin access, other users' data, system implementation details, or unrelated topics.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + question + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"model\": \"microsoft/Phi-3-mini-4k-instruct-fast\"\n" +
                "}";

        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", hfToken)
                .addHeader("Content-Type", "application/json")
                .build();

        //sends api request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject message = jsonObject
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message");
                        String content = message.getString("content");
                        callback.onSuccess(content.trim());
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    callback.onError("HTTP error: " + response.code() + " - " + response.message());
                }
            }
        });
    }

}
