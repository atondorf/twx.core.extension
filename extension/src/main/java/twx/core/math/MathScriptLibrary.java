package twx.core.math;

import org.mozilla.javascript.*;

import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import org.apache.commons.math3.util.Precision;

public class MathScriptLibrary {

	//// Require & Construction  ////

    public static void requrire_core_math(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        //  ScriptableObject.defineClass(me, MultiTimer.class);
    }

	//// Basic Math Helpers ////

    public static Object core_formatInteger(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
		if (args.length != 2)
			throw new Exception("Invalid Number of Arguments in core_formatInteger"); 
        DSLConverter.convertValues(args, me);
        StringPrimitive  fmt = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
		IntegerPrimitive val = (IntegerPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.INTEGER);
		return String.format( fmt.getValue(),val.getValue() );
    }
	
    public static Object core_formatLong(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
		if (args.length != 2)
			throw new Exception("Invalid Number of Arguments in core_formatLong"); 
        DSLConverter.convertValues(args, me);
        StringPrimitive	fmt = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
		LongPrimitive	val = (LongPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.LONG);
		return String.format( fmt.getValue(),val.getValue() );
    }

	public static Object core_formatNumber(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
		if (args.length != 2)
			throw new Exception("Invalid Number of Arguments in core_formatNumber"); 
        DSLConverter.convertValues(args, me);
        StringPrimitive fmt = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
		NumberPrimitive val = (NumberPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.NUMBER);
		return String.format(fmt.getValue(),val.getValue());
    }

	public static Object core_roundNumber(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
		if (args.length != 2)
			throw new Exception("Invalid Number of Arguments in core_roundNumber"); 
        DSLConverter.convertValues(args, me);
		NumberPrimitive 	val = (NumberPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.NUMBER);
		IntegerPrimitive 	dec = (IntegerPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.INTEGER);
		return Precision.round(val.getValue(),dec.getValue());
    }

	public static Object core_compareNumbers(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
		if (args.length != 3)
			throw new Exception("Invalid Number of Arguments in core_compareNumbers"); 
        DSLConverter.convertValues(args, me);
		NumberPrimitive 	val1 = (NumberPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.NUMBER);
		NumberPrimitive 	val2 = (NumberPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.NUMBER);
		IntegerPrimitive 	eps  = (IntegerPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.INTEGER);
		return Precision.compareTo(val1.getValue(),val2.getValue(),eps.getValue());
    }

	public static Object core_equalNumbers(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
		if (args.length != 3)
			throw new Exception("Invalid Number of Arguments in core_compareNumbers"); 
        DSLConverter.convertValues(args, me);
		NumberPrimitive 	val1 = (NumberPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.NUMBER);
		NumberPrimitive 	val2 = (NumberPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.NUMBER);
		IntegerPrimitive 	eps  = (IntegerPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.INTEGER);
		return Precision.equals(val1.getValue(),val2.getValue(),eps.getValue());
    }

	//// Old Deprecated Versions ///

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
