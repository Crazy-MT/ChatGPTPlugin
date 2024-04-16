package com.crazymt.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.crazymt.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

public class Prompt1Action extends AbstractEditorAction {

    public Prompt1Action() {
        super(() -> OpenAISettingsState.getInstance().prompt1Name);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt1Value;
        super.actionPerformed(e);
    }

}
