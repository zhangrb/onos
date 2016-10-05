/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.net.config.basics;

import org.onosproject.net.DeviceId;
import org.onosproject.net.config.Config;
import org.onosproject.net.region.Region;
import org.onosproject.net.region.RegionId;

import java.util.List;

/**
 * Basic configuration for network regions.
 */
public final class BasicRegionConfig extends Config<RegionId> {

    private static final String TYPE = "type";
    private static final String DEVICES = "devices";

    @Override
    public boolean isValid() {
        return hasOnlyFields(TYPE, DEVICES);
    }

    /**
     * Returns the region type.
     *
     * @return the region type
     */
    public Region.Type getType() {
        String t = get(TYPE, null);
        return t == null ? null : regionTypeFor(t);
    }

    private Region.Type regionTypeFor(String t) {
        try {
            return Region.Type.valueOf(t.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    /**
     * Returns the identities of the devices in this region.
     *
     * @return list of device identifiers
     */
    public List<DeviceId> getDevices() {
        return getList(DEVICES, DeviceId::deviceId);
    }

    // TODO: implement setters
}
