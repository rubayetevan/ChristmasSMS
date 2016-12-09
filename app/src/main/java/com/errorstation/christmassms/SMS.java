
package com.errorstation.christmassms;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SMS {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("sms")
    @Expose
    private List<Sm> sms = null;

    /**
     * 
     * @return
     *     The success
     */
    public String getSuccess() {
        return success;
    }

    /**
     * 
     * @param success
     *     The success
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * 
     * @return
     *     The sms
     */
    public List<Sm> getSms() {
        return sms;
    }

    /**
     * 
     * @param sms
     *     The sms
     */
    public void setSms(List<Sm> sms) {
        this.sms = sms;
    }

}
