package com.example.socialmediaapp.application.dao.message;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.message.Chat;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.util.List;

@Dao
public abstract class ChatDao {

   @Query("select * from Chat where lastMsgTime < :bound order by lastMsgTime asc limit 10")
   public abstract List<Chat> findChatByOrder(Long bound);
   @Query("select u.* from UserBasicInfo u, Chat c where c.id = :chatId and u.autoId = c.otherId")
   public abstract UserBasicInfo loadPartnerInfo(String chatId);

   @Query("select max(time) from MessageItem where chatId = :chatId")
   public abstract Long loadLastMessageTime(String chatId);

   @Query("select * from MessageItem where chatId = :chatId and time = (select max(time) from MessageItem where chatId = :chatId)")
   public abstract MessageItem loadLastMessage(String chatId);

   @Query("select * from MessageItem where chatId = :chatId and time = :time")
   public abstract MessageItem loadMessageByTime(String chatId, long time);

   @Transaction
   public MessageItem lastMessage(String chatId) {
      Long lastTime = loadLastMessageTime(chatId);
      return lastTime == null ? null : loadMessageByTime(chatId, lastTime);
   }

   @Insert
   public abstract void insert(Chat chat);

   @Insert
   public abstract void insertAll(List<Chat> chat);

   @Update
   public abstract void update(Chat chat);

   @Query("select * from Chat where id = :chatId")
   public abstract Chat findChatById(String chatId);

   @Query("select ord from OrderedChat where chatId = :chatId")
   public abstract int findOrdByChatId(String chatId);

   @Query("delete from Chat")
   public abstract void deleteAllChat();

   @Query("delete from OrderedChat")
   public abstract void deleteAllOrderedChat();

}
