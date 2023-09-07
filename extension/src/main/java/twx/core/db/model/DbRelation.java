package twx.core.db.model;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import twx.core.db.model.settings.DbRelationSetting;
import twx.core.db.model.settings.DbRelationType;
import twx.core.db.model.settings.SettingHolder;

public class DbRelation extends DbObject<DbModel> implements SettingHolder<DbRelationSetting> {
    private final Map<DbRelationSetting, String> settings = new EnumMap<>(DbRelationSetting.class);
    protected final List<DbRelationColumn> relationColumns = new LinkedList<>();
    private final DbRelationType relationType = null;

    public DbRelation(String name) {
        super(null, name);
    }

    protected DbRelation(DbModel parent, String name) {
        super(parent, name);
    }
    
    @Override
    public void clear() {
        super.clear();
        this.settings.clear();
        this.relationColumns.stream().forEach(c -> c.clear());
        this.relationColumns.clear();
    }

    public DbSchema getSchema() {
        return (DbSchema)this.getParent();
    }

    // region Get/Set Settings ... 
    // --------------------------------------------------------------------------------
    @Override
    public void addSetting(DbRelationSetting setting, String value) {
        settings.put(setting, value);
    }

    public Map<DbRelationSetting, String> getSettings() {
        return Collections.unmodifiableMap(settings);
    }
    // endregion 
    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public Boolean hasRelationFromColumn(final String fromColumnName ) {
        return DbObject.hasObject(this.relationColumns, fromColumnName);
    }

    public DbRelationColumn getRelationFromColumn(final String fromColumnName ) {
        return DbObject.findObject(this.relationColumns, fromColumnName);
    }

    public Boolean hasRelationToColumn(final String toColumnName ) {
        return this.relationColumns.stream().anyMatch(c -> c.getToColumn().getName().equals(toColumnName));
    }

    public DbRelationColumn getRelationToColumn(final String toColumnName ) {
         return this.relationColumns.stream().filter(c -> c.getToColumn().getName().equals(toColumnName)).findAny().orElse(null);
    }

    public DbRelationType getRelationType() {
        return this.relationType;
    }

    public DbRelationType setRelationType() {
        return this.relationType;
    }

    public List<DbRelationColumn> getRelationColumns() {
        return Collections.unmodifiableList(relationColumns);
    }

    // region Compare and Hash ...
    // --------------------------------------------------------------------------------
/*
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DbRelation that = (DbRelation) o;
        return this.getFrom().equals(that.getFrom()) && this.getTo().equals(that.getTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFrom(), this.getTo(), this.getName());
    }

    @Override
    public String toString() {
        return this.getFrom() + " " + this.relationType + " " + this.getTo();
    }
*/    
    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    public JSONObject toJSON() {
        var json = new JSONObject();
        return json;
    }

    public DbObject<?> fromJSON(JSONObject json) {
        return this;
    }
    // endregion

}
