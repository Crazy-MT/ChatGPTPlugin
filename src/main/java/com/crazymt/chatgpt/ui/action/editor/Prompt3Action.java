package com.crazymt.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.crazymt.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

public class Prompt3Action extends AbstractEditorAction {

    public Prompt3Action() {
        super(() -> OpenAISettingsState.getInstance().prompt3Name);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt3Value;
        super.actionPerformed(e);
    }

}
