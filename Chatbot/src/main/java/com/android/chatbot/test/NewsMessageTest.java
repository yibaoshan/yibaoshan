package com.android.chatbot.test;

import com.android.chatbot.SendResult;
import com.android.chatbot.WxChatbotClient;
import com.android.chatbot.message.NewsArticle;
import com.android.chatbot.message.NewsMessage;

import org.junit.Test;

/**
 * 
 */
public class NewsMessageTest {

    private WxChatbotClient client = new WxChatbotClient();

    @Test
    public void testSendNewsMessage() throws Exception {

    	NewsArticle article=new NewsArticle();
    	article.setTitle("中秋节礼品领取");
    	article.setDescription("今年中秋节公司有豪礼相送");
    	article.setUrl("https://work.weixin.qq.com/api/doc#90000/90135/91760");
    	article.setPicurl("http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png");
    	
    	NewsMessage message=new NewsMessage();
    	message.addNewsArticle(article);
    	SendResult result = client.send(TestConfig.CHATBOT_WEBHOOK, message);
        System.out.println(result);
    }

}