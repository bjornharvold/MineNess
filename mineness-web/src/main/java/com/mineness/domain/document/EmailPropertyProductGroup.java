package com.mineness.domain.document;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 5/27/13
 * Time: 10:04 PM
 * Responsibility:
 */
public class EmailPropertyProductGroup implements Serializable {
    private List<EmailProperty> prprts;

    public List<EmailProperty> getPrprts() {
        return prprts;
    }

    public void setPrprts(List<EmailProperty> prprts) {
        this.prprts = prprts;
    }
}
