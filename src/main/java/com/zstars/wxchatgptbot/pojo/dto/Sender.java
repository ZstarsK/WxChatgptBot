package com.zstars.wxchatgptbot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Sender {
    private String name;
    private String id;
    private boolean isRoom;
}
