package com.Turfbooking.models.externalCalls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalOtpCallResponse implements Serializable {

    private static final long serialVersionUID = -713662202633809526L;

//    {"msg":"success","code":200,"msg_text":"SMS campaign has been submitted
//        successfully","data":[{"campaign_id":14684,"number":"8511108666","message_id":"14684_1_8511108666_1"}]}

    private String msg;
    private Integer code;
    private String msg_text;
    private List<ExternalOtpCallResponseData> data;

    @Override
    public String toString() {

        return "ExternalOtpCallResponse{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", msg_text='" + msg_text + '\'' +
                '}';
    }

    private String allData() {
        String response = "";
        for (ExternalOtpCallResponseData ex : data) {
            response.concat(ex.toString());
        }
        return response;
    }
}
