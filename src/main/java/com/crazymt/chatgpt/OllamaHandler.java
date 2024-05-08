package com.crazymt.chatgpt;

import com.crazymt.chatgpt.bean.OpenAIToken;
import com.crazymt.chatgpt.core.ConversationManager;
import com.crazymt.chatgpt.core.parser.Parser;
import com.crazymt.chatgpt.settings.OpenAISettingsState;
import com.crazymt.chatgpt.ui.MainPanel;
import com.crazymt.chatgpt.ui.MessageComponent;
import com.crazymt.chatgpt.util.StringUtil;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public class OllamaHandler extends AbstractHandler {
    private String baseUrl = "http://localhost:11434/api/chat";
    private String modelName = "llama3:8b";

    private Project myProject;

    private static final Logger LOG = LoggerFactory.getLogger(OllamaHandler.class);

    private final Stack<String> gpt35Stack = new Stack<>();

    public Call handle(MainPanel mainPanel, MessageComponent component, String question) {
        myProject = mainPanel.getProject();
        Call call = null;

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, OfficialBuilder.buildOllama(question, mainPanel.getContentPanel()).toString().getBytes(StandardCharsets.UTF_8));

            Request request = new Request.Builder()
                    .url(baseUrl)
                    .post(body)
//                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            OpenAISettingsState instance = OpenAISettingsState.getInstance();
            OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(Integer.parseInt(instance.connectionTimeout), TimeUnit.MILLISECONDS).readTimeout(Integer.parseInt(instance.readTimeout), TimeUnit.MILLISECONDS);
            /*builder.hostnameVerifier(getHostNameVerifier());

            builder.sslSocketFactory(getSslContext().getSocketFactory(), (X509TrustManager) getTrustAllManager());
*/
            OkHttpClient httpClient = builder.build();

            call = httpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    String errorMessage = StringUtil.isEmpty(e.getMessage())? "None" : e.getMessage();
                    /*if (e instanceof SocketException) {
                        LOG.info("GPT 3.5 Turbo: Stop generating");
                        component.setContent("Stop generating");
                        e.printStackTrace();
                        return;
                    }*/
                    LOG.error("GPT 3.5 Turbo Request failure. Url={}, error={}",
                            call.request().url(),
                            errorMessage);
                    errorMessage = "GPT 3.5 Turbo Request failure, cause: " + errorMessage;
                    component.setSourceContent(errorMessage);
                    component.setContent(errorMessage);
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                    mainPanel.getExecutorService().shutdown();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseMessage = response.body().string();
                    LOG.info("GPT 3.5 Turbo Response: answer={}",responseMessage);
                    if (response.code() != 200) {
//                        LOG.info("GPT 3.5 Turbo: Request failure. Url={}, response={}",provider.getUrl(), responseMessage);
                        component.setContent("Response failure, please try again. Error message: " + responseMessage);
                        mainPanel.aroundRequest(false);
                        return;
                    }
                    Parser.ParseResult parseResult = Parser.
                            parseOllama(mainPanel.getProject(), component, responseMessage);

                    mainPanel.getContentPanel().getMessages().add(OfficialBuilder.assistantMessage(parseResult.getSource()));
                    component.setSourceContent(parseResult.getSource());
                    component.setContent(parseResult.getHtml());
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                }
            });
            return call;

        } catch (Exception e) {
            component.setSourceContent(e.getMessage());
            component.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
        } finally {
            mainPanel.getExecutorService().shutdown();
        }
        return null;
    }
}
