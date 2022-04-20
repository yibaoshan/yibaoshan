import com.android.chatbot.SendResult;
import com.android.chatbot.WxChatbotClient;
import com.android.chatbot.message.TaskMessage;
import com.android.chatbot.test.TestConfig;

import org.junit.Test;

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