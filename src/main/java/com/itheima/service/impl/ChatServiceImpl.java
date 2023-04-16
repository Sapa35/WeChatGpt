package com.itheima.service.impl;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.itheima.dto.DataInfo;
import com.itheima.dto.Info;
import com.itheima.dto.User;
import com.itheima.dto.UserDto;
import com.itheima.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Value("${sendMsgUrl}")
    private String sendMsgUrl;

    @Value("${robotId}")
    private String robotId;

    @Value("${openai.apiKey}")
    private String apiKey;

    @Override
    public UserDto chat(User user) {
        //参数校验
        if(user == null){
            return new UserDto(-1,"无法接收到参数",null);
        }

        //构造gpt请求
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", user.getSpoken());
        }});
        paramMap.put("messages", dataList);

        log.info("user = " + user);

        if(user.getRoomType() == 2 || user.getRoomType() == 4){
            log.info("私聊"+"用户"+user.getReceivedName()+"  提问："+user.getSpoken());
        }else {
            log.info("群聊"+user.getGroupName()+"  用户"+user.getGroupRemark()+"  提问："+user.getSpoken());
        }

        final long before = System.currentTimeMillis();
        try {
            //发送请求，调用gpt

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(paramMap));

            Request request = new Request.Builder()
                    .url(chatEndpoint)
                    .method("POST", body)
                    .addHeader("Authorization", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error("handleChatGptMsg ->>> " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //获得响应，处理数据
                    handleResponse(response, user, before);
                }
            });
        } catch (HttpException e) {
            return new UserDto(-1,e.getMessage(),null);
        } catch (ConvertException e) {
            return new UserDto(-2,e.getMessage(),null);
        }

        return new UserDto(0, "success", null);
    }

    private void sendMsg(User user, DataInfo info) {
        final String paramMap = "{\"socketType\":2," +
                "\"list\":[" +
                "{" +
                "\"type\":203," +
                "\"titleList\":[" +
                "\"" + user.getGroupName() + "\"" +
                "]," +
                "\"receivedContent\":\"" + info.getInfo().getText() + "\"" +
                (user.getAtMe() ? ",\"atList\":[\"" + user.getReceivedName() + "\"]" : "") +
                "}" +
                "]" +
                "}";

        final OkHttpClient client = new OkHttpClient().newBuilder().build();
        final MediaType mediaType = MediaType.parse("application/json");
        final RequestBody requestBody = RequestBody.create(mediaType, JSONUtil.toJsonStr(paramMap));

        Request request = new Request.Builder()
                .url(sendMsgUrl + robotId)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            final Response response = client.newCall(request).execute();
            final ResponseBody body = response.body();
            if (body == null) {
                log.error("sendMsg ->>> npe");
                return;
            }
            final String res = body.string();
            log.info("sendMsg ->>> success, res = " + res);
        } catch (IOException e) {
            log.error("sendMsg ->>> " + e.getMessage());
        }
    }

    private void handleResponse(Response response, User user, long before) {
        //获得响应，处理数据
        ResponseBody body = response.body();
        if (body == null) {
            return;
        }
        JSONObject jsonObject;
        try {
            final String bodyStr = body.string();
            log.error(bodyStr);
            jsonObject = JSONUtil.parseObj(bodyStr);
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            final JSONObject message = result.getJSONObject("message");

            long later = System.currentTimeMillis();
            log.info("提问字数："+ user.getSpoken().length());
            log.info("返回字数："+message.getStr("content").length());
            log.info("调用时长：" +(later-before)+"ms");
            log.info("Chatgpt的回答:\n"+message.getStr("content"));

            //敏感检测过滤 1.本地检测 2.阿里云内容安全 todo

            final DataInfo resultContent = new DataInfo(5000,new Info(message.getStr("content")));

            sendMsg(user, resultContent);
        } catch (IOException e) {
            log.error("handleResponse ->>> " + e.getMessage());
        }

    }
}
