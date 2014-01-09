/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.spring.social;

import com.mineness.service.UserService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Bjorn Harvold
 * Date: 7/29/11
 * Time: 4:45 PM
 * Responsibility:
 */
@Component("springSecuritySignInAdapter")
public class SpringSecuritySignInAdapter implements SignInAdapter {
    private static final Logger log = LoggerFactory.getLogger(SpringSecuritySignInAdapter.class);
    private final UserService userService;
    private final RequestCache requestCache;
    private final RememberMeServices rememberMeServices;

    @Autowired
    public SpringSecuritySignInAdapter(RequestCache requestCache,
                                       UserService userService,
                                       RememberMeServices rememberMeServices) {
        this.requestCache = requestCache;
        this.userService = userService;
        this.rememberMeServices = rememberMeServices;
    }

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {

        HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
        String remoteAddress = null;
        if (nativeReq != null) {
            remoteAddress = nativeReq.getRemoteAddr();
        }

        userService.autoLogin(new ObjectId(localUserId), remoteAddress);

        // add remember me
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest sRequest = request.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse sResponse = request.getNativeResponse(HttpServletResponse.class);
        rememberMeServices.loginSuccess(sRequest, sResponse, authentication);

        return extractOriginalUrl(request);
    }

    private String extractOriginalUrl(NativeWebRequest request) {
        HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse nativeRes = request.getNativeResponse(HttpServletResponse.class);

        // else look to see if there is another saved request defined somewhere else
        SavedRequest saved = requestCache.getRequest(nativeReq, nativeRes);
        if (saved == null) {
            return null;
        }
        requestCache.removeRequest(nativeReq, nativeRes);
        removeAutheticationAttributes(nativeReq.getSession(false));
        return saved.getRedirectUrl();
    }

    private void removeAutheticationAttributes(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
