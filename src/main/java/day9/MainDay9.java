package day9;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay9 {

  public static void main(String[] args) throws FileNotFoundException {
    Game game = readInput();
    System.out.println(solve(game, true));
    System.out.println(solve(game, false));
  }

  public static long solve(Game game, boolean first) {
    int sum = 0;
    List<Long> basinSizes = new ArrayList<>();
    for (int i = 0; i <= game.getX(); i++) {
      for (int j = 0; j <= game.getY(); j++) {
        if (isMin(game, i, j)) {
          sum += game.getBoard().get(i).get(j) + 1;
          if (!first) {
            basinSizes.add(getBasinSize(game, i, j));
          }
        }
      }
    }
    if (first) {
      return sum;
    } else {
      List<Long> descending = basinSizes.stream().sorted((l1, l2) -> Long.compare(l2, l1)).limit(3)
          .collect(Collectors.toList());
      return descending.get(1) * descending.get(2) * descending.get(0);
    }
  }

  private static long getBasinSize(Game game, int i, int j) {
    Map<Integer, Set<Integer>> points = new HashMap<>();
    for (int k = 0; k <= game.getX(); k++) {
      points.put(k, new HashSet<>());
    }
    points.get(i).add(j);
    getBasin(game, i, j, points);
    return points.values().stream().map(Set::size).mapToInt(Integer::intValue).sum();
  }

  private static void getBasin(Game game, int i, int j, Map<Integer, Set<Integer>> points) {
    int val = game.getBoard().get(i).get(j);
    if (val == 9) {
      //Return on purpose
    } else {
      //Add current
      points.get(i).add(j);
      if (i != 0 && !isIn(points, i - 1, j) && game.getBoard().get(i - 1).get(j) > val) {
        getBasin(game, i - 1, j, points);
      }
      if (i != game.getX() && !isIn(points, i + 1, j) && game.getBoard().get(i + 1).get(j) > val) {
        getBasin(game, i + 1, j, points);
      }
      if (j != 0 && !isIn(points, i, j - 1) && game.getBoard().get(i).get(j - 1) > val) {
        getBasin(game, i, j - 1, points);
      }
      if (j != game.getY() && !isIn(points, i, j + 1) && game.getBoard().get(i).get(j + 1) > val) {
        getBasin(game, i, j + 1, points);
      }
    }
  }

  private static boolean isIn(Map<Integer, Set<Integer>> points, int i, int j) {
    return points.get(i) != null && points.get(i).contains(j);
  }

  private static boolean isMin(Game game, int i, int j) {
    int val = game.getBoard().get(i).get(j);
    return (
        (i == 0 || game.getBoard().get(i - 1).get(j) > val)
            && (i == game.getX() || game.getBoard().get(i + 1).get(j) > val)
            && (j == 0 || game.getBoard().get(i).get(j - 1) > val)
            && (j == game.getY() || game.getBoard().get(i).get(j + 1) > val)
    );
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day9/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<List<Integer>> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      input.add(Arrays.stream(scanner.nextLine().split("")).map(Integer::valueOf)
          .collect(Collectors.toList()));
    }
//    int[][] board = new int[input.size()][input.get(0).size()];
//    for (int i = 0; i < input.size(); i++) {
//      for (int j = 0; j < input.get(0).size(); j++) {
//        board[i][j] = input.get(i).get(j)
//      }
//    }
    return new Game(input, input.size() - 1, input.get(0).size() - 1);
  }


  @Data
  @AllArgsConstructor
  public static class Game {

    List<List<Integer>> board;
    int x;
    int y;
  }

  @AllArgsConstructor
  @Data
  public static class Point {

    int x;
    int y;
  }
}
