package com.crazymt.chatgpt.core;

import com.crazymt.chatgpt.FreeChatGPTHandler;
import com.crazymt.chatgpt.MyToolWindowFactory;
import com.crazymt.chatgpt.ui.MainPanel;
import com.crazymt.chatgpt.ui.MessageComponent;
import com.crazymt.chatgpt.ui.MessageGroupComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class SendAction extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    private String data;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object mainPanel = project.getUserData(MyToolWindowFactory.ACTIVE_CONTENT);
        doActionPerformed((MainPanel) mainPanel, data);
    }

    public void doActionPerformed(MainPanel mainPanel, String data) {
        if (StringUtils.isEmpty(data)) {
            return;
        }

        mainPanel.getSearchTextArea().getTextArea().setText("");
        mainPanel.aroundRequest(true);
        Project project = mainPanel.getProject();
        MessageGroupComponent contentPanel = mainPanel.getContentPanel();

        MessageComponent question = new MessageComponent(data,true, false);
        MessageComponent answer = new MessageComponent("loading...",false, false);
        contentPanel.add(question);
        contentPanel.add(answer);

        try {
            ExecutorService executorService = mainPanel.getExecutorService();
            FreeChatGPTHandler chatGPTHandler = project.getService(FreeChatGPTHandler.class);
            executorService.submit(() -> {
                EventSource handle = chatGPTHandler.handle(mainPanel, answer, data);
                mainPanel.setRequestHolder(handle);
                contentPanel.updateLayout();
                contentPanel.scrollToBottom();
            });

        } catch (Exception e) {
            answer.setSourceContent(e.getMessage());
            answer.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
            LOG.error("ChatGPT: Request failed, error={}", e.getMessage());
        }
    }
}
