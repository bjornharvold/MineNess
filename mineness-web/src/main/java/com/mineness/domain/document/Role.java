/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.document;

//~--- non-JDK imports --------------------------------------------------------

import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 6/19/11
 * Time: 1:04 PM
 * Responsibility:
 */
@Persistent
public class Role extends AbstractDocument implements Serializable {

    /** Field description */
    private static final long serialVersionUID = -3836008580458749250L;

    //~--- fields -------------------------------------------------------------

    /** Rights */
    private List<String> rghts = new ArrayList<String>();

    /** Name */
    private String nm;

    /** Url name */
    private String rlnm;

    /** Description */
    private String dscrptn;

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getNm() {
        return nm;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public List<String> getRghts() {
        return rghts;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getRlnm() {
        return rlnm;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param nm nm
     */
    public void setNm(String nm) {
        this.nm = nm;
    }

    /**
     * Method description
     *
     *
     * @param rghts rghts
     */
    public void setRghts(List<String> rghts) {
        this.rghts = rghts;
    }

    /**
     * Method description
     *
     *
     * @param rlnm rlnm
     */
    public void setRlnm(String rlnm) {
        this.rlnm = rlnm;
    }

    public String getDscrptn() {
        return dscrptn;
    }

    public void setDscrptn(String dscrptn) {
        this.dscrptn = dscrptn;
    }
}
