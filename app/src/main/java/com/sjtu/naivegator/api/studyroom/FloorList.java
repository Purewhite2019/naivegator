/**
  * Copyright 2021 bejson.com 
  */
package com.sjtu.naivegator.api.studyroom;
import java.util.List;

/**
 * Auto-generated: 2021-12-01 19:14:36
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FloorList {

    private int indexNum;
    private List<RoomStuNumbs> roomStuNumbs;
    private boolean hidden;
    private String fullName;
    private String freeRoom;
    private int parentNodeId;
    private String roomCode;
    private String type;
    private String codePath;
    private List<Children> children;
    private String name;
    private int id;
    private String text;
    private int nodeId;
    public void setIndexNum(int indexNum) {
         this.indexNum = indexNum;
     }
     public int getIndexNum() {
         return indexNum;
     }

    public void setRoomStuNumbs(List<RoomStuNumbs> roomStuNumbs) {
         this.roomStuNumbs = roomStuNumbs;
     }
     public List<RoomStuNumbs> getRoomStuNumbs() {
         return roomStuNumbs;
     }

    public void setHidden(boolean hidden) {
         this.hidden = hidden;
     }
     public boolean getHidden() {
         return hidden;
     }

    public void setFullName(String fullName) {
         this.fullName = fullName;
     }
     public String getFullName() {
         return fullName;
     }

    public void setFreeRoom(String freeRoom) {
         this.freeRoom = freeRoom;
     }
     public String getFreeRoom() {
         return freeRoom;
     }

    public void setParentNodeId(int parentNodeId) {
         this.parentNodeId = parentNodeId;
     }
     public int getParentNodeId() {
         return parentNodeId;
     }

    public void setRoomCode(String roomCode) {
         this.roomCode = roomCode;
     }
     public String getRoomCode() {
         return roomCode;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setCodePath(String codePath) {
         this.codePath = codePath;
     }
     public String getCodePath() {
         return codePath;
     }

    public void setChildren(List<Children> children) {
         this.children = children;
     }
     public List<Children> getChildren() {
         return children;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setId(int id) {
         this.id = id;
     }
     public int getId() {
         return id;
     }

    public void setText(String text) {
         this.text = text;
     }
     public String getText() {
         return text;
     }

    public void setNodeId(int nodeId) {
         this.nodeId = nodeId;
     }
     public int getNodeId() {
         return nodeId;
     }

}