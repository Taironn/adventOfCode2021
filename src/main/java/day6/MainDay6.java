package day6;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author delikristof.demeter
 */
public class MainDay6 {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(solve1(readInput()));
    System.out.println(solve2(readInput()));

  }

  private static long solve2(Game game) {
    Game2 game2 = new Game2(game.getSwarm());
    for (int i = 0; i < 256; i++){
      game2.increment();
    }
    return game2.total();
  }

  private static int solve1(Game game) {
    for (int i = 0; i < 80; i++) {
      game.increment();
    }
    return game.getSwarm().size();
  }

  private static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day6/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<Fish> initialSwarm = Arrays.stream(scanner.nextLine().split(","))
        .map(Integer::parseInt)
        .map(Fish::new)
        .collect(Collectors.toList());

    return new Game(initialSwarm, new ArrayList<>());
  }

  public static class Game2 {

    private long[] swarm;
    private long newBorn;
    private long newBorn2;
    int pos;

    public Game2(List<Fish> swarm) {
      this.swarm = new long[7];
      for (Fish fish : swarm) {
        this.swarm[fish.getLife()]++;
      }
      pos = 0;
      newBorn = 0;
      newBorn2 = 0;
    }

    public void increment() {
      long newest = swarm[pos];
      swarm[pos] += newBorn2;
      newBorn2 = newBorn;
      newBorn = newest;
      if (pos == 6) {
        pos = 0;
      } else {
        pos++;
      }
    }

    public long total() {
      return newBorn + newBorn2 + Arrays.stream(swarm).sum();
    }
  }

  @Data
  @AllArgsConstructor
  public static class Game {

    private List<Fish> swarm;
    private List<Fish> newBorns;

    public void increment() {
      for (Fish fish : swarm) {
        if (fish.decrement()) {
          newBorns.add(new Fish(8));
        }
      }
      swarm.addAll(newBorns);
      newBorns.clear();
    }
  }

  @Getter
  @AllArgsConstructor
  public static class Fish {

    private int life;

    public boolean decrement() {
      if (life == 0) {
        life = 6;
        return true;
      } else {
        life--;
        return false;
      }
    }
  }
}
