package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class InvoiceClient extends ApiClient {

    InvoiceClient(String auth) {
        super(auth);
    }

    public Invoice create(JSONObject request) throws RazorpayException {
        return post(Constants.INVOICE_CREATE, request);
    }

    public List<Invoice> fetchAll() throws RazorpayException {
        return fetchAll(null);
    }

    public List<Invoice> fetchAll(JSONObject request) throws RazorpayException {
        return getCollection(Constants.INVOICE_LIST, request);
    }

    public Invoice fetch(String id) throws RazorpayException {
        return get(String.format(Constants.INVOICE_GET, id), null);
    }

    public Invoice cancel(String id) throws RazorpayException {
        return post(String.format(Constants.INVOICE_CANCEL, id), null);
    }
}
