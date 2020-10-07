package com.Turfbooking.models.externalCalls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalOtpCallResponseData implements Serializable {

    private static final long serialVersionUID = 8825136144094224471L;

    private Integer campaign_id;
    private String number;
    private String message_id;
    //{"msg":"success","code":200,"msg_text":"SMS campaign has been submitted
    //successfully","data":[{"campaign_id":14684,"number":"8511108666","message_id":"14684_1_8511108666_1"}]}


    @Override
    public String toString() {
        return "ExternalOtpCallResponseData{" +
                "campaign_id=" + campaign_id +
                ", number='" + number + '\'' +
                ", message_id='" + message_id + '\'' +
                '}';
    }
}
