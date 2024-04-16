package com.crazymt.chatgpt;

import com.crazymt.chatgpt.ui.action.GitHubAction;
import com.crazymt.chatgpt.ui.action.NewChatAction;
import com.crazymt.chatgpt.ui.action.PluginAction;
import com.crazymt.chatgpt.ui.action.SettingAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import com.crazymt.chatgpt.message.MessageBundle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MyToolWindowFactory implements ToolWindowFactory {

    public static final Key ACTIVE_CONTENT = Key.create("ActiveContent");

    public static final String GPT35_TRUBO_CONTENT_NAME = "ChatGPT";

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        GPT35TurboToolWindow gpt35TurboToolWindow = new GPT35TurboToolWindow(project);
        com.intellij.ui.content.Content gpt35Turbo = contentFactory.createContent(gpt35TurboToolWindow.getContent(), "", false);
        gpt35Turbo.setCloseable(false);

        toolWindow.getContentManager().addContent(gpt35Turbo);

        project.putUserData(ACTIVE_CONTENT, gpt35TurboToolWindow.getPanel());

        // Add the selection listener
        toolWindow.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                project.putUserData(ACTIVE_CONTENT,gpt35TurboToolWindow.getPanel());
            }
        });

        List<AnAction> actionList = new ArrayList<>();
        actionList.add(new NewChatAction(gpt35TurboToolWindow.getPanel().getContentPanel()));
//        actionList.add(new SettingAction(MessageBundle.message("action.settings")));
        actionList.add(new GitHubAction());
        actionList.add(new PluginAction());
        toolWindow.setTitleActions(actionList);
    }
}
