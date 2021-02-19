package com.Turfbooking.models.externalCalls;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalOtpCallResponse implements Serializable {

    private static final long serialVersionUID = -6190539474319446094L;

//    {"msg":"success","code":200,"msg_text":"SMS campaign has been submitted successfully",
//    "data":[{"campaign_id":14684,"number":"8511108666","message_id":"14684_1_8511108666_1"}]}

//    {"ErrorCode":"000","ErrorMessage":"Done","JobId":"283982",
//    "MessageData":[{"Number":"918460339810","MessageId":"GpK1btkNMEKztEJzFWOavQ"}]}

    @JsonProperty("ErrorCode")
    private String ErrorCode;
    @JsonProperty("ErrorMessage")
    private String ErrorMessage;
    @JsonProperty("JobId")
    private String JobId;
    @JsonProperty("MessageData")
    private List<ExternalOtpCallResponseData> MessageData;

}
