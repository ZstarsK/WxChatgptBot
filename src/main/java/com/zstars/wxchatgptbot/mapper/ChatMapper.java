package com.zstars.wxchatgptbot.mapper;

import com.zstars.wxchatgptbot.pojo.Chat;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
* @author i-kevin
* @description 针对表【Chat】的数据库操作Mapper
* @createDate 2024-01-19 15:37:56
* @Entity pojo.chat
*/
@Repository
public interface ChatMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Chat record);

    int insertSelective(Chat record);

    Chat selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Chat record);

    int updateByPrimaryKey(Chat record);
    
    void insertChat(Chat chat);

    List<Chat> findChatsByName(@Param("name") String name);

    List<Chat> findChatsByUserId(@Param("userId") String userId);
    
    Chat findChatsByUserId_L1(@Param("userId") String userId);

    @Select("SELECT ChatId FROM Chat WHERE UserId = #{userId} ORDER BY ChatId ASC LIMIT #{limit}")
    List<Integer> findOldestChatIds(@Param("userId") String userId, @Param("limit") int limit);
    @Select("SELECT ChatId FROM Chat WHERE UserId = #{userId} ORDER BY ChatId DESC LIMIT 10, 18446744073709551615")
    List<Integer> findChatIdsToDelete(@Param("userId") String userId);

    @Delete("<script>" +
            "DELETE FROM Chat WHERE ChatId IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deleteChatsByIds(@Param("ids") List<Integer> ids);
    
}
