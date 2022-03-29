package com.cc.jokit;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class Utils {

    // 判断是否是合法的IP地址 或者是可以成为合法的IP地址
    public static boolean isValidIP(String ip) {

        if(ip.equals("")) return true;

        int pointCount = 0;
        for (Character c : ip.toCharArray()) {
            if(c == '.') pointCount++;
        }
        if(pointCount > 3) return false;

        String[] ipSplit = ip.split("\\.");

        if(ipSplit.length != pointCount && ipSplit.length != pointCount + 1) return false;

        for(String s : ipSplit) {
            try {
                int temp = Integer.parseInt(s);
                if(temp < 0 || temp > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    // 判断是否是合法的端口
    public static boolean isValidPort(String port) {
        if(port.equals("")) return true;

        try {
            int temp = Integer.parseInt(port);
            if(temp < 0 || temp > 65535) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static int parsePort(String port) throws JokitException {

        if (port.equals("")) {
            throw new JokitException("Port格式错误:" + port);
        }
        int res;
        try {
            res = Integer.parseInt(port);
            if (res < 0 || res > 65535) throw new JokitException("Port格式错误:" + port);
        } catch (NumberFormatException e) {
            throw new JokitException("Port格式错误:" + port);
        }

        return res;
    }

    // 查询本机本地所有IP地址
    public static List<String> getLocalIP() {
        List<String> res = new LinkedList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ia = inetAddresses.nextElement();
                    if(ia instanceof Inet6Address) {
                        continue; // omit IPv6 address
                    }
                    res.add(ia.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return res;
    }

}
