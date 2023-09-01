package twx.core.db.model.settings;

import java.util.Arrays;

public enum DbRelationType {
	ONE_TO_MANY("<"),
	MANY_TO_ONE(">"),
	ONE_TO_ONE("-"),
	MANY_TO_MANY("<>");
	private final String symbol;
	
	DbRelationType(final String symbol) {
		this.symbol = symbol;
	}
	
	public static DbRelationType of(final String symbol) {
		return Arrays.stream(values()).filter(r -> r.symbol.equals(symbol)).findAny().orElse(null);
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return symbol;
	}
}