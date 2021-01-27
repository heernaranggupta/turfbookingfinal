package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class TransferClient extends ApiClient {

    TransferClient(String auth) {
        super(auth);
    }

    public Transfer create(JSONObject request) throws RazorpayException {
        return post(Constants.TRANSFER_CREATE, request);
    }

    public Transfer edit(String id, JSONObject request) throws RazorpayException {
        return patch(String.format(Constants.TRANSFER_EDIT, id), request);
    }

    public Reversal reversal(String id, JSONObject request) throws RazorpayException {
        return post(String.format(Constants.TRANSFER_REVERSAL_CREATE, id), request);
    }

    public Transfer fetch(String id) throws RazorpayException {
        return get(String.format(Constants.TRANSFER_GET, id), null);
    }

    public List<Transfer> fetchAll() throws RazorpayException {
        return fetchAll(null);
    }

    public List<Transfer> fetchAll(JSONObject request) throws RazorpayException {
        return getCollection(Constants.TRANSFER_LIST, request);
    }
}
