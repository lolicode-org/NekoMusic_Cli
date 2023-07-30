package org.lolicode.nekomusiccli.utils;

import org.apache.commons.text.StringSubstitutor;

public class StrEnvSubstitutor {
    private static final StringSubstitutor substitutor = new StringSubstitutor(System.getenv());

    public static String replace(String str) {
        return substitutor.replace(str);
    }
}
