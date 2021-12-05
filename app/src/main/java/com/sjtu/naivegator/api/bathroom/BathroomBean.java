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
public class BathroomBean {

    private String status;
    private String message;
    private List<Data> data;
    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

}