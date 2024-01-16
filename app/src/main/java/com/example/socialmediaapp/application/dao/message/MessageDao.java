package com.example.socialmediaapp.application.dao.message;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.message.IconMessageItem;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.pend.PendTask;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;

import java.util.List;

@Dao
public abstract class MessageDao {

  @Insert
  public abstract long insert(MessageItem messageItem);

  @Update
  public abstract void update(MessageItem messageItem);

  @Query("delete from MessageItem where id = :id")
  public abstract void deleteById(Integer id);

  @Update
  public abstract void updateImageMessage(ImageMessageItem messageItem);

  @Insert
  public abstract long insertImageMessageItem(ImageMessageItem messageItem);

  @Insert
  public abstract long insertTextMessageItem(TextMessageItem messageItem);

  @Insert
  public abstract long insertIconMessageItem(IconMessageItem messageItem);

  @Query("select min(ord) from MessageItem where chatId = :chatId")
  public abstract Integer lastMessageOrder(String chatId);

  @Query("select * from MessageItem where chatId = :chatId and time < :lastMessageTime order by time desc limit 10")
  public abstract List<MessageItem> loadByTime(String chatId, Long lastMessageTime);

  @Query("select count(*) from MessageItem where chatId = :chatId and ord = :ord")
  public abstract int countMessage(String chatId, Integer ord);

  @Query("select count(*) from MessageItem where ord = :ord")
  public abstract int findByOrder(int ord);

  @Query("update MessageItem set pendId = :pendId where id =:id")
  public abstract void updatePendId(String pendId, Integer id);

  @Query("select * from MessageItem where id =:id")
  public abstract MessageItem findMessageById(Integer id);

  @Transaction
  @Query("select * from ImageMessageItem where messageId = :messageId")
  public abstract ImageMessageItem loadImageMessage(Integer messageId);

  @Query("select * from TextMessageItem where messageId = :messageId")
  public abstract TextMessageItem loadTextMessage(Integer messageId);

  @Query("select * from IconMessageItem where messageId = :messageId")
  public abstract IconMessageItem loadIconMessage(Integer messageId);

  @Query("delete from MessageItem")
  public abstract void deleteAllMessage();

  @Query("select * from PendTask order by ord")
  public abstract List<PendTask> loadAllPendingMessages();

  @Insert
  public abstract long insertPendingMessage(PendTask message);

  @Query("delete from PendTask where id = :id")
  public abstract void deletePendingMessageById(int id);

  @Query("select * from PendTask where id = :id")
  public abstract PendTask findPendingMessage(int id);

  @Query("select * from PendTask where msgId = :msgId")
  public abstract PendTask findPendingByMessage(int msgId);

}
