package com.crazymt.chatgpt;

import com.crazymt.chatgpt.ui.MainPanel;
import com.crazymt.chatgpt.util.MyUIUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class GPT35TurboToolWindow {

  private final MainPanel panel;

  public GPT35TurboToolWindow(@NotNull Project project) {
    panel = new MainPanel(project, false);
  }

  public JPanel getContent() {
    return panel.init();
  }

  public MainPanel getPanel() {
    return panel;
  }

/**
 * rapidly get input focus by keystorke f key
 */
  public void registerKeystrokeFocus(){
    MyUIUtil.registerKeystrokeFocusForInput(panel.getSearchTextArea().getTextArea());
  }
}
