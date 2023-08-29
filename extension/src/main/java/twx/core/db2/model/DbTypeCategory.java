package twx.core.db2.model;

// Internal Enum for FK-Rules
    // --------------------------------------------------------------------------------
    public enum DbTypeCategory {
        NULL(1, "null"),
        NUMERIC(1, "numeric"), 
        DATETIME(2, "datetime"), 
        TEXTUAL(3, "textual"), 
        BINARY(4, "binary"),
        SPECIAL(5, "special"),
        OTHER(6, "other");

        public Integer  key;
        public String   label;

        private DbTypeCategory(Integer key, String label) {
            this.key = key;
            this.label = label;
        }

        public static DbTypeCategory getByKey(Integer key) {
            for (DbTypeCategory e : values()) {
                if (e.key.equals(key)) {
                    return e;
                }
            }
            return null;
        }

        public static DbTypeCategory getByLabel(String label) {
            for (DbTypeCategory e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }
    }
