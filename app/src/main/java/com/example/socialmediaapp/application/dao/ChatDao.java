package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.socialmediaapp.application.database.OrderedChat;
import com.example.socialmediaapp.application.entity.Chat;
import com.example.socialmediaapp.application.entity.MessageItem;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ChatDao {

   @Query("select * from OrderedChat where chatId = :chatId")
   public abstract OrderedChat findOrderedChatByChatId(Integer chatId);
   @Query("select * from OrderedChat where ord > :bound order by ord desc limit 10")
   public abstract List<OrderedChat> findOrderedChatByBound(Integer bound);

   @Insert
   public abstract long insertOrderedChat(OrderedChat msg);

   @Insert
   public abstract void insertAllOrderedChat(List<OrderedChat> chats);
   @Update
   public abstract void updateOrderedChat(OrderedChat msg);
   @Transaction
   public List<Chat> loadNextChats(int lastId) {
      int ord = findOrdByChatId(lastId);
      List<OrderedChat> orderedChats = findOrderedChatByBound(ord);
      List<Chat> chats = new ArrayList<>();
      for (OrderedChat orderedChat : orderedChats) {
         chats.add(findChatById(orderedChat.getChatId()));
      }
      return chats;
   }

   @Query("select max(time) from MessageItem where chatId = :chatId")
   public abstract long loadLastMessageTime(int chatId);

   @Query("select * from MessageItem where chatId = :chatId and time = :time")
   public abstract MessageItem loadMessageByTime(int chatId, long time);

   @Transaction
   public MessageItem lastMessage(Integer chatId) {
      long lastTime = loadLastMessageTime(chatId);
      return loadMessageByTime(chatId, lastTime);
   }

   @Insert
   public abstract long insert(Chat chat);

   @Update
   public abstract void update(Chat chat);

   @Query("select * from Chat where id = :chatId")
   public abstract Chat findChatById(Integer chatId);

   @Query("select ord from OrderedChat where chatId = :chatId")
   public abstract int findOrdByChatId(Integer chatId);
}
