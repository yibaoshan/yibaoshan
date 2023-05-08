package com.android.blackboard;

import org.junit.Test;

import java.io.File;

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