
import com.android.blackboard.chatbot.SendResult;
import com.android.blackboard.chatbot.WxChatbotClient;
import com.android.blackboard.chatbot.message.ImageMessage;
import com.android.blackboard.chatbot.test.TestConfig;
import com.android.blackboard.chatbot.utils.Base64Utils;
import com.android.blackboard.chatbot.utils.dto.ImageBase64Md5;

import org.junit.Test;

/**
 *
 */
public class ImageMessageTest {


    @Test
    public void imageMessageTest() throws Exception {

//        String string = "https://c-ssl.duitang.com/uploads/blog/202008/22/20200822114634_2b3e0.thumb.1000_0.png";
//        ImageBase64Md5 image = Base64Utils.ImageToBase64ByOnline(string);

        String path = "src/main/resources/";
        path += "20220905092248.jpg";
        ImageBase64Md5 image = Base64Utils.ImageToBase64ByLocal(path);

        ImageMessage imageMessage = new ImageMessage(image.getBase64(), image.getMd5());
        SendResult result = WxChatbotClient.send(TestConfig.CHATBOT_WEBHOOK, imageMessage);
        System.out.println(result);
    }
}