package com.android.designpattern.structural.proxy.binder.server;

import java.util.HashMap;
import java.util.Map;

public class Database {

    private final Map<String, String> dp = new HashMap<>();

    private Database() {

    }

    private final static class DatabaseHolder {
        private static final Database instance = new Database();
    }

    public static Database getInstance() {
        return DatabaseHolder.instance;
    }

    public boolean query(String id, String pwd) {
        if (id == null || pwd == null) return false;
        if (!dp.containsKey(id)) return false;
        return pwd.equals(dp.get(id));
    }

    public void put(String key, String val) {
        dp.put(key, val);
    }

    public int size() {
        return dp.size();
    }

    public void clear() {
        dp.clear();
    }

}
