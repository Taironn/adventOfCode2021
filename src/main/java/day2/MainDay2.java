package day2;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay2 {

  public static void main(String[] args) throws FileNotFoundException {
    List<Command> input = readInput();
    System.out.println(calculateFinalPosition(input));
    System.out.println(calculateFinalPosition2(input));
  }

  private static int calculateFinalPosition(List<Command> input) {
    int x = 0;
    int y = 0;
    for (Command command : input) {
      switch (command.getDirection()) {
        case forward:
          x += command.getAmount();
          break;
        case down:
          y += command.getAmount();
          break;
        case up:
          y -= command.getAmount();
          break;
      }
    }
    return x * y;
  }

  private static int calculateFinalPosition2(List<Command> input) {
    int x = 0;
    int y = 0;
    int aim = 0;
    for (Command command : input) {
      switch (command.getDirection()) {
        case forward:
          x += command.getAmount();
          y += command.getAmount() * aim;
          break;
        case down:
          aim += command.getAmount();
          break;
        case up:
          aim -= command.getAmount();
          break;
      }
    }
    return x * y;
  }

  private static List<Command> readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day2/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<Command> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String[] split = scanner.nextLine().split(" ");
      input.add(Command.of(Direction.valueOf(split[0]), Integer.parseInt(split[1])));
    }
    return input;
  }

  public enum Direction {
    forward,
    down,
    up
  }


  @Data(staticConstructor = "of")
  public static class Command {

    private final Direction direction;
    private final int amount;
  }
}
