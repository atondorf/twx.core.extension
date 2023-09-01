package twx.core.db.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.json.JSONObject;

public class DbEnumValue extends DbObject<DbEnum> {

    public DbEnumValue(DbEnum parent, String name) {
        super(parent, name);
    }
    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    @Override
    public void clear() {
        super.clear();
    }

    public DbEnum getEnum() {
        return (DbEnum) this.getParent();
    }

    // region Compare and Hash ...
    // --------------------------------------------------------------------------------
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final DbEnumValue enumValue = (DbEnumValue) o;
		return Objects.equals(this.getName(), enumValue.getName() );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }

    @Override
    public String toString() {
        return DbNameUtil.of(this.getEnum(), this.getName() );
    }

    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    @Override
    public DbEnumValue fromJSON(JSONObject json) {
        super.fromJSON(json);
        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        return json;
    }
    // endregion
}
