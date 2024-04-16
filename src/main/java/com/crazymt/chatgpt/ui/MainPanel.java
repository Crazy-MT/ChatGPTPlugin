package com.crazymt.chatgpt.ui;

import com.intellij.find.SearchTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.project.Project;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBTextArea;
import com.crazymt.chatgpt.ui.listener.SendListener;
import okhttp3.Call;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPanel {

    private final SearchTextArea searchTextArea;
    private final JButton button;
    private final JButton stopGenerating;
    private final MessageGroupComponent contentPanel;
    private final JProgressBar progressBar;
    private final OnePixelSplitter splitter;
//    private final JPanel splitterPannel;
    private final Project myProject;
    private JPanel actionPanel;
    private ExecutorService executorService;
    private Object requestHolder;

    private boolean myIsChatGPTModel;

    public MainPanel(@NotNull Project project, boolean isChatGPTModel) {
        myIsChatGPTModel = isChatGPTModel;
        myProject = project;
        SendListener listener = new SendListener(this);
//        splitterPannel = new JPanel(new BorderLayout());
        splitter = new OnePixelSplitter(true,.98f);
        splitter.setDividerWidth(2);

        searchTextArea = new SearchTextArea(new JBTextArea(),true);
        searchTextArea.getTextArea().addKeyListener(listener);
        searchTextArea.setMultilineEnabled(true);
        button = new JButton("Submit");
        button.addActionListener(listener);
        button.setUI(new DarculaButtonUI());

        stopGenerating = new JButton("Stop");
        stopGenerating.addActionListener(e -> {
            executorService.shutdownNow();
            aroundRequest(false);
            if (requestHolder instanceof EventSource) {
                ((EventSource)requestHolder).cancel();
            } else if (requestHolder instanceof Call) {
                ((Call) requestHolder).cancel();
            }
        });
        stopGenerating.setUI(new DarculaButtonUI());

        actionPanel = new JPanel(new BorderLayout());
        actionPanel.setMinimumSize(new Dimension(searchTextArea.getWidth(), 100));
        actionPanel.setMaximumSize(new Dimension(searchTextArea.getWidth(), 500));
        System.out.println("MainPanel.MainPanel MTMTMT" + searchTextArea.getWidth());
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        actionPanel.add(progressBar, BorderLayout.NORTH);
        actionPanel.add(searchTextArea, BorderLayout.CENTER);
        actionPanel.add(button, BorderLayout.EAST);
        contentPanel = new MessageGroupComponent(project, isChatGPTModel());

//        splitterPannel.add(contentPanel, BorderLayout.CENTER);
//        splitterPannel.add(actionPanel, BorderLayout.SOUTH);
        splitter.setFirstComponent(contentPanel);
        splitter.setSecondComponent(actionPanel);
    }

    public Project getProject() {
        return myProject;
    }

    public SearchTextArea getSearchTextArea() {
        return searchTextArea;
    }

    public MessageGroupComponent getContentPanel() {
        return contentPanel;
    }

    public JPanel init() {
        return splitter;
    }

    public JButton getButton() {
        return button;
    }

    public ExecutorService getExecutorService() {
        executorService = Executors.newFixedThreadPool(1);
        return executorService;
    }

    public void aroundRequest(boolean status) {
        progressBar.setIndeterminate(status);
        progressBar.setVisible(status);
        button.setEnabled(!status);
        if (status) {
            contentPanel.addScrollListener();
            actionPanel.remove(button);
            actionPanel.add(stopGenerating,BorderLayout.EAST);
        } else {
            contentPanel.removeScrollListener();
            actionPanel.remove(stopGenerating);
            actionPanel.add(button,BorderLayout.EAST);
        }
        actionPanel.updateUI();
    }

    public void setRequestHolder(Object eventSource) {
        this.requestHolder = eventSource;
    }

    public boolean isChatGPTModel() {
        return myIsChatGPTModel;
    }
}
