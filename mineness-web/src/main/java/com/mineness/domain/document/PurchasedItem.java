package com.mineness.domain.document;

import com.mineness.domain.enums.PurchaseStatus;
import org.springframework.data.annotation.Persistent;
import org.springframework.util.Assert;

import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Persistent
public class PurchasedItem extends AbstractDocument implements Serializable {

    private static final long serialVersionUID = 767609828187990509L;

    /** All properties taken from product apis */
    private Map<String, Attribute> ttrbts;

    /** User created tags */
    private List<String> tgs;

    /** The order this item was purchased under */
    private Order rdr;

    /** Where in the purchase process is this item */
    @Enumerated
    private PurchaseStatus sts;

    public PurchasedItem() {
    }

    public PurchasedItem(Order order) {
        Assert.hasText(order.getWnr());
        Assert.notNull(order.getVndr());

        // when the object is created the first time like this, we set it to confirmed
        this.sts = PurchaseStatus.CONFIRMED;

        this.rdr = order;
    }

    public Map<String, Attribute> getTtrbts() {
        return ttrbts;
    }

    public void setTtrbts(Map<String, Attribute> ttrbts) {
        this.ttrbts = ttrbts;
    }

    public List<String> getTgs() {
        return tgs;
    }

    public void setTgs(List<String> tgs) {
        this.tgs = tgs;
    }

    public Order getRdr() {
        return rdr;
    }

    public void setRdr(Order rdr) {
        this.rdr = rdr;
    }

    public PurchaseStatus getSts() {
        return sts;
    }

    public void setSts(PurchaseStatus sts) {
        this.sts = sts;
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
}
