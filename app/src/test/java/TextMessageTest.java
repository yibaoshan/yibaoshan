

import org.junit.Test;

import java.io.File;

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
        File file = new File("~/Desktop/CP/workspace/android/heyo");
        File[] listFiles = file.listFiles();
        assert listFiles != null;
        for (File f : listFiles) System.out.println(f.getName());
    }
}