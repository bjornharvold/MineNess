/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.dto;

import com.mineness.domain.document.Role;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 7/28/11
 * Time: 12:37 PM
 * Responsibility:
 */
public class Roles {
    private List<Role> list;

    public Roles() {
    }

    public Roles(List<Role> list) {
        this.list = list;
    }

    public List<Role> getList() {
        return list;
    }

    public void setList(List<Role> list) {
        this.list = list;
    }
}
