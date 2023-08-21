package com.bit.eduventure.livestation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveStationInfoDTO {
    private String channelId;
    private String channelName;

    private int cdnInstanceNo;
    private String cdnStatus;

    private String publishUrl;
    private String streamKey;
}
