/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.domain.document;

import com.mineness.ApplicationConstants;
import com.mineness.domain.enums.Gender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 8/7/12
 * Time: 10:07 PM
 * Responsibility:
 */
@Persistent
public class UserSupplement extends AbstractDocument implements Serializable {
    private static final long serialVersionUID = 6172102508023958255L;

    /**
     * User code
     */
    @Indexed(unique = true)
    private String cd;

    /**
     * Social network connections
     */
    private List<Social> scls;

    /**
     * Date of Birth
     */
    private Date db;

    /**
     * Gender
     */
    private Gender gndr;

    /**
     * Locale
     */
    private Locale lcl = Locale.US;    // default

    /**
     * First name
     */
    private String fnm;

    /**
     * Last name
     */
    private String lnm;

    /**
     * Duplicate of User.ml
     */
    private String ml;

    /**
     * Duplicate of User.srnm
     */
    private String srnm;

    public UserSupplement() {
    }

    public UserSupplement(String cd) {
        this.cd = cd;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public List<Social> getScls() {
        return scls;
    }

    public void setScls(List<Social> scls) {
        this.scls = scls;
    }

    public Date getDb() {
        return db;
    }

    public void setDb(Date db) {
        this.db = db;
    }

    public Gender getGndr() {
        return gndr;
    }

    public void setGndr(Gender gndr) {
        this.gndr = gndr;
    }

    public Locale getLcl() {
        return lcl;
    }

    public String getLnm() {
        return lnm;
    }

    public String getMl() {
        return ml;
    }

    public void setLcl(Locale lcl) {
        this.lcl = lcl;
    }

    public void setLnm(String lnm) {
        this.lnm = lnm;
    }

    public void setMl(String ml) {
        this.ml = ml;
    }

    public String getFnm() {
        return fnm;
    }

    public void setFnm(String fnm) {
        this.fnm = fnm;
    }

    public String getSrnm() {
        return srnm;
    }

    public void setSrnm(String srnm) {
        this.srnm = srnm;
    }

    public Social getSocial(String providerId) {

        if ((scls != null) && !scls.isEmpty()) {

            for (Social social : scls) {
                if (StringUtils.equals(social.getProviderId(), providerId)) {
                    return social;
                }
            }
        }

        return null;
    }

    public Map<String, Social> getSocials() {
        Map<String, Social> result = null;

        if ((scls != null) && !scls.isEmpty()) {
            result = new HashMap<String, Social>();

            for (Social social : scls) {
                result.put(social.getProviderId(), social);
            }
        }

        return result;
    }

    public Boolean getFacebook() {
        return getSocial(ApplicationConstants.FACEBOOK) != null;
    }


}
