package edu.miu.blog.app.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;

public class SlugUtil {
    private static final Random RANDOM = new Random();

    public static String slugify(String input) {
        String base = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\w\\s-]", "")
                .trim()
                .replaceAll("[-\\s]+", "-")
                .toLowerCase(Locale.ENGLISH);
        return base + "-" + randomAlphanumeric(6);
    }

    private static String randomAlphanumeric(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}