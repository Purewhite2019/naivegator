/**
  * Copyright 2021 bejson.com 
  */
package com.sjtu.naivegator.api.bathroom;

/**
 * Auto-generated: 2021-12-01 22:55:46
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data {

    private int Id;
    private String Name;
    private int ParentId;
    private int GroupLevel;
    private Status_count status_count;
    public void setId(int Id) {
         this.Id = Id;
     }
     public int getId() {
         return Id;
     }

    public void setName(String Name) {
         this.Name = Name;
     }
     public String getName() {
         return Name;
     }

    public void setParentId(int ParentId) {
         this.ParentId = ParentId;
     }
     public int getParentId() {
         return ParentId;
     }

    public void setGroupLevel(int GroupLevel) {
         this.GroupLevel = GroupLevel;
     }
     public int getGroupLevel() {
         return GroupLevel;
     }

    public void setStatus_count(Status_count status_count) {
         this.status_count = status_count;
     }
     public Status_count getStatus_count() {
         return status_count;
     }

}