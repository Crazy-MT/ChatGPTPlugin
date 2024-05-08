package com.crazymt.chatgpt.ui;

import com.crazymt.chatgpt.OfficialBuilder;
import com.google.gson.JsonArray;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.NullableComponent;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.crazymt.chatgpt.core.ConversationManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static com.intellij.icons.AllIcons.Actions.DeleteTag;

public class MessageGroupComponent extends JBPanel<MessageGroupComponent> implements NullableComponent {
    private final JPanel myList = new JPanel(new VerticalLayout(JBUI.scale(10)));
    private final MyScrollPane myScrollPane = new MyScrollPane(myList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private int myScrollValue = 0;

    private final MyAdjustmentListener scrollListener = new MyAdjustmentListener();
    private final MessageComponent tips =
            new MessageComponent("How can I help you today?",false, true);
    private JsonArray messages = new JsonArray();

    private Project project;

    public MessageGroupComponent(@NotNull Project project, boolean isChatGPT) {
        this.project = project;
        messages.add(OfficialBuilder.systemMessage("Always response in Chinese(汉字), not English"));
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(7), 0));
        setBackground(UIUtil.getListBackground());

        JPanel mainPanel = new JPanel(new BorderLayout(0, JBUI.scale(8)));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(JBUI.Borders.emptyLeft(8));

        add(mainPanel,BorderLayout.CENTER);

        JBLabel myTitle = new JBLabel("Conversation");
        myTitle.setForeground(JBColor.namedColor("Label.infoForeground", new JBColor(Gray.x80, Gray.x8C)));
        myTitle.setFont(JBFont.label());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(JBUI.Borders.empty(0,10,10,0));

//        panel.add(myTitle, BorderLayout.WEST);

        /*LinkLabel<String> newChat = new LinkLabel<>("", DeleteTag);
        newChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                myList.removeAll();
                myList.add(tips);
//                myList.add(mustRead);
                myList.updateUI();
                ConversationManager.getInstance(project).setConversationId(null);
                ConversationManager.getInstance(project).setNewDeviceId();
                messages = new JsonArray();
            }
        });*/

//        newChat.setFont(JBFont.label());
//        newChat.setBorder(JBUI.Borders.emptyRight(20));
//        panel.add(newChat, BorderLayout.EAST);
        mainPanel.add(panel, BorderLayout.NORTH);

        myList.setOpaque(true);
        myList.setBackground(UIUtil.getListBackground());
        myList.setBorder(JBUI.Borders.emptyRight(10));

        myScrollPane.setBorder(JBUI.Borders.empty());
        mainPanel.add(myScrollPane);
        myScrollPane.getVerticalScrollBar().setAutoscrolls(true);
        myScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            int value = e.getValue();
            if (myScrollValue == 0 && value > 0 || myScrollValue > 0 && value == 0) {
                myScrollValue = value;
                repaint();
            }
            else {
                myScrollValue = value;
            }
        });

        add(tips);
    }

    public void add(MessageComponent messageComponent) {
        // The component should be immediately added to the
        // container and displayed in the UI

        // SwingUtilities.invokeLater(() -> {
        myList.add(messageComponent);
        updateLayout();
        scrollToBottom();
        updateUI();
        // });
    }

    public void scrollToBottom() {
        JScrollBar verticalScrollBar = myScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    public void updateLayout() {
        LayoutManager layout = myList.getLayout();
        int componentCount = myList.getComponentCount();
        for (int i = 0 ; i< componentCount ; i++) {
            layout.removeLayoutComponent(myList.getComponent(i));
            layout.addLayoutComponent(null,myList.getComponent(i));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (myScrollValue > 0) {
            g.setColor(JBColor.border());
            int y = myScrollPane.getY() - 1;
            g.drawLine(0, y, getWidth(), y);
        }
    }


    @Override
    public boolean isVisible() {
        if (super.isVisible()) {
            int count = myList.getComponentCount();
            for (int i = 0 ; i < count ; i++) {
                if (myList.getComponent(i).isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNull() {
        return !isVisible();
    }

    public void newChat() {
        myList.removeAll();
        myList.add(tips);
//                myList.add(mustRead);
        myList.updateUI();
        ConversationManager.getInstance(project).setConversationId(null);
        ConversationManager.getInstance(project).setNewDeviceId();
        messages = new JsonArray();
        messages.add(OfficialBuilder.systemMessage("Always response in Chinese(汉字), not English"));
    }

    static class MyAdjustmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar source = (JScrollBar) e.getSource();
            if (!source.getValueIsAdjusting()) {
                source.setValue(source.getMaximum());
            }
        }
    }

    public void addScrollListener() {
        myScrollPane.getVerticalScrollBar().
                addAdjustmentListener(scrollListener);
    }

    public void removeScrollListener() {
        myScrollPane.getVerticalScrollBar().
                removeAdjustmentListener(scrollListener);
    }

    public JsonArray getMessages() {
        return messages;
    }
}
