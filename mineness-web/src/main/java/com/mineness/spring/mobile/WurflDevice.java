/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.spring.mobile;

import net.sourceforge.wurfl.core.MarkUp;
import net.sourceforge.wurfl.core.matchers.MatchType;
import org.springframework.mobile.device.Device;

import java.util.Map;

/**
 * WURFL-based {@link org.springframework.mobile.device.Device} implementation.
 * Also implements net.sourceforge.wurfl.core.Device, exposing the full capabilities of the WURFL API.
 * @author Keith Donald
 * @author Roy Clarkson
 */
public class WurflDevice implements Device, net.sourceforge.wurfl.core.Device {

	private final net.sourceforge.wurfl.core.Device device;
	
	public WurflDevice(net.sourceforge.wurfl.core.Device device) {
		this.device = device;
	}

	// implementing our Device interface

    @Override
    public boolean isNormal() {
        return !isMobile() && !isTablet();
    }

    @Override
    public boolean isMobile() {
		String capability = getCapability("is_wireless_device");
		return capability != null && capability.length() > 0 && Boolean.valueOf(capability);
	}

    @Override
    public boolean isTablet() {
        String capability = getCapability("is_tablet");
        return capability != null && capability.length() > 0 && Boolean.valueOf(capability);
    }

    // implementing Wurfl Device
    @Override
	public String getId() {
		return device.getId();
	}

    @Override
	public String getUserAgent() {
		return device.getUserAgent();
	}

    /**
     * http://wurfl.sourceforge.net/help_doc.php for details
     * @param name name
     * @return String
     */
    @Override
	public String getCapability(String name) {
		return device.getCapability(name);
	}

	@SuppressWarnings("rawtypes")
	public Map getCapabilities() {
		return device.getCapabilities();
	}

    @Override
	public MarkUp getMarkUp() {
		return device.getMarkUp();
	}

    public Integer getWidth() {
        return Integer.valueOf(getCapability("resolution_width"));
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[WurflDevice ");
		builder.append("mobile").append("=").append(isMobile()).append(", ");
		builder.append("tablet").append("=").append(isTablet()).append(", ");
		builder.append("normal").append("=").append(isNormal()).append(", ");
		builder.append("generic").append("=").append(isGeneric()).append(", ");
		builder.append("id").append("=").append(getId()).append(", ");
		builder.append("userAgent").append("=").append(getUserAgent());
		builder.append("]");
		return builder.toString();
	}

    @Override
	public String getDeviceRootId() {
		return device.getDeviceRootId();
	}

    @Override
    public boolean isGeneric() {
        return device.isGeneric();
    }

    @Override
    public MatchType getMatchType() {
        return device.getMatchType();
    }

    @Override
    public boolean isActualDeviceRoot() {
		return device.isActualDeviceRoot();
	}

}