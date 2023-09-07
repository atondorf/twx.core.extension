package twx.core.db.model.settings;

import twx.core.db.model.DbConstants;
import twx.core.db.model.DbForeignKey.FkRule;

public enum DbTableSetting implements Setting {
	HEADERCOLOR(0, DbConstants.MODEL_SETTING_HEADERCOLOR),
	THINGWORXTYPE(99, DbConstants.MODEL_SETTING_TWXTYPE);

	public Integer key;
	public String label;

	private DbTableSetting(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	public static DbTableSetting getByKey(Integer key) {
		for (DbTableSetting e : values()) {
			if (e.key.equals(key)) {
				return e;
			}
		}
		return null;
	}

	public static DbTableSetting getByLabel(String label) {
		for (DbTableSetting e : values()) {
			if (e.label.equals(label)) {
				return e;
			}
		}
		return null;
	}
}
