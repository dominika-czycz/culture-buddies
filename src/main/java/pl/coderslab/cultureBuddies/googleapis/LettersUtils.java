package pl.coderslab.cultureBuddies.googleapis;

import java.text.Normalizer;

public class LettersUtils {
    public static String replaceSpecialLetters(String word) {
        return Normalizer
                .normalize(word, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
