package com.ylzs.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * 说明：检验工具
 *
 * @author Administrator
 * 2019-10-11 10:09
 */
public class Assert {
    private Assert() {
    }

    public static void notBlank(CharSequence cs) {
        notBlank(cs, null);
    }

    public static void notBlank(CharSequence cs, String message, Object... values) {
        isTrue(StringUtils.isNotBlank(cs), message, values);
    }

    public static void notNull(Object o) {
        notNull(o, null);
    }

    public static void notNull(Object o, String message, Object... values) {
        isTrue(o != null, message, values);
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, null);
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        try {
            if (message == null) {
                Validate.isTrue(expression);
            } else {
                Validate.isTrue(expression, message, values);
            }
        } catch (Exception e) {
            throw new AssertException(e.getMessage());
        }
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, null);
    }

    public static void isFalse(boolean expression, String message, Object... values) {
        isTrue(!expression, message, values);
    }
}
