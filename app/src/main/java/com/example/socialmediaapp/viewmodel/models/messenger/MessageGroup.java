package com.example.socialmediaapp.viewmodel.models.messenger;


import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class MessageGroup {
    private UserBasicInfo partner;
    private long start, end;

    public MessageGroup(UserBasicInfo partner, long start, long end) {
        this.partner = partner;
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public UserBasicInfo getPartner() {
        return partner;
    }

    public void setPartner(UserBasicInfo partner) {
        this.partner = partner;
    }


}
