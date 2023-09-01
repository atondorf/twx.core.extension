package twx.core.db.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DbProject {
    private final String name;
	private final Map<String, String> properties = new LinkedHashMap<>();
	
	public DbProject(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return name;
	}
}
