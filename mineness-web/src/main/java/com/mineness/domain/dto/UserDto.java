/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.dto;

//~--- non-JDK imports --------------------------------------------------------

import com.mineness.domain.document.UserSupplement;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.social.connect.UserProfile;

import java.util.Locale;

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 7/21/11
 * Time: 11:51 AM
 * Responsibility:
 */
public class UserDto {

    /** Field description */
    private String srnm;

    /** Field description */
    private Locale lcl;

    /** Field description */
    @NotEmpty
    @Email
    private String ml;

    /** Field description */
    @NotEmpty
    private String psswrd;

    private String lnm;

    private String fnm;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public UserDto() {}

    /**
     * Constructs ...
     *
     *
     * @param us user
     */
    public UserDto(UserSupplement us) {
        this.ml  = us.getMl();
        this.srnm = us.getSrnm();
        this.fnm = us.getFnm();
        this.lnm = us.getLnm();
    }

    /**
     * Constructs ...
     *
     *
     * @param profile profile
     */
    public UserDto(UserProfile profile) {
        this.ml  = profile.getEmail();
        this.srnm = profile.getFirstName();
        this.fnm = profile.getFirstName();
        this.lnm = profile.getLastName();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getSrnm() {
        return srnm;
    }

    public String getFnm() {
        return fnm;
    }

    public void setFnm(String fnm) {
        this.fnm = fnm;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public Locale getLcl() {
        return lcl;
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    public String getLnm() {
        return lnm;
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
    public String getPsswrd() {
        return psswrd;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param srnm srnm
     */
    public void setSrnm(String srnm) {
        this.srnm = srnm;
    }

    /**
     * Method description
     *
     *
     * @param lcl lcl
     */
    public void setLcl(Locale lcl) {
        this.lcl = lcl;
    }

    /**
     * Method description
     *
     *
     * @param lnm lnm
     */
    public void setLnm(String lnm) {
        this.lnm = lnm;
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
     * @param psswrd psswrd
     */
    public void setPsswrd(String psswrd) {
        this.psswrd = psswrd;
    }
}
