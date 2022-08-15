package br.org.eldorado.hiaac.utils;

public class Logger {
    public static void print(String tag, String msg) {
        System.out.println(tag + ": " + msg);
    }

    public static void print(String tag, String msg, int connectionNumber) {
        System.out.format(tag + ": " +"%04d - "+ msg+"\n", connectionNumber);
    }
}
