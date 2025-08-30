package com.altale.esperis.client.cache;

import com.altale.esperis.client.packet.CurrentBuffS2CPacketReceiver;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UseScopeCache {
    private static final Logger LOG = LoggerFactory.getLogger(UseScopeCache.class);
    public static final Map<Boolean, Integer> scopeCacheMap = new HashMap<>();
    public static void setScopeInfo(boolean useScope, int scopeTime){
        scopeCacheMap.put(useScope, scopeTime);
        Log.info(LogCategory.LOG,"useScope: %b , %d scope time", useScope, scopeTime );
    }
    public static void clear(){
        scopeCacheMap.clear();
    }
    public static boolean isUseScope(){
        return scopeCacheMap.containsKey(Boolean.TRUE);
    }
    public static int getScopeTime(){
        if(scopeCacheMap.containsKey(Boolean.TRUE)){
            return scopeCacheMap.get(Boolean.TRUE);
        }
        return 0;
    }

}
