package com.Turfbooking.razorpay.response;

import com.Turfbooking.razorpay.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {

    private String id;
    private String createdAt;
    private String description;

    private String contact;
    private String email;
    private List<String> notes;

    private String method;
    private String currency;
    private Float amount;
    private String bank;
    private String vpa;

    private String captured;

    private Float amountRefunded;
    private String refundStatus;

    private String error_reason;
    private String error_description;
    private String errorCode;

    private String invoiceID;
    private Float tax;
    private String status;

    private String cardID;
    private String orderID;

    private Float fee;


    public PaymentResponse(Payment payment) {

        this.id = payment.get("id").toString();
        this.createdAt = payment.get("created_at").toString();
        this.description = payment.get("description").toString();
        this.contact = payment.get("contact").toString();
        this.email = payment.get("email").toString();
        this.method = payment.get("method").toString();
        this.currency = payment.get("currency").toString();
        this.amount = Float.parseFloat(payment.get("amount").toString()) / 100;
        this.bank = payment.get("bank").toString();
        this.vpa = payment.get("vpa").toString();
        this.captured = payment.get("captured").toString();
        this.amountRefunded = Float.parseFloat(payment.get("amount_refunded").toString()) / 100;
        this.refundStatus = payment.get("refund_status").toString();
        this.error_reason = payment.get("error_reason").toString();
        this.error_description = payment.get("error_description").toString();
        this.errorCode = payment.get("error_code").toString();
        this.invoiceID = payment.get("invoice_id").toString();
        if (!payment.get("tax").toString().equalsIgnoreCase("null")) {
            this.tax = Float.parseFloat(payment.get("tax").toString()) / 100; //check null exception
//            this.tax = this.tax ;
        }
        this.status = payment.get("status").toString();
        this.cardID = payment.get("card_id").toString();
        this.orderID = payment.get("order_id").toString();
        if (!payment.get("fee").toString().equalsIgnoreCase("null")) {
            this.fee = Float.parseFloat(payment.get("fee").toString()) / 100;
//            this.fee = this.fee / 100;
        }
        if (!payment.get("status").toString().equalsIgnoreCase("failed")) {
            JSONObject note = payment.get("notes");
            notes = new ArrayList<>();
            this.notes.add(note.get("address").toString());
        } else {
            JSONArray notes = payment.get("notes");
            if (!notes.isEmpty()) {
                for (Object o : notes) {
                    this.notes.add(o.toString());
                }
            }
        }

    }
}
