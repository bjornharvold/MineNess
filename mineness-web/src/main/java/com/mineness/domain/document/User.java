/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.document;

//~--- non-JDK imports --------------------------------------------------------

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.mineness.domain.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.social.connect.UserProfile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 6/17/11
 * Time: 4:28 PM
 * Responsibility:
 */
@Persistent
public class User extends AbstractDocument implements Serializable {

    /**
     * Field description
     */
    private static final long serialVersionUID = -5850171832971023139L;

    @Transient
    private final static EthernetAddress nic = EthernetAddress.fromInterface();

    @Transient
    private final static TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);

    public User(UserProfile dto) {
        this.ml = dto.getEmail();
        this.srnm = dto.getUsername();
    }

    public User(UserDto dto) {
        this.ml = dto.getMl();
        this.srnm = dto.getSrnm();
    }

    /**
     * Static method to generate a User Code value
     * @return
     */
    public static String generateUserCode() {
        return uuidGenerator.generate().toString();
    }

    //~--- fields -------------------------------------------------------------

    /**
     * Account Not Expired
     */
    private Boolean nxprd = true;

    /**
     * Account not Locked
     */
    private Boolean nlckd = true;

    /**
     * Enabled
     */
    private Boolean nbld = true;

    /**
     * Credentials Non expired
     */
    private Boolean crdnxprd = true;

    /**
     * Role url names
     */
    private List<String> rrlnms = new ArrayList<String>();

    /**
     * Checksum that we can use to uniquely identify user with using something public like email
     * This will be overwritten by stored database user.cd
     */
    private String cd = generateUserCode();

    /**
     * Email
     */
    @NotNull
    private String ml;

    @NotNull
    @Indexed(unique = true)
    private String srnm;

    /**
     * Password
     */
    @NotNull
    private String psswrd;

    /**
     * User roles
     */
    @Transient
    private List<Role> roles = new ArrayList<Role>();

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     */
    public User() {
    }

    /**
     * Construct an anonymous user with a specified user code
     */
    public User(String userCode) {
        if (userCode != null && userCode.length() > 0) {
            this.cd = userCode;
        }
    }

    //~--- methods ------------------------------------------------------------

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @return Return value
     */
    public String getCd() {
        return cd;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Boolean getCrdnxprd() {
        return crdnxprd;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getMl() {
        return ml;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Boolean getNbld() {
        return nbld;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Boolean getNlckd() {
        return nlckd;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public Boolean getNxprd() {
        return nxprd;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public String getPsswrd() {
        return psswrd;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Method description
     *
     * @return Return value
     */
    public List<String> getRrlnms() {
        return rrlnms;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param cd cd
     */
    public void setCd(String cd) {
        if (cd != null && cd.length() > 0) {
            this.cd = cd;
        }
    }

    /**
     * Method description
     *
     * @param crdnxprd credentialsNonExpired
     */
    public void setCrdnxprd(Boolean crdnxprd) {
        this.crdnxprd = crdnxprd;
    }

    /**
     * Method description
     *
     * @param ml email
     */
    public void setMl(String ml) {
        this.ml = ml;
    }

    /**
     * Method description
     *
     * @param nbld enabled
     */
    public void setNbld(Boolean nbld) {
        this.nbld = nbld;
    }

    /**
     * Method description
     *
     * @param nlckd accountNonLocked
     */
    public void setNlckd(Boolean nlckd) {
        this.nlckd = nlckd;
    }

    /**
     * Method description
     *
     * @param nxprd accountNonExpired
     */
    public void setNxprd(Boolean nxprd) {
        this.nxprd = nxprd;
    }

    /**
     * Method description
     *
     * @param psswrd password
     */
    public void setPsswrd(String psswrd) {
        this.psswrd = psswrd;
    }

    /**
     * Method description
     *
     * @param roles roles
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getSrnm() {
        return srnm;
    }

    public void setSrnm(String srnm) {
        this.srnm = srnm;
    }

    /**
     * Method description
     *
     * @param rrlnms roleIds
     */
    public void setRrlnms(List<String> rrlnms) {
        this.rrlnms = rrlnms;
    }

    public void addRole(String roleUrlName) {
        if (this.rrlnms == null) {
            this.rrlnms = new ArrayList<String>();
        }

        boolean dupe = false;

        for (String rrlnm : rrlnms) {
            if (StringUtils.equals(roleUrlName, rrlnm)) {
                dupe = true;
                break;
            }
        }

        if (!dupe) {
            this.rrlnms.add(roleUrlName);
        }
    }

    public void removeRole(String roleUrlName) {
        if (this.rrlnms != null) {
            String toRemove = null;

            for (String rrlnm : rrlnms) {
                if (StringUtils.equals(roleUrlName, rrlnm)) {
                    toRemove = rrlnm;
                    break;
                }
            }

            if (StringUtils.isNotBlank(toRemove)) {
                this.rrlnms.remove(toRemove);
            }
        }
    }
}
