package com.secreto.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aashish Tomar on 8/31/2017.
 */

public class QueryBuilder {
    public static String buildQuery(String url, HashMap<Object, Object> hashMap) {
        url += "?";
        if (hashMap != null && !hashMap.isEmpty()) {
            for (Map.Entry<Object, Object> entry : hashMap.entrySet()) {
                url += entry.getKey() + "=" + entry.getValue()+"&";
            }
        }
        return url;
    }
}
