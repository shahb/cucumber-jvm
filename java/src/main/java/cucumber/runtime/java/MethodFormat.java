package cucumber.runtime.java;

import cucumber.runtime.CucumberException;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for formatting a method signature to a shorter form.
 */
public class MethodFormat {
    private static final Pattern METHOD_PATTERN = Pattern.compile("((?:static\\s|public\\s)+)([^\\s]*)\\s\\.?(.*)\\.([^\\(]*)\\(([^\\)]*)\\)(?: throws )?(.*)");
    private static final String PACKAGE_PATTERN = "[^,]*\\.";
    private final MessageFormat format;

    /**
     * Creates an instance with default formatting.
     */
    public MethodFormat() {
        this("%c.%m(%a)");
    }

    /**
     * @param format the format string to use. There are several pattern tokens that can be used:
     *               <ul>
     *               <li><strong>%M</strong>: Modifiers</li>
     *               <li><strong>%qr</strong>: Qualified return type</li>
     *               <li><strong>%r</strong>: Unqualified return type</li>
     *               <li><strong>%qc</strong>: Qualified class</li>
     *               <li><strong>%c</strong>: Unqualified class</li>
     *               <li><strong>%m</strong>: Method name</li>
     *               <li><strong>%qa</strong>: Qualified arguments</li>
     *               <li><strong>%a</strong>: Unqualified arguments</li>
     *               <li><strong>%qe</strong>: Qualified exceptions</li>
     *               <li><strong>%e</strong>: Unqualified exceptions</li>
     *               </ul>
     */
    public MethodFormat(String format) {
        String pattern = format
                .replaceAll("%M", "{0}")
                .replaceAll("%r", "{1}")
                .replaceAll("%qc", "{2}")
                .replaceAll("%m", "{3}")
                .replaceAll("%qa", "{4}")
                .replaceAll("%qe", "{5}")
                .replaceAll("%c", "{6}")
                .replaceAll("%a", "{7}")
                .replaceAll("%e", "{8}");
        this.format = new MessageFormat(pattern);
    }

    public String format(Method method) {
        String signature = method.toGenericString();
        Matcher matcher = METHOD_PATTERN.matcher(signature);
        if (matcher.find()) {
            String M = matcher.group(1);
            String r = matcher.group(2);
            String qc = matcher.group(3);
            String m = matcher.group(4);
            String qa = matcher.group(5);
            String qe = matcher.group(6);
            String c = qc.replaceAll(PACKAGE_PATTERN, "");
            String a = qa.replaceAll(PACKAGE_PATTERN, "");
            String e = qe.replaceAll(PACKAGE_PATTERN, "");

            return format.format(new Object[]{
                    M,
                    r,
                    qc,
                    m,
                    qa,
                    qe,
                    c,
                    a,
                    e
            });
        } else {
            throw new CucumberException("Cucumber bug: Couldn't format " + signature);
        }
    }
}
