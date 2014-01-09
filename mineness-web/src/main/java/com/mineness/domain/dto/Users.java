/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.dto;

import com.mineness.domain.document.User;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 7/28/11
 * Time: 12:37 PM
 * Responsibility:
 */
public class Users {
    private List<User> list;

    public Users() {
    }

    public Users(List<User> list) {
        this.list = list;
    }

    public List<User> getList() {
        return list;
    }

    public void setList(List<User> list) {
        this.list = list;
    }
}
