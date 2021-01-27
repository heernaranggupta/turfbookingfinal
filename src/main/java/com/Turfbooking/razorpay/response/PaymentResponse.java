package com.Turfbooking.razorpay.response;

import com.Turfbooking.razorpay.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {

    private String id;
    private String createdAt;
    private String description;

    private String contact;
    private String email;
    private String address;

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
    private String tax;
    private String status;

    private String cardID;
    private String orderID;

    private String fee;


    public PaymentResponse(Payment payment) {

        JSONObject notes = payment.get("notes");

        this.id = payment.get("id").toString();
        this.createdAt = payment.get("created_at").toString();
        this.description = payment.get("description").toString();
        this.contact = payment.get("contact").toString();
        this.email = payment.get("email").toString();
        this.address = notes.get("address").toString();
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
        this.tax = payment.get("tax").toString();
        this.status = payment.get("status").toString();
        this.cardID = payment.get("card_id").toString();
        this.orderID = payment.get("order_id").toString();
        this.fee = payment.get("fee").toString();
    }
}
