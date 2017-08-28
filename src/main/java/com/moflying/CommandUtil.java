package com.moflying;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandUtil {
    public static String ping(String ip) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 " + ip);
            int returnValue = p.waitFor();
            return String.valueOf(returnValue == 0);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String runSystemCommand(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s = "";
            String out = "";
            // reading output stream of the command
            while ((s = inputStream.readLine()) != null) {
                out += s;
            }
            return out;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
