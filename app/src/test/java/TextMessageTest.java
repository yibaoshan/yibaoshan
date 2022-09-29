
import com.android.blackboard.chatbot.SendResult;
import com.android.blackboard.chatbot.WxChatbotClient;
import com.android.blackboard.chatbot.message.TaskMessage;
import com.android.blackboard.chatbot.test.TestConfig;

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
//        SendResult result = WxChatbotClient.send(TestConfig.CHATBOT_WEBHOOK, taskMessage);
//        System.out.println(result);
    }
}