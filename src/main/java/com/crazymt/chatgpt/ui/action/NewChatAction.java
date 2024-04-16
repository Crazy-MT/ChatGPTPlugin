package com.crazymt.chatgpt.ui.action;

import com.crazymt.chatgpt.icons.Icons;
import com.crazymt.chatgpt.ui.MessageGroupComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

import org.jetbrains.annotations.NotNull;


public class NewChatAction extends DumbAwareAction {
    MessageGroupComponent messageGroupComponent;

    public NewChatAction(MessageGroupComponent messageGroupComponent) {
        super(() -> "Clear Conversation History", Icons.DELETE);
        this.messageGroupComponent = messageGroupComponent;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        messageGroupComponent.newChat();
    }
}
