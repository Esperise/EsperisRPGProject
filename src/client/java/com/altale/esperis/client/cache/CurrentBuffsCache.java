package com.altale.esperis.client.cache;

import java.util.HashMap;
import java.util.Map;

public class CurrentBuffsCache {
    public static final Map<String, Map<Integer, Integer>> buffsMap = new HashMap<>();
    public static final Map<String, Double> healBuffsMap = new HashMap<>();
    public static void setBuffsMap(Map<String, Map<Integer, Integer>> buffMap) {
        buffsMap.clear();
        buffsMap.putAll(buffMap);

    }
    public static void setHealBuffsMap(Map<String, Double> healBuffMap) {
        healBuffsMap.clear();
        healBuffsMap.putAll(healBuffMap);

    }
    public static Map<String, Map<Integer, Integer>> getBuffsMap() {
        return buffsMap;
    }
    public static Map<String, Double> getHealBuffsMap() {
        return healBuffsMap;
    }
}
