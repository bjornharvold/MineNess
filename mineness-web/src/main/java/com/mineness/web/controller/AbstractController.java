/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.web.controller;

import com.mineness.spring.mobile.WurflDevice;
import com.mineness.utils.web.WebConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Bjorn Harvold
 * Date: 5/10/12
 * Time: 3:40 PM
 * Responsibility:
 */
public abstract class AbstractController {

    /**
     * Determine whether the resolution of the device falls within our mobile categorization
     *
     * @param device device
     * @param view   original view
     * @return View
     */
    /*
    protected String returnMobileViewIfNecessary(Model model, Device device, HttpSession session, String view) {

        // check if environment supports mobile versions
        if (mobileEnabledInEnvironment) {
            // if user has explicitly requested to see the full version of the site
            if (!userDesiresFullSiteResolutionOnMobileDevice(session)) {
                // the user has not requested to see the full version of the site
                // so now we check if the device is mobile
                if (device.isMobile() || device.isTablet()) {
                    // set the device on the request in case front-end wants to query on something specific
                    model.addAttribute(WebConstants.MOBILE_DEVICE, device);

                    // check resolution of device
                    WurflDevice wurflDevice = (WurflDevice) device;

                    if (wurflDevice.getWidth() <= mobileResolutionRequirement) {
                        view += ".mobile";
                    }
                }
            }
        }

        return view;
    }
    */

    /*
    protected void setUserDesiresFullSiteResolutionOnMobileDevice(HttpSession session, Boolean full) {
        session.setAttribute(WebConstants.FULL_EXPERIENCE_ON_MOBILE, full);
    }
    */

    /*
    private Boolean userDesiresFullSiteResolutionOnMobileDevice(HttpSession session) {
        Boolean result = Boolean.FALSE;
        Object obj = session.getAttribute(WebConstants.FULL_EXPERIENCE_ON_MOBILE);

        if (obj != null && obj instanceof Boolean) {
            result = (Boolean) obj;
        }

        return result;
    }
    */

    protected String createBaseUrl(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder(request.getScheme());
        sb.append("://").append(request.getServerName());

        if (request.getServerPort() != 80) {
            sb.append(":").append(request.getServerPort());
        }

        if (StringUtils.isNotBlank(request.getContextPath())) {
            sb.append(request.getContextPath());
        }
        return sb.toString();
    }
}
