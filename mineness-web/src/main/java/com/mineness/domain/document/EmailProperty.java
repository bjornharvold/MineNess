package com.mineness.domain.document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Bjorn Harvold
 * Date: 4/1/13
 * Time: 11:40 AM
 * Responsibility:
 */
public class EmailProperty implements Serializable {
    private static final long serialVersionUID = -7632086481724568224L;

    /** The name of this key to use for retrieving it */
    @NotNull
    private String ky;

    /** The xpath query */
    /** Sample: "//a[starts-with(@href, 'http://www.amazon.com/dp/') and not(child::img)]" */
    @NotNull
    private String xpth;

    /** How to navigate from the xpath result to the actual value we want */
    /** Sample: "parent|parent|first-sibling|first-sibling|value()" */
    @NotNull
    private String nvgtn;

    /** Is this property required */
    @NotNull
    private Boolean rqrd;

    /** The lbl to give the product attribute */
    @NotNull
    private String lbl;

    public String getKy() {
        return ky;
    }

    public void setKy(String key) {
        this.ky = key;
    }

    public String getXpth() {
        return xpth;
    }

    public void setXpth(String xpth) {
        this.xpth = xpth;
    }

    public String getNvgtn() {
        return nvgtn;
    }

    public void setNvgtn(String nvgtn) {
        this.nvgtn = nvgtn;
    }

    public Boolean getRqrd() {
        return rqrd;
    }

    public void setRqrd(Boolean required) {
        this.rqrd = required;
    }

    public String getLbl() {
        return lbl;
    }

    public void setLbl(String lbl) {
        this.lbl = lbl;
    }
}
