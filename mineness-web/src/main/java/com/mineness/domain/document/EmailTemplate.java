package com.mineness.domain.document;

import com.mineness.domain.enums.EmailTemplateContentType;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.mineness.domain.enums.Vendor;
import org.springframework.data.annotation.Persistent;

@Persistent
public class EmailTemplate extends AbstractDocument implements Serializable {

    private static final long serialVersionUID = -8388945289301518956L;

    /** Descriptive label for template */
    @NotNull
    private String lbl;

    /** Which version of this template is it */
    @NotNull
    private String vrsn;

    /** Which online retailer is this template for */
    @NotNull
    @Enumerated
    private Vendor vndr;

    /** Which locale is the vendor in */
    @NotNull
    private Locale lcl;

    /** Do we expect html or plain text emails from this vendor */
    @NotNull
    @Enumerated
    private EmailTemplateContentType cntntTp;

    /** Strings that need to match before we determine a template match */
    private List<String> mtch;

    /** List of xpath expressions that lead to data that should be looped through and grouped with an item */
    private List<EmailPropertyProductGroup> grps;

    /** List of xpath expressions that lead to data global to
    the order and should most likely be made available to all items in the order */
    private List<EmailProperty> prps;

    /** Service name of bean to handle parsing */
    @NotNull
    private String srvc;

    @NotNull
    private EmailProperty rdrd;

    public String getLbl() {
        return lbl;
    }

    public void setLbl(String lbl) {
        this.lbl = lbl;
    }

    public String getVrsn() {
        return this.vrsn;
    }

    public void setVrsn(String vrsn) {
        this.vrsn = vrsn;
    }

    public Vendor getVndr() {
        return this.vndr;
    }

    public void setVndr(Vendor vndr) {
        this.vndr = vndr;
    }

    public Locale getLcl() {
        return this.lcl;
    }

    public void setLcl(Locale lcl) {
        this.lcl = lcl;
    }

    public EmailTemplateContentType getCntntTp() {
        return this.cntntTp;
    }

    public void setCntntTp(EmailTemplateContentType cntntTp) {
        this.cntntTp = cntntTp;
    }

    public List<String> getMtch() {
        return mtch;
    }

    public void setMtch(List<String> mtch) {
        this.mtch = mtch;
    }

    public String getSrvc() {
        return srvc;
    }

    public void setSrvc(String srvc) {
        this.srvc = srvc;
    }

    public List<EmailPropertyProductGroup> getGrps() {
        return grps;
    }

    public void setGrps(List<EmailPropertyProductGroup> grps) {
        this.grps = grps;
    }

    public List<EmailProperty> getPrps() {
        return prps;
    }

    public void setPrps(List<EmailProperty> prps) {
        this.prps = prps;
    }

    public EmailProperty getRdrd() {
        return rdrd;
    }

    public void setRdrd(EmailProperty rdrd) {
        this.rdrd = rdrd;
    }
}
