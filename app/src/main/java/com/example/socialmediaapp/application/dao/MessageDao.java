package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.socialmediaapp.application.entity.IconMessageItem;
import com.example.socialmediaapp.application.entity.ImageMessageItem;
import com.example.socialmediaapp.application.entity.MessageItem;
import com.example.socialmediaapp.application.entity.TextMessageItem;

import java.util.List;

@Dao
public abstract class MessageDao {
   @Insert
   public abstract long insert(MessageItem messageItem);

   @Insert
   public abstract long insertImageMessageItem(ImageMessageItem messageItem);

   @Insert
   public abstract long insertTextMessageItem(TextMessageItem messageItem);

   @Insert
   public abstract long insertIconMessageItem(IconMessageItem messageItem);

   @Query("select min(ord) from MessageItem where chatId = :chatId")
   public abstract Integer lastMessageOrder(Integer chatId);

   @Query("select * from MessageItem where chatId = :chatId and ord <= :lastMessageOrder and ord >= :lastMessageOrder - 10 order by ord asc")
   public abstract List<MessageItem> loadMessages(Integer chatId, Integer lastMessageOrder);

   @Transaction
   @Query("select * from ImageMessageItem where messageId = :messageId")
   public abstract ImageMessageItem loadImageMessage(Integer messageId);

   @Query("select * from TextMessageItem where messageId = :messageId")
   public abstract TextMessageItem loadTextMessage(Integer messageId);

   @Query("select * from IconMessageItem where messageId = :messageId")
   public abstract IconMessageItem loadIconMessage(Integer messageId);
}
