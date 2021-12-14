package day13;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay13 {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(solve1(readInput()));
    solve2(readInput());
  }

  public static long solve1(Game game) {
    game.fold(game.folds.get(0));
    return game.countPoints();
  }

  public static void solve2(Game game) {
    game.folds.forEach(game::fold);
    for (int j = 0; j <= game.yMax; j++) {
      for (int i = 0; i <= game.xMax; i++) {
        System.out.print(game.board[i][j] ? "█" : " ");
      }
      System.out.print(System.getProperty("line.separator"));
    }
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day13/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    Set<Point> points = new HashSet<>();
    List<String> folds = new ArrayList<>();
    String line = scanner.nextLine();
    while (!"".equals(line)) {
      String[] split = line.split(",");
      points.add(Point.of(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
      line = scanner.nextLine();
    }
    while (scanner.hasNextLine()) {
      folds.add(scanner.nextLine());
    }
    return new Game(points, folds);
  }

  public static class Game {

    Set<Point> points;
    Boolean[][] board;
    int xMax;
    int yMax;

    List<Fold> folds;

    public Game(Set<Point> points, List<String> folds) {
      this.points = points;
      xMax = points.stream().mapToInt(Point::getX).max().getAsInt();
      yMax = points.stream().mapToInt(Point::getY).max().getAsInt();
      board = new Boolean[xMax + 1][yMax + 1];
      for (int i = 0; i <= xMax; i++) {
        for (int j = 0; j <= yMax; j++) {
          board[i][j] = false;
        }
      }
      points.forEach(point -> board[point.x][point.y] = true);
      this.folds = folds.stream().map(fold -> {
        String[] split = fold.substring("fold along ".length()).split("=");
        return Fold.of(Integer.parseInt(split[1]), "x".equals(split[0]));
      }).collect(Collectors.toList());
    }

    public void fold(Fold fold) {
      int where = fold.value;
      if (!fold.x) {
        for (int i = 0; i <= xMax; i++) {
          for (int j = where + 1; j <= yMax; j++) {
            if (board[i][j]) {
              board[i][where - (j - where)] = true;
              board[i][j] = false;
            }
          }
        }
        yMax = where;
      } else {
        for (int i = where + 1; i <= xMax; i++) {
          for (int j = 0; j <= yMax; j++) {
            if (board[i][j]) {
              board[where - (i - where)][j] = true;
              board[i][j] = false;
            }
          }
        }
        xMax = where;
      }
    }

    public long countPoints() {
      return Arrays.stream(board).flatMap(Arrays::stream).filter(bool -> bool).count();
    }
  }

  @Data(staticConstructor = "of")
  public static class Point {

    final int x, y;
  }

  @Data(staticConstructor = "of")
  public static class Fold {

    final int value;
    final boolean x;
  }
}
