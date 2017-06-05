package pl.wipek.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Krzysztof Adamczyk on 14.04.2017.
 * @author http://stackoverflow.com/users/51230/maksim edited by Krzysztof Adamczyk
 * Validate correctness of email address
 */
public class EmailValidator {
    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate hex with regular expression
     *
     * @param hex
     *            hex for validation
     * @return true valid hex, false invalid hex
     */
    public static boolean validate(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}
