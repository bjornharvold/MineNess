/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.domain.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 5/15/12
 * Time: 4:56 PM
 * Responsibility:
 */
public class UserSearchQuery {
    private String fnm;
    private String lnm;
    private String ml;
    private List<String> fields;
    private Date cdt;
    private Date ldt;

    public String getFnm() {
        return fnm;
    }

    public void setFnm(String fnm) {
        this.fnm = fnm;
    }

    public String getLnm() {
        return lnm;
    }

    public void setLnm(String lnm) {
        this.lnm = lnm;
    }

    public String getMl() {
        return ml;
    }

    public void setMl(String ml) {
        this.ml = ml;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Date getCdt() {
        return cdt;
    }

    public void setCdt(Date cdt) {
        this.cdt = cdt;
    }

    public Date getLdt() {
        return ldt;
    }

    public void setLdt(Date ldt) {
        this.ldt = ldt;
    }
}
