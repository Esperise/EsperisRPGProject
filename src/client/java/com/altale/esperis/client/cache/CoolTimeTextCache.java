package com.altale.esperis.client.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoolTimeTextCache {
    private static final List<String> coolTimeTextList = new ArrayList<String>();

    public static void setCoolTimeTextList( String coolTimeText) {
        coolTimeTextList.clear();
        String[] temp= coolTimeText.split("\n");
        coolTimeTextList.addAll(Arrays.asList(temp));
    }
    public static List<String> getCoolTimeTextList() {
        return coolTimeTextList;
    }
}
