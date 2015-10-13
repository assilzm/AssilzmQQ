package utils
/**
 *
 * @author: Assilzm
 * create: 17:28.
 * description:
 */
class RegexUtils {


    static String getRegexValue(String str, String pattern, int groupIndex = 1) {
        def m = str =~ pattern
        if (m.find())
            return m.group(groupIndex)
        else
            throw new Exception("can not find group ${groupIndex} with pattern ${pattern.toString()}")

    }
}
