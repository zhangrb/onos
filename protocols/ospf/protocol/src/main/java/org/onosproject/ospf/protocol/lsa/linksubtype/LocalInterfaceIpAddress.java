/*
 * Copyright 2016-present Open Networking Foundation
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
package org.onosproject.ospf.protocol.lsa.linksubtype;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Bytes;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.ospf.exceptions.OspfErrorType;
import org.onosproject.ospf.exceptions.OspfParseException;
import org.onosproject.ospf.protocol.lsa.TlvHeader;
import org.onosproject.ospf.protocol.util.OspfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of local interface ip address TE value.
 */
public class LocalInterfaceIpAddress extends TlvHeader implements LinkSubType {
    private static final Logger log =
            LoggerFactory.getLogger(RemoteInterfaceIpAddress.class);
    private List<String> localInterfaceIPAddress = new ArrayList<>();

    /**
     * Creates an instance of local interface ip address.
     *
     * @param header tlv header instance
     */
    public LocalInterfaceIpAddress(TlvHeader header) {
        this.setTlvType(header.tlvType());
        this.setTlvLength(header.tlvLength());
    }

    /**
     * Adds local interface ip address.
     *
     * @param localAddress ip address
     */
    public void addLocalInterfaceIPAddress(String localAddress) {
        localInterfaceIPAddress.add(localAddress);
    }

    /**
     * Gets local interface ip address.
     *
     * @return localAddress ip address
     */
    public List<String> getLocalInterfaceIPAddress() {
        return localInterfaceIPAddress;
    }

    /**
     * Reads bytes from channel buffer.
     *
     * @param channelBuffer channel buffer instance
     * @throws OspfParseException might throws exception while parsing buffer
     */
    public void readFrom(ChannelBuffer channelBuffer) throws OspfParseException {
        while (channelBuffer.readableBytes() >= OspfUtil.FOUR_BYTES) {
            try {
                byte[] tempByteArray = new byte[OspfUtil.FOUR_BYTES];
                channelBuffer.readBytes(tempByteArray, 0, OspfUtil.FOUR_BYTES);
                this.addLocalInterfaceIPAddress(InetAddress.getByAddress(tempByteArray).getHostName());
            } catch (UnknownHostException e) {
                log.debug("Error::readFrom:: {}", e.getMessage());
                throw new OspfParseException(OspfErrorType.OSPF_MESSAGE_ERROR,
                                             OspfErrorType.BAD_MESSAGE);
            }
        }
    }

    /**
     * Gets local interface ip address as byte array.
     *
     * @return local interface ip address as byte array
     * @throws OspfParseException might throws exception while parsing packet
     */
    public byte[] asBytes() throws OspfParseException {
        byte[] linkSubType = null;

        byte[] linkSubTlvHeader = getTlvHeaderAsByteArray();
        byte[] linkSubTlvBody = getLinkSubTypeTlvBodyAsByteArray();
        linkSubType = Bytes.concat(linkSubTlvHeader, linkSubTlvBody);

        return linkSubType;
    }

    /**
     * Gets byte array of local interface ip address.
     *
     * @return byte array of local interface ip address
     * @throws OspfParseException might throws exception while parsing packet
     */
    public byte[] getLinkSubTypeTlvBodyAsByteArray() throws OspfParseException {

        List<Byte> linkSubTypeBody = new ArrayList<>();

        for (String remoteAddress : this.localInterfaceIPAddress) {
            try {
                linkSubTypeBody.addAll(Bytes.asList(InetAddress.getByName(remoteAddress).getAddress()));
            } catch (Exception e) {
                log.debug("Error::getLinkSubTypeTlvBodyAsByteArray:: {}", e.getMessage());
                throw new OspfParseException(OspfErrorType.OSPF_MESSAGE_ERROR,
                                             OspfErrorType.BAD_MESSAGE);
            }
        }

        return Bytes.toArray(linkSubTypeBody);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .omitNullValues()
                .add("localInterfaceIPAddress", localInterfaceIPAddress)
                .toString();
    }
}