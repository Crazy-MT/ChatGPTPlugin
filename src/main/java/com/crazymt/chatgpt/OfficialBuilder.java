package com.crazymt.chatgpt;

import com.crazymt.chatgpt.core.ConversationManager;
import com.crazymt.chatgpt.settings.OpenAISettingsState;
import com.crazymt.chatgpt.ui.MessageGroupComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OfficialBuilder {

    public static JsonObject buildOllama(String text, MessageGroupComponent component) {
        JsonObject result = new JsonObject();
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        result.addProperty("model","llama3:8b");
        result.addProperty("stream", false);
        component.getMessages().add(userMessage(text));
        result.add("messages",component.getMessages());
//        if (OpenAISettingsState.getInstance().enableGPT35StreamResponse) {
//            result.addProperty("stream",true);
//        }
        System.out.println("MTMTMT buildOllama:" + result.toString());
        return result;
    }

    private static JsonObject message(String role, String text) {
        JsonObject message = new JsonObject();
        message.addProperty("role",role);
        message.addProperty("content",text);
        return message;
    }

    public static JsonObject userMessage(String text) {
        return message("user",text);
    }

    public static JsonObject systemMessage(String text) {
        return message("system",text);
    }

    public static JsonObject assistantMessage(String text) {
        return message("assistant",text);
    }
}
