package com.github.rickmvi.jtoolbox.utils.constants;

public class Constants {

    // --- Regex ---
    public static final String GENERIC            = "\\{}";
    public static final String PLACEHOLDERS_REGEX = "%(dc|dp|i|p|sc|S|lc)\\{(\\d+)}";

    // --- Number Formats ---
    public static final String DECIMAL_COMMA  = "#,##0.00";
    public static final String DECIMAL_POINT  = "###0.00";
    public static final String INTEGER        = "#,##0";
    public static final String PERCENT        = "0.00'%'";
    public static final String SCIENTIFIC     = "0.##E0";

    // --- Colors ---
    public static final String BLACK   = "\u001B[30m";
    public static final String BLUE    = "\u001B[34m";
    public static final String BOLD    = "\u001B[1m";
    public static final String CYAN    = "\u001B[36m";
    public static final String GREEN   = "\u001B[32m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String RED     = "\u001B[31m";
    public static final String RESET   = "\u001B[0m";
    public static final String WHITE   = "\u001B[37m";
    public static final String YELLOW  = "\u001B[33m";

    // --- Date/Time Formats ---
    public static final String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TIME_STAMP_FORMAT = "yyyyMMddHHmmssSSS";

    // --- Generic Messages ---
    public static final String NON_NEGATIVE   = "Value must be non-negative.";
    public static final String POSITIVE_VALUE = "Value must be positive.";

    // --- Error Messages (Index) ---
    public static final String INDEX_NEGATIVE         = "Index {} cannot be negative.";
    public static final String INDEX_OUT_OF_BOUNDS    = "Index {} is out of bounds for length {}.";
    public static final String INDEX_LESS_THAN        = "Index {} cannot be less than {}.";
    public static final String INDEX_GREATER_THAN     = "Index {} cannot be greater than {}.";
    public static final String INDEX_EQUALS           = "Index {} cannot be equal to {}.";
    public static final String INDEX_NOT_FOUND        = "Index {} not found in array.";
    public static final String INDEX_NOT_UNIQUE       = "Index {} must be unique in array.";
    public static final String INDEX_DUPLICATES       = "Index {} is not unique in array. Found {} duplicates.";

    // --- Error Messages (Range) ---
    public static final String START_NEGATIVE         = "Start index {} cannot be negative.";
    public static final String END_NEGATIVE           = "End index {} cannot be negative.";
    public static final String START_GREATER_THAN_END = "Start index {} cannot be greater than end index {}.";
    public static final String END_GREATER_THAN_LENGTH= "End index {} cannot exceed array length {}.";
    public static final String END_LESS_THAN_START    = "End index {} cannot be less than start index {}.";

    // --- Error Messages (Array / Value) ---
    public static final String NULL_VALUE             = "Value cannot be null.";
    public static final String EMPTY_ARRAY            = "Array cannot be empty.";
    public static final String STEP_OUT_OF_BOUNDS     = "Step {} is out of bounds.";

    public static final String INVALID_OPERATION         =  "Invalid {} operation (NaN encountered)";
    public static final String SUM_OVERFLOW              = "Sum overflow";
    public static final String SUBTRACT_OVERFLOW         = "Subtract overflow";
    public static final String PRODUCT_OVERFLOW          = "Product overflow";
    public static final String DIVISION_BY_ZERO          = "Division by zero";
    public static final String DIVIDEND_NEGATIVE         = "Negative dividend";
    public static final String DIVISOR_BE_POSITIVE       = "Divisor must be positive";
    public static final String DIVISION_UNDERFLOW        = "Division underflow";
    public static final String MODULO_BY_ZERO            = "Modulo by zero";
    public static final String NEGATIVE_EXPONENT         = "Negative exponent";
    public static final String NEGATIVE_VALUE            = "Negative value";
    public static final String EXPONENT_OVERFLOW         = "Exponentiation overflow";
    public static final String INVALID_EXPONENTIATION    = "Invalid exponentiation result";
    public static final String AVERAGE_EMPTY_ARRAY       = "Cannot average an empty array";
    public static final String OVERFLOW_DIVISIBLE_NUMBER = "Overflow while searching nearest divisible number";

    public static final String NEXTSAFE_FAILED           = "nextSafe() failed. Returning empty string. Cause: {}";
    public static final String SCANNER_NOT_INITIALIZED   = "Mistake: Scanner not initialized. Call Scan.init() first.";

}
