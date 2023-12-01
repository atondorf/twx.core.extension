package twx.core.db.model.settings;

import twx.core.db.model.DbConstants;

public enum DbIndexSetting implements Setting {
	UNIQUE(0, DbConstants.MODEL_SETTING_UNIQUE),
	PK(1, DbConstants.MODEL_SETTING_PRIMARY_KEY);

	public Integer key;
	public String label;

	private DbIndexSetting(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	public static DbIndexSetting getByKey(Integer key) {
		for (DbIndexSetting e : values()) {
			if (e.key.equals(key)) {
				return e;
			}
		}
		return null;
	}

	public static DbIndexSetting getByLabel(String label) {
		for (DbIndexSetting e : values()) {
			if (e.label.equals(label)) {
				return e;
			}
		}
		return null;
	}

}
