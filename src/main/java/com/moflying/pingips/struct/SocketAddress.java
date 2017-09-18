package com.moflying.pingips.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketAddress {
    private String ip;
    private int port;

    @Override
    public String toString() {
        return String.format("%s:%d", ip, port);
    }

    SocketAddress(String ip, String portString) {
        int port = 0;
        try {
            port = Integer.parseInt(portString);
        } catch (Exception e) {
            System.out.println(e);
        }
        this.ip = ip;
        this.port = port;
    }

    public static List<SocketAddress> toSocketAddresses(List<String> ipPlusPortInfo) {
        List<SocketAddress> socketAddresses = new ArrayList<>();
        if (CollectionUtils.isEmpty(ipPlusPortInfo)) {
            return socketAddresses;
        }

        for (String info : ipPlusPortInfo) {
            String[] result = info.split(":");
            if (result.length != 2) {
                socketAddresses.add(new SocketAddress(info, 0));
            } else {
                socketAddresses.add(new SocketAddress(result[0], result[1]));
            }
        }

        return socketAddresses;
    }
}
