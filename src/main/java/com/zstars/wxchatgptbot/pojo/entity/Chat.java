package com.zstars.wxchatgptbot.pojo.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @TableName Chat
 */
@Data
@Getter
@Setter
public class Chat implements Serializable {
    /**
     * 
     */
    
    private Integer chatid;
    
    private String userid;
    
    private String name;
    
    private String prompt;
    
    private String promptanswer;
    
    private String timestamp;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Chat other = (Chat) that;
        return (this.getChatid() == null ? other.getChatid() == null : this.getChatid().equals(other.getChatid()))
            && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPrompt() == null ? other.getPrompt() == null : this.getPrompt().equals(other.getPrompt()))
            && (this.getPromptanswer() == null ? other.getPromptanswer() == null : this.getPromptanswer().equals(other.getPromptanswer()))
            && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getChatid() == null) ? 0 : getChatid().hashCode());
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPrompt() == null) ? 0 : getPrompt().hashCode());
        result = prime * result + ((getPromptanswer() == null) ? 0 : getPromptanswer().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", chatid=" + chatid +
                ", userid=" + userid +
                ", name=" + name +
                ", prompt=" + prompt +
                ", promptanswer=" + promptanswer +
                ", timestamp=" + timestamp +
                ", serialVersionUID=" + serialVersionUID +
                "]";
    }


}