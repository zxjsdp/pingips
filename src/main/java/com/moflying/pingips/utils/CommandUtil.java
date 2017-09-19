package com.moflying.pingips.utils;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandUtil {
    private static Runtime runtime = Runtime.getRuntime();
    private static final InetAddressValidator inetAddressValidator = new InetAddressValidator();

    public static String ping(String ip, double timeoutSecond) {
        if (timeoutSecond <= 0) {
            timeoutSecond = 0.3;
        }

        if (!inetAddressValidator.isValid(ip)) {
            return " invalid . " + ip;
        }

        try {
            String command = String.format("ping -t 1 -i %.2f %s", timeoutSecond, ip);
            System.out.println(command);
            Process p = runtime.exec(command);
            int returnValue = p.waitFor();
            return String.format("%8s | %s", returnValue == 0 ? "ok" : "timeout", ip);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String tcping(String ip, int port, int timeoutSecond) {
        if (timeoutSecond <= 0) {
            timeoutSecond = 1;
        }

        if (!inetAddressValidator.isValid(ip)) {
            return " invalid . " + ip;
        }

        if (port <= 0) {
            return " invalid . " + ip;
        }

        try {
            String command = String.format("tcping -t %d %s %d", timeoutSecond, ip, port);
            System.out.println(command);
            int returnValue = runtime.exec(command).waitFor();
            return String.format("%8s | %s:%d", returnValue == 0 ? "ok" : "timeout", ip, port);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String runSystemCommand(String command) {
        try {
            Process p = runtime.exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            StringBuilder out = new StringBuilder();
            // reading output stream of the command
            while ((s = inputStream.readLine()) != null) {
                out.append(s);
            }
            return out.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
