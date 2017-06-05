package pl.wipek.validators;

import java.util.regex.Pattern;

/**
 * Created by Krzysztof Adamczyk on 14.04.2017.
 * Validator class which validate object to the required requirements
 */
public class Validator {
    /**
     * @author Krzysztof Adamczyk
     * @param str string which will be validate
     * @param args string contains what will be validate in form [rule]:[value]|[rule2]:[value_to_rule2]|... for example: maxLength:10|minLength:3
     * available pl.wipek.validators
     * maxLength - check String length is equal or shorter than given value
     * minLength - check String length is equal or longer than given value
     * exactLength - check String length is exact to value
     * isValidEmail - check String value is valid Email Address
     * @return boolean true if object meets all the validation requirements otherwise return false
     */
    public static boolean validate(String str, String args) {
        boolean result = false;
        String[] rules = args.split(Pattern.quote("|"));
        for (String rule : rules) {
            String[] tmp = rule.split(Pattern.quote(":"));
            if(tmp[0].equals("maxLength")) {
                result = Validator.maxLength(str, Integer.parseInt(tmp[1]));
            }
            else if(tmp[0].equals("minLength")) {
                result = Validator.minLength(str, Integer.parseInt(tmp[1]));
            }
            else if(tmp[0].equals("exactLength")) {
                result = Validator.exactLength(str, Integer.parseInt(tmp[1]));
            }
            else if(tmp[0].equals("isValidEmail")) {
                result = Validator.isValidEmail(str);
            }
            else if(tmp[0].equals("onlyLetters")) {
                result = str.chars().allMatch(Character::isLetter);
            }
            else if(tmp[0].equals("onlyNumbers")) {
                result = !str.contains("[a-zA-Z]+");
            }
        }
        return result;
    }

    /**
     * @author Krzysztof Adamczyk
     * @param str String which will be validate
     * @param value is max object [field] length value
     * @return boolean true if object [field] length is equal or shorter than value otherwise return false
     */
    public static boolean maxLength(String str, int value) {
        return str.length() <= value;
    }

    /**
     * @author Krzysztof Adamczyk
     * @param str String which will be validate
     * @param value is min string length value
     * @return boolean true if string length is equal or longer than value otherwise return false
     */
    public static boolean minLength(String str, int value) {
        return str.length() >= value;
    }

    /**
     * @author Krzysztof Adamczyk
     * @param str String which will be validate
     * @param value is length which String length should have
     * @return boolean true if String length is exact to value
     */
    public static boolean exactLength(String str, int value) {
        return str.length() == value;
    }

    /**
     * @author Krzysztof Adamczyk
     * @param str String will be check is valid Email Address
     * @return boolean true if String is valid Email Address otherwise return false
     */
    public static boolean isValidEmail(String str) {
        return EmailValidator.validate(str);
    }
}
