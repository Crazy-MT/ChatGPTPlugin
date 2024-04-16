package com.crazymt.chatgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.crazymt.chatgpt.message.MessageBundle;
import org.jetbrains.annotations.NotNull;

public class PluginAction extends DumbAwareAction {

    public PluginAction() {
        super(() -> MessageBundle.message("action.plugins"), AllIcons.Nodes.Plugin);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse("https://plugins.jetbrains.com/author/103f2850-496f-4f8d-aec7-28240c6c07e4");
    }
}
