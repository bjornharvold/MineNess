/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.spring.mobile;

import net.sourceforge.wurfl.core.DefaultDeviceProvider;
import net.sourceforge.wurfl.core.DefaultWURFLManager;
import net.sourceforge.wurfl.core.DefaultWURFLService;
import net.sourceforge.wurfl.core.WURFLManager;
import net.sourceforge.wurfl.core.matchers.MatcherManager;
import net.sourceforge.wurfl.core.resource.DefaultWURFLModel;
import net.sourceforge.wurfl.core.resource.WURFLResource;
import net.sourceforge.wurfl.core.resource.WURFLResources;
import net.sourceforge.wurfl.core.resource.XMLResource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Factory that constructs the central {@link net.sourceforge.wurfl.core.WURFLManager} and exports it as a Spring bean that can be injected into other beans.
 * The specifics of this factory class are hidden when working with the wurfl-device-resolver tag in the wurfl-spring XML namespace.
 *
 * @author Keith Donald
 * @author Roy Clarkson
 */
public class WurflManagerFactoryBean implements FactoryBean<WURFLManager>, InitializingBean {

    private final Resource rootLocation;

    private List<? extends Resource> patchLocations;

    private WURFLManager manager;

    /**
     * Constructs a WurflManagerFactoryBean that loads the root Device model from the XML file at the specified resource path.
     * The specified resource must be resolvable to a file on the filesystem.
     *
     * @param rootLocation the path to the root device model XML file
     */
    public WurflManagerFactoryBean(Resource rootLocation) {
        Assert.notNull(rootLocation, "The rootResource property cannot be null");
        this.rootLocation = rootLocation;
    }

    /**
     * Set additional resource paths for patches that should be applied atop the root model.
     * If not set, no patches will be applied.
     * The specified resources must be resolvable to files on the filesystem.
     *
     * @param patchLocations the XML-based patch resources to apply
     */
    public void setPatchLocations(List<? extends Resource> patchLocations) {
        this.patchLocations = patchLocations;
    }

    // implementing InitializingBean

    public void afterPropertiesSet() throws Exception {
        this.manager = createWURFLManager();
    }

    // implementing FactoryBean

    public Class<?> getObjectType() {
        return WURFLManager.class;
    }

    public WURFLManager getObject() throws Exception {
        return manager;
    }

    public boolean isSingleton() {
        return true;
    }

    // internal helpers

    private WURFLManager createWURFLManager() {
        DefaultWURFLModel model = new DefaultWURFLModel(getRoot(), getPatches());
        MatcherManager matcherManager = new MatcherManager(model);
        DefaultDeviceProvider deviceProvider = new DefaultDeviceProvider(model);
        DefaultWURFLService service = new DefaultWURFLService(matcherManager, deviceProvider);
        return new DefaultWURFLManager(service);
    }

    private WURFLResource getRoot() {
        WURFLResource result = null;

        try {
            result = new XMLResource(rootLocation.getInputStream(), rootLocation.getFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private WURFLResources getPatches() {
        if (patchLocations == null) {
            return null;
        }
        WURFLResources patches = new WURFLResources();
        for (Resource patch : patchLocations) {
            try {
                patches.add(new XMLResource(patch.getInputStream(), patch.getFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return patches;
    }

}