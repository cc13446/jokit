package com.cc.jokit;

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
}
