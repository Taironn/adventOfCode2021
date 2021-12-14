package day4;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay4 {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(getWinningBoardStat(readInput()));
    System.out.println(getLastWinningBoardStat(readInput()));
  }

  private static Integer getLastWinningBoardStat(Game game) {
    for (Integer num : game.getNumbers()) {
      List<Point[][]> boards = game.getBoards();
      for (int k = 0; k < boards.size(); k++) {
        Point[][] board = boards.get(k);
        for (int i = 0; i < 5; i++) {
          for (int j = 0; j < 5; j++) {
            if (board[i][j].getValue() == num) {
              board[i][j].setPulled(true);
            }

          }
        }
        if (!game.getHasWonMap().get(k) && hasWon(board)) {
          game.getHasWonMap().put(k, true);
          game.setRemaining(game.getRemaining() - 1);
          if (game.getRemaining() == 0) {
            return getStat(board, num);
          }
        }
      }
    }
    //Shouldn't happen
    return null;
  }

  private static Integer getWinningBoardStat(Game game) {
    for (Integer num : game.getNumbers()) {
      for (Point[][] board : game.getBoards()) {
        for (int i = 0; i < 5; i++) {
          for (int j = 0; j < 5; j++) {
            if (board[i][j].getValue() == num) {
              board[i][j].setPulled(true);
            }

          }
        }
        if (hasWon(board)) {
          return getStat(board, num);
        }
      }
    }
    //Shouldn't happen
    return null;
  }

  private static int getStat(Point[][] board, int current) {
    int sum = 0;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (!board[i][j].isPulled()) {
          sum += board[i][j].getValue();
        }
      }
    }
    return sum * current;
  }

  private static boolean hasWon(Point[][] board) {
    for (int i = 0; i < 5; i++) {
      if (board[i][0].isPulled() && board[i][1].isPulled()
          && board[i][2].isPulled() && board[i][3].isPulled()
          && board[i][4].isPulled()) {
        return true;
      }
      if (board[0][i].isPulled() && board[1][i].isPulled()
          && board[2][i].isPulled() && board[3][i].isPulled()
          && board[4][i].isPulled()) {
        return true;
      }
    }
    return false;
  }

  private static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day4/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    String firstRow = scanner.nextLine();
    Game game = new Game();
    game.setNumbers(Arrays.stream(firstRow.split(",")).mapToInt(Integer::parseInt).boxed()
        .collect(Collectors.toList()));
    game.setBoards(new ArrayList<>());
    //Skip first empty line
    scanner.nextLine();
    Point[][] current = new Point[5][5];
    int currentLine = 0;
    while (scanner.hasNextLine()) {
      String row = scanner.nextLine();
      if (row == null || row.isEmpty()) {
        game.getBoards().add(current);
        current = new Point[5][5];
        currentLine = 0;
      } else {
        List<Integer> line = Arrays.stream(row.split("  *"))
            .filter(str -> !str.isEmpty())
            .map(Integer::parseInt)
            .collect(Collectors.toList());
        for (int i = 0; i < line.size(); i++) {
          Integer value = line.get(i);
          current[currentLine][i] = new Point(value, false);
        }
        currentLine++;
      }
    }
    game.getBoards().add(current);
    Map<Integer, Boolean> wonMap = new HashMap<>();
    for (int i = 0; i < game.getBoards().size(); i++) {
      wonMap.put(i, false);
    }
    game.setHasWonMap(wonMap);
    game.setRemaining(wonMap.size());
    return game;
  }

  @Data
  public static class Game {

    List<Integer> numbers;
    List<Point[][]> boards;
    Map<Integer, Boolean> hasWonMap;
    Integer remaining;
  }

  @Data
  @AllArgsConstructor
  public static class Point {

    int value;
    boolean pulled = false;
  }
}
