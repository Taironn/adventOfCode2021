package day15;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainDay15 {

  private static final int size = 500;

  public static void main(String[] args) throws FileNotFoundException {
//    System.out.println(solve1(readInput()));
    System.out.println(solve2(readInput()));
  }

  public static int solve1(Game game) {
    game.dijkstraFrom00();
    return game.map[99][99].sumRisk;
  }
  public static int solve2(Game game) {
    game.dijkstraFrom00();
    return game.map[size - 1][size - 1].sumRisk;
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day15/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    Place[][] map = new Place[size][size];
    int i = 0;
    while (scanner.hasNextLine()) {
      String[] split = scanner.nextLine().split("");
      for (int j = 0; j < 100; j++) {
        map[i][j] = new Place(Integer.parseInt(split[j]), i, j);
      }
      i++;
    }
    for (int ib = 0; ib < 5; ib++) {
      for (int jb = 0; jb < 5; jb++) {
        for (int ii = 0; ii < 100; ii++) {
          for (int jj = 0; jj < 100; jj++) {
            if (ib == 0 && jb == 0) {
              continue;
            }
            int orig = map[ii][jj].riskLevel;
            orig = orig + ib + jb;
            if (orig > 9) {
              orig = orig - 9;
            }
            map[ib * 100 + ii][jb * 100 + jj] =
                    new Place(orig, ib * 100 + ii, jb * 100 + jj);
          }
        }
      }
    }

    return new Game(map);
  }

  public static class Game {
    Place[][] map;
    Set<Place> unvisited;

    public Game(Place[][] map) {
      this.map = map;
      unvisited = Arrays.stream(map).flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    public void dijkstraFrom00() {
      Place current = map[0][0];
      // For first one
      current.sumRisk = 0;
      while (!unvisited.isEmpty()) {
        if (unvisited.size() % 1000 == 0) {
          System.out.println(unvisited.size());
        }
        current = getMinUnvisited(unvisited);
        if (current.i != 0) {
          Place left = map[current.i - 1][current.j];
          visit(current, left);
        }
        if (current.i != size - 1) {
          Place right = map[current.i + 1][current.j];
          visit(current, right);
        }
        if (current.j != 0) {
          Place up = map[current.i][current.j - 1];
          visit(current, up);
        }
        if (current.j != size - 1) {
          Place down = map[current.i][current.j + 1];
          visit(current, down);
        }
        // Mark current as visited
        current.visited = true;
        unvisited.remove(current);
      }
    }
  }

  private static void visit(Place current, Place neighbour) {
    if (!neighbour.visited) {
      if (current.sumRisk + neighbour.riskLevel < neighbour.sumRisk) {
        neighbour.sumRisk = current.sumRisk + neighbour.riskLevel;
      }
    }
  }

  public static Place getMinUnvisited(Set<Place> unvisited) {
    return unvisited.stream().min(Comparator.comparingInt(Place::getSumRisk)).get();
  }

  @Getter
  public static class Place {
    int riskLevel;
    int sumRisk;
    boolean visited;
    int i;
    int j;

    public Place(int riskLevel, int i, int j) {
      this.riskLevel = riskLevel;
      this.i = i;
      this.j = j;
      sumRisk = Integer.MAX_VALUE;
      visited = false;
    }
  }
}
