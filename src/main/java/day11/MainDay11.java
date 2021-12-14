package day11;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author delikristof.demeter
 */
public class MainDay11 {

  public static void main(String[] args) throws FileNotFoundException {
//    System.out.println(solve1(readInput()));
    System.out.println(solve2(readInput()));
  }

  public static long solve2(Game game) {
    long flashes = 0;
    for (int i = 0; i < 10000; i++) {
      game.iterate();
      long newFlash = game.flashes - flashes;
      System.out.println(String.format("Turn: %d   flashes: %d", i, game.flashes - flashes));
      flashes = game.flashes;
      if (newFlash == 100) {
        return i;
      }
    }
    return -1;
  }

  public static long solve1(Game game) {
    for (int i = 0; i < 100; i++) {
      game.iterate();
    }
    return game.getFlashes();
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day11/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    Octopus[][] field = new Octopus[10][10];
    int i = 0;
    while (scanner.hasNextLine()) {
      int j = 0;
      for (String s : scanner.nextLine().split("")) {
        Octopus octopus = Octopus.of(Integer.parseInt(s));
        field[i][j++] = octopus;
      }
      i++;
    }
    return new Game(field, 0);
  }

  @Data
  @AllArgsConstructor
  public static class Game {

    Octopus[][] field;
    long flashes;

    public void iterate() {
      //1. Increase energy level by 1
      Arrays.stream(field).flatMap(Arrays::stream).forEach(Octopus::increment);
      //2. Flash all at least 9s
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
          flash(i, j);
        }
      }
      //3. Set all flashed to 0
      Arrays.stream(field).flatMap(Arrays::stream).forEach(Octopus::setToZeroIfFlashed);
    }

    private void flash(int i, int j) {
      Octopus octopus = field[i][j];
      if (octopus.energy > 9 && !octopus.flashedThisTurn) {
        //Increase neighbour and call recursively
        octopus.flashedThisTurn = true;
        flashes++;
        if (i != 0 && j != 0) {
          field[i - 1][j - 1].increment();
          flash(i - 1, j - 1);
        }
        if (i != 0) {
          field[i - 1][j].increment();
          flash(i - 1, j);
        }
        if (i != 0 && j != 9) {
          field[i - 1][j + 1].increment();
          flash(i - 1, j + 1);
        }
        if (j != 0) {
          field[i][j - 1].increment();
          flash(i, j - 1);
        }
        if (j != 9) {
          field[i][j + 1].increment();
          flash(i, j + 1);
        }
        if (i != 9 && j != 0) {
          field[i + 1][j - 1].increment();
          flash(i + 1, j - 1);
        }
        if (i != 9) {
          field[i + 1][j].increment();
          flash(i + 1, j);
        }
        if (i != 9 && j != 9) {
          field[i + 1][j + 1].increment();
          flash(i + 1, j + 1);
        }
      }
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Octopus {

    private int energy;
    private boolean flashedThisTurn;

    public static Octopus of(int energy) {
      return new Octopus(energy, false);
    }

    public void increment() {
      energy++;
    }

    public void setToZeroIfFlashed() {
      if (flashedThisTurn) {
        energy = 0;
        flashedThisTurn = false;
      }
    }
  }
}
