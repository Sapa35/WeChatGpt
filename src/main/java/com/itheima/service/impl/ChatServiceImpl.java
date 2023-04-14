package com.itheima.service.impl;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.itheima.dto.DataInfo;
import com.itheima.dto.Info;
import com.itheima.dto.User;
import com.itheima.dto.UserDto;
import com.itheima.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dsapa
 * @date 2023/4/1 0:57
 * @Description
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Value("${openai.chatEndpoint}")
    private String chatEndpoint;
    @Value("${openai.apiKey}")
    private String apiKey;

    @Override
    public UserDto chat(User user) {
        //参数校验
        if(user == null){
            return new UserDto(-1,"无法接收到参数",null);
        }
        //判断消息类型 todo

        //构造gpt请求
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", user.getSpoken());
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;

        if(user.getRoomType() == 2 || user.getRoomType() == 4){
            log.info("私聊"+"用户"+user.getReceivedName()+"  提问："+user.getSpoken());
        }else {
            log.info("群聊"+user.getGroupName()+"用户"+user.getGroupRemark()+"  提问："+user.getSpoken());
        }

        long before = System.currentTimeMillis();
        try {
            //发送请求，调用gpt
            String body = HttpRequest.post(chatEndpoint)
                    .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(paramMap))
                    .execute()
                    .body();

            //获得响应，处理数据
            JSONObject jsonObject = JSONUtil.parseObj(body);
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
        } catch (HttpException e) {
            return new UserDto(-1,e.getMessage(),null);
        } catch (ConvertException e) {
            return new UserDto(-2,e.getMessage(),null);
        }

        long later = System.currentTimeMillis();
        log.info("提问字数："+user.getSpoken().length());
        log.info("返回字数："+message.getStr("content").length());
        log.info("调用时长：" +(later-before)+"ms");
        log.info("Chatgpt的回答:\n"+message.getStr("content"));

        //敏感检测过滤 1.本地检测 2.阿里云内容安全 todo

        UserDto userDto = new UserDto();
        userDto.setCode(0);
        userDto.setMessage("success");
        userDto.setData(new DataInfo(5000,new Info(message.getStr("content"))));
        return userDto;

        //流式输出
        /*OpenAiService service = new OpenAiService("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");
        System.out.println("Streaming chat completion...");

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(50)
                .logitBias(new HashMap<>())
                .build();

        service.streamChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(System.out::println);*/
    }
}
