
package com.crazymt.chatgpt.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@State(
        name = "com.crazymt.chatgpt.settings.OpenAISettingsState",
        storages = @Storage("ChatGPTSettings.xml")
)
public class OpenAISettingsState implements PersistentStateComponent<OpenAISettingsState> {

  public String customizeUrl = "";

  public String readTimeout = "50000";
  public String connectionTimeout = "50000";
  public String accessToken = "";
  public String expireTime = "";
  public String apiKey = "";

  @Deprecated
  public List<String> customActionsPrefix = new ArrayList<>();

  public String chatGptModel = "text-davinci-002-render-sha";
  public String gpt35Model = "gpt-3.5-turbo";
  public Boolean enableContext = false;
  public String assistantApiKey = "";
  public Boolean enableTokenConsumption = false;
  public Boolean enableGPT35StreamResponse = false;
  public String gpt35TurboUrl = "https://api.openai.com/v1/chat/completions";

  public Boolean enableCustomizeGpt35TurboUrl = false;
  public Boolean enableCustomizeChatGPTUrl = false;

  public String prompt1Name = "Translate";
  public String prompt1Value = "Translate to Chinese:";
  public String prompt2Name = "Code Interpreter";
  public String prompt2Value = "Interpreter this code:";
  public String prompt3Name = "Custom";
  public String prompt3Value = "Custom prompt:";

  @Tag("customPrompts")
  public Map<String, String> customPrompts = new HashMap<>();

  public static OpenAISettingsState getInstance() {
    return ApplicationManager.getApplication().getService(OpenAISettingsState.class);
  }

  @Nullable
  @Override
  public OpenAISettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void reload() {
    loadState(this);
  }
}
