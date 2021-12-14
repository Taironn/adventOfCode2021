package day10;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author delikristof.demeter
 */
public class MainDay10 {

  public static void main(String[] args) throws FileNotFoundException {
    Game game = readInput();
    System.out.println(solve1(game));
    System.out.println(solve2(game));
  }

  public static long solve2(Game game) {
    List<Long> completionScores = new ArrayList<>();
    for (List<Item> row : game.getInput()) {
      Long autoFillScore = getAutoFillScore(row);
      if (autoFillScore != null) {
        completionScores.add(autoFillScore);
      }
    }
    int middle = completionScores.size() / 2;
    completionScores.sort(Long::compareTo);
    return completionScores.get(middle);
  }

  public static Long getAutoFillScore(List<Item> row) {
    Stack<Item> stack = new Stack<>();
    for (Item item : row) {
      if (item.isOpening()) {
        stack.push(item);
      } else {
        if (stack.isEmpty() || !stack.peek().value.equals(item.pair)) {
          //Error here, so no need to autofill
          return null;
        } else {
          stack.pop();
        }
      }
    }
    long autoFillScore = 0;
    //Autofill score
    while (!stack.isEmpty()){
      Item item = Item.of(stack.pop().pair);
      autoFillScore *= 5;
      autoFillScore += item.getAutoFillValue();
    }
    return autoFillScore;
  }

  public static int solve1(Game game) {
    int sum = 0;
    for (List<Item> row : game.getInput()) {
      Integer errorScore = getErrorScore(row);
      if (errorScore != null) {
        sum += errorScore;
      }
    }
    return sum;
  }

  public static Integer getErrorScore(List<Item> row) {
    Stack<Item> stack = new Stack<>();
    for (Item item : row) {
      if (item.isOpening()) {
        stack.push(item);
      } else {
        if (stack.isEmpty() || !stack.peek().value.equals(item.pair)) {
          return item.getErrorValue();
        } else {
          stack.pop();
        }
      }
    }
    return null;
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day10/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<List<Item>> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      input.add(
          Arrays.stream(scanner.nextLine().split("")).map(Item::of).collect(Collectors.toList()));
    }
    return new Game(input);
  }

  @Data
  @AllArgsConstructor
  public static class Game {

    List<List<Item>> input;
  }

  @RequiredArgsConstructor
  @Getter
  public enum Item {
    A("(", ")", true, -1000000000, -1000000000),
    B("[", "]", true, -1000000000, -1000000000),
    C("{", "}", true, -1000000000, -1000000000),
    D("<", ">", true, -1000000000, -1000000000),
    AC(")", "(", false, 3,1),
    BC("]", "[", false, 57,2),
    CC("}", "{", false, 1197,3),
    DC(">", "<", false, 25137,4);

    private final String value;
    private final String pair;
    private final boolean opening;
    private final int errorValue;
    private final int autoFillValue;

    private static final Map<String, Item> lookup = Arrays.stream(Item.values())
        .collect(Collectors.toMap(Item::getValue, Function.identity()));

    public static Item of(String s) {
      return lookup.get(s);
    }
  }
}
