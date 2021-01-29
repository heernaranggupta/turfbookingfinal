package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class PlanClient extends ApiClient {

    PlanClient(String auth) {
        super(auth);
    }

    public Plan create(JSONObject request) throws RazorpayException {
        return post(Constants.PLAN_CREATE, request);
    }

    public Plan fetch(String id) throws RazorpayException {
        return get(String.format(Constants.PLAN_GET, id), null);
    }

    public List<Plan> fetchAll() throws RazorpayException {
        return fetchAll(null);
    }

    public List<Plan> fetchAll(JSONObject request) throws RazorpayException {
        return getCollection(Constants.PLAN_LIST, request);
    }
}
