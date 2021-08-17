package com.Turfbooking.razorpay.response;

import com.Turfbooking.razorpay.Refund;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;

import java.util.List;

@Getter
@Setter
public class RefundResponse {

    private String id;
    private String batch_id;
    private String created_at;
    private String payment_id;
    private String currency;
    private Float amount;
    private String speed_requested;
    private String speed_processed;
    private String receipt;
    private String status;
    private List<String> notes;

    public RefundResponse(Refund refund) {
        JSONArray noteObject = refund.get("notes");
        this.id = refund.get("id").toString();
        this.batch_id = refund.get("batch_id").toString();
        this.created_at = refund.get("created_at").toString();
        this.payment_id = refund.get("payment_id").toString();
        this.currency = refund.get("currency").toString();
        this.amount = Float.parseFloat(refund.get("amount").toString()) / 100;
        this.speed_requested = refund.get("speed_requested").toString();
        this.speed_processed = refund.get("speed_processed").toString();
        this.receipt = refund.get("receipt").toString();
        this.status = refund.get("status").toString();
        if (!noteObject.isEmpty()) {
            for (Object object : noteObject) {
                this.notes.add(object.toString());
            }
        }
    }
}
