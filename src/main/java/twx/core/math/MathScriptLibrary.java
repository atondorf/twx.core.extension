package twx.core.math;

import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;

import org.apache.commons.math3.util.Precision;

public class MathScriptLibrary {

	@ThingworxServiceDefinition(name = "int_format")
	@ThingworxServiceResult(name = "result", baseType = "STRING", aspects = {})
	public static String int_format(
			@ThingworxServiceParameter(name = "fmt", baseType = "STRING" ) String fmt,
			@ThingworxServiceParameter(name = "value", baseType = "INTEGER" ) Integer value 
	) throws Exception {
		return String.format(fmt,value);
	}

	@ThingworxServiceDefinition(name = "number_format")
	@ThingworxServiceResult(name = "result", baseType = "STRING", aspects = {})
	public static String number_format(
			@ThingworxServiceParameter(name = "fmt", baseType = "STRING" ) String fmt,
			@ThingworxServiceParameter(name = "value", baseType = "NUMBER" ) Double value 
	) throws Exception {
		return String.format(fmt,value);
	}

	@ThingworxServiceDefinition(name = "number_round", description="Rounds the given value to the specified number of decimal places.")
	@ThingworxServiceResult(name = "result", baseType = "NUMBER", aspects = {})
	public static Double number_round(
			@ThingworxServiceParameter(name = "value", baseType = "NUMBER") Double value,
			@ThingworxServiceParameter(name = "decimals", baseType = "INTEGER") Integer decimals
	) throws Exception {
		return Precision.round(value,decimals);
	}

	@ThingworxServiceDefinition(name = "number_compareTo")
	@ThingworxServiceResult(name = "result", baseType = "INTEGER", aspects = {})
	public static Integer number_compareTo(
		@ThingworxServiceParameter(name = "value1", baseType = "NUMBER") Double value1,
		@ThingworxServiceParameter(name = "value1", baseType = "NUMBER") Double value2,
		@ThingworxServiceParameter(name = "eps", baseType = "NUMBER") Double eps
	) throws Exception {
		return Precision.compareTo(value1,value2,eps);
	}

	@ThingworxServiceDefinition(name = "number_equals", description = "Returns true if there is no double value strictly between the arguments or the difference between them is within the range of allowed error (inclusive).", category = "Math")
	@ThingworxServiceResult(name = "result", baseType = "BOOLEAN", aspects = {})
	public static Boolean number_equals(
		@ThingworxServiceParameter(name = "value1", baseType = "NUMBER") Double value1,
		@ThingworxServiceParameter(name = "value1", baseType = "NUMBER") Double value2,
		@ThingworxServiceParameter(name = "eps", baseType = "NUMBER") Double eps
	) throws Exception {
		return Precision.equals(value1,value2,eps);
	}
}
