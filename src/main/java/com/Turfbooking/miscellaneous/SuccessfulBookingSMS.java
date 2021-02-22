package com.Turfbooking.miscellaneous;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfulBookingSMS {

//    public static String slotCountReplacementPart;
//    public static String userReplacementPart;
//    public static String urlReplacementPart;
//    public static String contactReplacementPart;
//    public static String baseMessageForSuccessfulBookingSMS = "Dear+"+userReplacementPart+",+Your+booking+of+"+slotCountReplacementPart+"+slots+at+Rebounce+Turf+is+confirmed.+You+can+view+your+booking+on+"+urlReplacementPart+"+by+signing+in.+For+any+change+call+or+Whatapp+on+"+contactReplacementPart+".+Thanking+you+for+choosing+REBOUNCE!";

    public static final String baseURLForOTPService = "http://msg.balajitech.co.in/api/mt/SendSMS";

    public SuccessfulBookingSMS(String slotCountReplacementPart, String userReplacementPart, String urlReplacementPart, String contactReplacementPart) {
//
//        this.slotCountReplacementPart = slotCountReplacementPart;
//        this.userReplacementPart = userReplacementPart;
//        this.urlReplacementPart = urlReplacementPart;
//        this.contactReplacementPart = contactReplacementPart;
    }
}


//    Dear+{USER},+Your+booking+of+{SLOT_COUNT}+slots+at+Rebounce+Turf+is+confirmed.+You+can+view+your+booking+on+{URL}+by+signing+in.+For+any+change+call+or+Whatapp+on+{CONTACT_NUMBER}.+Thanking+you+for+choosing+REBOUNCE!