package com.example.socialmediaapp.application.entity.pend;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = PendTask.class,
        parentColumns = "id",
        childColumns = "pendId",
        onDelete = ForeignKey.CASCADE
))
public class PendInput {
  @PrimaryKey(autoGenerate = true)
  private Integer id;
  private String pendId;
  private String key;
  private String value;


  /* Only belongs to java.lang */
  private String className;

  public String getPendId() {
    return pendId;
  }

  public void setPendId(String pendId) {
    this.pendId = pendId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}
