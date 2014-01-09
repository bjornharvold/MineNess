package com.mineness.domain.document;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by Bjorn Harvold
 * Date: 4/11/13
 * Time: 3:11 PM
 * Responsibility:
 */
public class AbstractDocument {

    @Id
    private ObjectId id;

    /** Created date */
    private Date cdt;

    /** Last update */
    private Date ldt;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getIdString() {
        String result = null;

        if (id != null) {
            result = id.toString();
        }

        return result;
    }

}
