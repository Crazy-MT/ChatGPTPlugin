package com.crazymt.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.crazymt.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

public class Prompt2Action extends AbstractEditorAction {

    public Prompt2Action() {
        super(() -> OpenAISettingsState.getInstance().prompt2Name);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt2Value;
        super.actionPerformed(e);
    }

}
