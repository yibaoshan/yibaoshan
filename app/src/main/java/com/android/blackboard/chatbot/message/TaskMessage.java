package com.android.blackboard.chatbot.message;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TaskMessage implements Message {

    private String path;

    public TaskMessage(String path) {
        this.path = path;
    }

    public String toJsonString() {
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(path);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(streamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> markdown = new HashMap<String, Object>();

        markdown.put("content", stringBuilder.toString());

        result.put("msgtype", "markdown");
        result.put("markdown", markdown);

        return JSON.toJSONString(result);
    }
}
