package twx.core.db.model.settings;

import twx.core.db.model.DbConstants;

public enum DbColumnSetting implements Setting {
	PRIMARY_KEY(0, DbConstants.MODEL_SETTING_PRIMARY_KEY),
	NOT_NULL(1,DbConstants.MODEL_SETTING_NOT_NULL),
	UNIQUE(2,DbConstants.MODEL_SETTING_UNIQUE),
	INCREMENT(3,DbConstants.MODEL_SETTING_INCREMENT),
	DEFAULT(4, DbConstants.MODEL_SETTING_DEFAULT),
	THINGWORXTYPE(99, DbConstants.MODEL_SETTING_TWXTYPE);

	public Integer key;
	public String label;

	private DbColumnSetting(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	public static DbColumnSetting getByKey(Integer key) {
		for (DbColumnSetting e : values()) {
			if (e.key.equals(key)) {
				return e;
			}
		}
		return null;
	}

	public static DbColumnSetting getByLabel(String label) {
		for (DbColumnSetting e : values()) {
			if (e.label.equals(label)) {
				return e;
			}
		}
		return null;
	}
}
