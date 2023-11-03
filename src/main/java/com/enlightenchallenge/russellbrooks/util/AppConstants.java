package com.enlightenchallenge.russellbrooks.util;

import java.util.Map;
import java.util.HashMap;

public class AppConstants {

    public static enum Components {
        TSTAT(0),
        BATT(1);
        private int value;

        private static Map map = new HashMap<>();

        private Components(int value) {
            this.value = value;
        }

        static {
            for (Components pageType : Components.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static Components valueOf(int pageType) {
            return (Components) map.get(pageType);
        }

        public int getValue() {
            return value;
        }

    }
}
