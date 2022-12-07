

import org.junit.Test;

import chatbot.SendResult;
import chatbot.WxChatbotClient;
import chatbot.message.TaskMessage;
import chatbot.test.TestConfig;

/**
 *
 */
public class TextMessageTest {

    @Test
    public void testSendTextMessageWithAtAndAtAll() throws Exception {
        TaskMessage taskMessage = new TaskMessage("src/main/resources/daily.md");
//        TaskMessage taskMessage = new TaskMessage("src/main/resources/weekly.md");
        System.out.println(taskMessage.toJsonString());
        SendResult result = WxChatbotClient.send(TestConfig.CHATBOT_WEBHOOK, taskMessage);
        System.out.println(result);
    }
}