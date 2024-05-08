package com.crazymt.chatgpt.core;

import com.crazymt.chatgpt.MyToolWindowFactory;
import com.crazymt.chatgpt.OllamaHandler;
import com.crazymt.chatgpt.ui.MainPanel;
import com.crazymt.chatgpt.ui.MessageComponent;
import com.crazymt.chatgpt.ui.MessageGroupComponent;
import com.crazymt.chatgpt.util.StringUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import okhttp3.Call;
import okhttp3.sse.EventSource;
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
        if (StringUtil.isEmpty(data)) {
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
            OllamaHandler ollamaHandler = project.getService(OllamaHandler.class);
            executorService.submit(() -> {
                Call handle = ollamaHandler.handle(mainPanel, answer, data);
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
