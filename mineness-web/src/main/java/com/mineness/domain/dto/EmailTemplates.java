package com.mineness.domain.dto;

import com.mineness.domain.document.EmailTemplate;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 4/9/13
 * Time: 10:42 PM
 * Responsibility:
 */
public class EmailTemplates {
    private List<EmailTemplate> list;

    public List<EmailTemplate> getList() {
        return list;
    }

    public void setList(List<EmailTemplate> list) {
        this.list = list;
    }
}
