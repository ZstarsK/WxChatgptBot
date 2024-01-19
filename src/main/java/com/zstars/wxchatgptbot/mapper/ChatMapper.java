package com.zstars.wxchatgptbot.mapper;

import com.zstars.wxchatgptbot.pojo.Chat;

/**
* @author i-kevin
* @description 针对表【Chat】的数据库操作Mapper
* @createDate 2024-01-19 15:37:56
* @Entity generator.domain.Chat
*/
public interface ChatMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Chat record);

    int insertSelective(Chat record);

    Chat selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Chat record);

    int updateByPrimaryKey(Chat record);

}
