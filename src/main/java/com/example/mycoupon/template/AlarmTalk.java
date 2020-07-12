package com.example.mycoupon.template;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmTalk {
    private String mediaId;
    private String phoneNumber;

    @Builder
    public AlarmTalk(String mediaId, String phoneNumber) {
        this.mediaId = mediaId;
        this.phoneNumber = phoneNumber;
    }
}
