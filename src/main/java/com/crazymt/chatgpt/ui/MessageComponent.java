package com.crazymt.chatgpt.ui;

import com.crazymt.chatgpt.util.ClipboardUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import com.crazymt.chatgpt.icons.Icons;
import com.crazymt.chatgpt.message.MessageBundle;
import com.crazymt.chatgpt.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MessageComponent extends JBPanel<MessageComponent> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageComponent.class);

    private final MessagePanel component = new MessagePanel();

    private final String question;

    private String answer;

    private final List<String> answers = new ArrayList<>();

    public MessageComponent(String content, boolean me, boolean isTips) {
        question = content;
        setDoubleBuffered(true);
        setOpaque(true);
        if (!isTips) {
            setBackground(me ? new JBColor(0xEAEEF7, 0x45494A) : new JBColor(0xE0EEF7, 0x2d2f30 /*2d2f30*/));
        }
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(7), 0));

        JPanel centerPanel = new JPanel(new VerticalLayout(JBUI.scale(8)));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(JBUI.Borders.emptyRight(10));
        centerPanel.add(createContentComponent(content));
        add(centerPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(JBUI.Borders.emptyRight(10));
        JLabel copyAction = new JLabel(AllIcons.Actions.Copy);
        copyAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyAction.setToolTipText("Copy it.");
        copyAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ClipboardUtil.setStr(me ? question : answer);
                Notifications.Bus.notify(
                        new Notification(MessageBundle.message("group.id"),
                                "Copy successfully",
                                NotificationType.INFORMATION));
            }
        });

        JLabel googleAction = new JLabel(Icons.GOOGLE);
        googleAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        googleAction.setToolTipText("Google it.");
        googleAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    BrowserUtil.open("https://www.google.com/search?q=" + URLEncoder.encode((me ? question : answer), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // 创建一个新的 JPanel 来包含 copyAction 和 googleAction
        JPanel actionsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 使用 FlowLayout 来使得组件右对齐
        actionsContainer.setOpaque(false);

        if (!isTips) {
            // 添加 copyAction 和 googleAction 到 actionsContainer
            actionsContainer.add(copyAction);
            actionsContainer.add(googleAction);
        }

        // 将 actionsContainer 添加到 actionPanel 的 BorderLayout.NORTH 位置
        actionPanel.add(actionsContainer, BorderLayout.NORTH);

        add(actionPanel, BorderLayout.EAST);
    }

    public Component createContentComponent(String content) {

        component.setEditable(false);
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, java.lang.Boolean.TRUE);
        component.setContentType("text/html; charset=UTF-8");
        component.setOpaque(false);
        component.setBorder(null);

        component.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, StringUtil.unescapeXmlEntities(StringUtil.stripHtml(content, " ")));

        component.updateMessage(content);

        component.setEditable(false);
        if (component.getCaret() != null) {
            component.setCaretPosition(0);
        }

        component.revalidate();
        component.repaint();

        return component;
    }

    public void setContent(String content) {
        new MessageWorker(content).execute();
    }

    public void setSourceContent(String source) {
        answer = source;
    }

    public void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            Rectangle bounds = getBounds();
            scrollRectToVisible(bounds);
        });
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String prevAnswers() {
        StringBuilder result = new StringBuilder();
        for (String s : answers){
            result.append(s);
        }
        return result.toString();
    }

    class MessageWorker extends SwingWorker<Void, String> {
        private final String message;

        public MessageWorker(String message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground() throws Exception {
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                component.updateMessage(message);
                component.updateUI();
            } catch (Exception e) {
                LOG.error("ChatGPT Exception in processing response: response:{} error: {}", message, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
