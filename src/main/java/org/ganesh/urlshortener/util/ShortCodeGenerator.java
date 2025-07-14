package org.ganesh.urlshortener.util;

import java.util.UUID;

public class ShortCodeGenerator {
    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
