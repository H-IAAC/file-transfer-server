package br.org.eldorado.hiaac;

import java.util.HashMap;
import java.util.Map;

public class Actions {
    public static final int SEND_MESSAGE = 0;
    public static final int SEND_FILE = 1;

    private static final Map<Integer, String> actionsDescriptions = new HashMap();
    static {
        actionsDescriptions.put(SEND_MESSAGE, "Sending message");
        actionsDescriptions.put(SEND_FILE, "Sending file");
    }

    public static String getDescription(int action) {
        return actionsDescriptions.get(action);
    }
}
