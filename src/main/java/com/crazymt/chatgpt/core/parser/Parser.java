package com.crazymt.chatgpt.core.parser;

import com.crazymt.chatgpt.bean.OpenAIStreamBean;
import com.crazymt.chatgpt.ui.MessageComponent;
import com.google.gson.*;
import com.intellij.openapi.project.Project;
import com.crazymt.chatgpt.core.ConversationManager;
import com.crazymt.chatgpt.util.HtmlUtil;

public class Parser {
    public static ParseResult parseFreeGPT35WithStream(Project project, MessageComponent component, String response) {
        OpenAIStreamBean resultData = new Gson().fromJson(response, OpenAIStreamBean.class);

        String conversationId = resultData.getConversation_id();
        String parentId = resultData.getMessage().getMetadata().getParent_id();
        ConversationManager.getInstance(project).setParentMessageId(parentId);
        ConversationManager.getInstance(project).setConversationId(conversationId);

        component.getAnswers().clear();
        component.getAnswers().add(resultData.getMessage().getContent().getParts().get(0));

        ParseResult parseResult = new ParseResult();
        parseResult.source = component.prevAnswers();
        parseResult.html = HtmlUtil.md2html(component.prevAnswers());
        return parseResult;
    }

    public static class ParseResult {
        private String source;
        private String html;

        public String getSource() {
            return source;
        }

        public String getHtml() {
            return html;
        }
    }

}
