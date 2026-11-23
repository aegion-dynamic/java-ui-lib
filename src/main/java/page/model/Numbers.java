package page.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Shared parsing for the number boxes. A blank box is not a number. */
final class Numbers {

    private Numbers() {
    }

    static boolean blank(String text) {
        return text == null || text.trim().length() == 0;
    }

    static String format(double value) {
        DecimalFormat format = new DecimalFormat("0.##");
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        return format.format(value);
    }
}
