package com.mineness.domain.document;

import java.io.Serializable;

/**
 * Created by Bjorn Harvold
 * Date: 4/11/13
 * Time: 4:35 PM
 * Responsibility:
 */
public class Attribute implements Serializable {
    private static final long serialVersionUID = 6585410687339422436L;

    /** Key to retrieve it with */
    private String ky;

    /** Label of attribute */
    private String lbl;

    /** Value of key */
    private String vl;

    public Attribute() {
    }

    public Attribute(String key, String lbl, String vl) {
        this.ky = key;
        this.lbl = lbl;
        this.vl = vl;
    }

    public String getKy() {
        return ky;
    }

    public void setKy(String ky) {
        this.ky = ky;
    }

    public String getLbl() {
        return lbl;
    }

    public void setLbl(String lbl) {
        this.lbl = lbl;
    }

    public String getVl() {
        return vl;
    }

    public void setVl(String vl) {
        this.vl = vl;
    }
}
