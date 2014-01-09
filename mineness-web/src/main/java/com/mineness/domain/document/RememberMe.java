/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.document;

//~--- non-JDK imports --------------------------------------------------------

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.io.Serializable;
import java.util.Date;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 9/23/11
 * Time: 12:57 PM
 * Responsibility:
 */
@Document
public class RememberMe extends AbstractDocument implements Serializable {

    /** Field description */
    private static final long serialVersionUID = 9077896528288879022L;

    //~--- fields -------------------------------------------------------------

    /** Date */
    private Date dt;

    /** Email */
    private String ml;

    /** Series */
    private String srs;

    /** Token value */
    private String tkn;

    public RememberMe() {
    }

    public RememberMe(PersistentRememberMeToken token) {
        this.ml = token.getUsername();
        this.srs = token.getSeries();
        this.tkn = token.getTokenValue();
        this.dt = token.getDate();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Date getDt() {
        return dt;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getMl() {
        return ml;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getSrs() {
        return srs;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getTkn() {
        return tkn;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param dt dt
     */
    public void setDt(Date dt) {
        this.dt = dt;
    }

    /**
     * Method description
     *
     *
     * @param ml ml
     */
    public void setMl(String ml) {
        this.ml = ml;
    }

    /**
     * Method description
     *
     *
     * @param srs srs
     */
    public void setSrs(String srs) {
        this.srs = srs;
    }

    /**
     * Method description
     *
     *
     * @param tkn tkn
     */
    public void setTkn(String tkn) {
        this.tkn = tkn;
    }
}
