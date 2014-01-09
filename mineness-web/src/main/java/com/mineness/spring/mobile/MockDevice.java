/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.spring.mobile;

import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.MarkUp;
import net.sourceforge.wurfl.core.exc.CapabilityNotDefinedException;
import net.sourceforge.wurfl.core.matchers.MatchType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 5/25/12
 * Time: 6:20 PM
 * Responsibility: For testing of mobile devices
 */
public class MockDevice implements Device {
    public enum DEVICE_TYPE {TABLET, NORMAL, MOBILE};
    private final DEVICE_TYPE type;
    private Map<String, String> capabilities = new HashMap<String, String>();

    public MockDevice(DEVICE_TYPE type) {
        this.type = type;

        switch (type) {
            case TABLET:
                capabilities.put("is_tablet", "true");
                break;
            case NORMAL:
                capabilities.put("is_tablet", "false");
                capabilities.put("is_wireless_device", "false");
                break;
            case MOBILE:
                capabilities.put("resolution_width", "480");
                capabilities.put("is_wireless_device", "true");
                break;
        }
    }

    @Override
    public String getId() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    @Override
    public String getUserAgent() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    @Override
    public String getCapability(String s) throws CapabilityNotDefinedException {
        return capabilities.get(s);
    }

    @Override
    public Map<String, String> getCapabilities() {
        return null;
    }

    @Override
    public MarkUp getMarkUp() {
        return null;
    }

    @Override
    public boolean isActualDeviceRoot() {
        return false;
    }

    @Override
    public String getDeviceRootId() {
        return getId();
    }

    @Override
    public boolean isGeneric() {
        return this.type.equals(DEVICE_TYPE.NORMAL);
    }

    @Override
    public MatchType getMatchType() {
        return null;
    }
}
