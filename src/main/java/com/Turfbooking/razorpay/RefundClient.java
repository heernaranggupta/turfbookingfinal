package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class RefundClient extends ApiClient {

    RefundClient(String auth) {
        super(auth);
    }

    public Refund create(JSONObject request) throws RazorpayException {
        return post(Constants.REFUND_CREATE, request);
    }

    public List<Refund> fetchAll(JSONObject request) throws RazorpayException {
        return getCollection(Constants.REFUND_LIST, request);
    }

    public Refund fetch(String id) throws RazorpayException {
        return get(String.format(Constants.REFUND_GET, id), null);
    }

    public List<Refund> fetchAll() throws RazorpayException {
        return fetchAll(null);
    }
}
