package com.Turfbooking.razorpay;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AddonClient extends ApiClient {

    AddonClient(String auth) {
        super(auth);
    }

    // To create an Addon, use the createAddon method of SubscriptionClient
    public Addon fetch(String id) throws RazorpayException {
        return get(String.format(Constants.ADDON_GET, id), null);
    }

    public void delete(String id) throws RazorpayException {
        delete(String.format(Constants.ADDON_DELETE, id), null);
    }
}
