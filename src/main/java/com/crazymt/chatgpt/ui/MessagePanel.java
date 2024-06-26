package com.crazymt.chatgpt.ui;

import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.UIUtil;
import com.crazymt.chatgpt.util.StringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MessagePanel extends HtmlPanel {

    private String message = "";

    @Override
    protected @NotNull @Nls String getBody() {
        return StringUtil.isEmpty(message) ? "" : message;
    }

    @Override
    protected @NotNull Font getBodyFont() {
        return UIUtil.getLabelFont();
    }

    public void updateMessage(String updateMessage) {
        this.message = updateMessage;
        update();
    }
}
