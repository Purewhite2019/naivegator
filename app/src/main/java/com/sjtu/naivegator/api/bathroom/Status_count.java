/**
  * Copyright 2021 bejson.com 
  */
package com.sjtu.naivegator.api.bathroom;
import java.util.List;

/**
 * Auto-generated: 2021-12-01 22:55:46
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Status_count {

    private int free;
    private int used;
    private int error;
    private List<Pos_info> pos_info;
    public void setFree(int free) {
         this.free = free;
     }
     public int getFree() {
         return free;
     }

    public void setUsed(int used) {
         this.used = used;
     }
     public int getUsed() {
         return used;
     }

    public void setError(int error) {
         this.error = error;
     }
     public int getError() {
         return error;
     }

    public void setPos_info(List<Pos_info> pos_info) {
         this.pos_info = pos_info;
     }
     public List<Pos_info> getPos_info() {
         return pos_info;
     }

}