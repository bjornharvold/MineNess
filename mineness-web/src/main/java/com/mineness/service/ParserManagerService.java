package com.mineness.service;

import com.mineness.service.impl.EmailParserException;

import java.io.InputStream;

/**
 * Created by Bjorn Harvold
 * Date: 5/8/13
 * Time: 3:41 PM
 * Responsibility:
 */
public interface ParserManagerService {
    void processIncomingEmail(String username, InputStream is) throws EmailParserException;
}
