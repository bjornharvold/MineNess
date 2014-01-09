package com.mineness.domain.document;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.mineness.domain.enums.Vendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Transient;

import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 6/3/13
 * Time: 7:44 PM
 * Responsibility:
 */
@Persistent
public class Order extends AbstractDocument implements Serializable {
    private static final long serialVersionUID = 1098748838911436141L;
    private final static Logger log = LoggerFactory.getLogger(Order.class);

    @Transient
    private final static EthernetAddress nic = EthernetAddress.fromInterface();

    @Transient
    private final static TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);

    /**
     * Static method to generate a User Code value
     * @return
     */
    public static String generateUserCode() {
        return uuidGenerator.generate().toString();
    }

    /** The user's mineness email who purchased this product */
    private String wnr;

    @Enumerated
    private Vendor vndr;

    private String cd = generateUserCode();

    private String lbl;

    private Map<String, Attribute> ttrbts;

    private String rdrd;

    @Transient
    private List<PurchasedItem> items;

    public Order() {
    }

    public Order(String wnr, Vendor vndr) {
        this.wnr = wnr;
        this.vndr = vndr;
    }

    public String getLbl() {
        return lbl;
    }

    public void setLbl(String lbl) {
        this.lbl = lbl;
    }

    public Map<String, Attribute> getTtrbts() {
        return ttrbts;
    }

    public void setTtrbts(Map<String, Attribute> ttrbts) {
        this.ttrbts = ttrbts;
    }

    public String getWnr() {
        return wnr;
    }

    public void setWnr(String wnr) {
        this.wnr = wnr;
    }

    public Vendor getVndr() {
        return vndr;
    }

    public void setVndr(Vendor vndr) {
        this.vndr = vndr;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public List<PurchasedItem> getItems() {
        return items;
    }

    public void setItems(List<PurchasedItem> items) {
        this.items = items;
    }

    public String getRdrd() {
        return rdrd;
    }

    public void setRdrd(String rdrd) {
        this.rdrd = rdrd;
    }

    public void addAttribute(String key, String label, String value) {
        if (this.ttrbts == null) {
            this.ttrbts = new HashMap<>();
        }

        this.ttrbts.put(key, new Attribute(key, label, value));
    }

    public void removeAttribute(String key) {
        if (this.ttrbts != null && this.ttrbts.containsKey(key)) {
            this.ttrbts.remove(key);
        }
    }

    /**
     * Merge all data from the existing order found in the db with this new order
     * @param eo order
     */
    public void mergeExistingOrder(Order eo) {
        if (!eo.getRdrd().equals(this.rdrd)) {
            throw new IllegalStateException("Order IDs do not match");
        }

        this.setId(eo.getId());
        this.setCdt(eo.getCdt());
        this.setLdt(eo.getLdt());

        this.cd =  eo.getCd();
        this.lbl = eo.getLbl();
        this.wnr = eo.getWnr();
        this.vndr = eo.getVndr();
        this.rdrd = eo.getRdrd();

        if (eo.getTtrbts() != null && !eo.getTtrbts().isEmpty()) {
            if (this.ttrbts == null) {
                this.ttrbts = eo.getTtrbts();
            } else {
                this.ttrbts.putAll(eo.getTtrbts());
            }
        }

    }
}
