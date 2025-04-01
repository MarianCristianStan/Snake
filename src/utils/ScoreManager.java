package utils;

import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String FILE_PATH = "scores.txt";
    private static final int MAX_SCORES = 5;

    public static void saveScore(int score) {
        List<Integer> scores = loadScores();
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());
        if (scores.size() > MAX_SCORES)
            scores = scores.subList(0, MAX_SCORES);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int s : scores) {
                writer.write(s + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> loadScores() {
        List<Integer> scores = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scores.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scores;
    }
}
