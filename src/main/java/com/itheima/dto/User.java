package com.itheima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dsapa
 * @date 2023/4/8 20:10
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String spoken; //问题文本

    private String rawSpoken; //原始问题文本

    private String receivedName; //提问者名称

    private String groupName; //QA所在群名（群聊）

    private String groupRemark; //QA所在群备注名（群聊）

    private Integer roomType; //QA所在房间类型 1=外部群 2=外部联系人 3=内部群 4=内部联系人

    private Boolean atMe; //是否@机器人（群聊）

    private Integer textType; //消息类型 0=未知 1=文本 2=图片 5=视频 7=小程序 8=链接 9=文件

    public String toString() {
        return "User: {" +
                "\tspoken: " + spoken + "\n" +
                "\trawSpoken: " + rawSpoken + "\n" +
                "\treceivedName: " + receivedName + "\n" +
                "\tgroupName: " + groupName + "\n" +
                "\tgroupRemark: " + groupRemark + "\n" +
                "\troomType: " + roomType + "\n" +
                "\tatMe: " + atMe + "\n" +
                "\ttextType: " + textType + "\n}";
    }

}
