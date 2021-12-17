package day14;

import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** @author delikristof.demeter */
public class MainDay14 {

  public static void main(String[] args) throws FileNotFoundException {
        System.out.println(solve1(readInput()));
    System.out.println(solve2(readInput()));
  }

  public static long solve1(Game game) {
    for (int i = 0; i < 10; i++) {
      game.iterate();
    }
    return game.getStat();
  }

  public static long solve2(Game game) {
    for (int i = 0; i < 40; i++) {
      game.iterate2();
    }
    return game.getStat2();
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day14/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    String startStrain = scanner.nextLine();
    scanner.nextLine(); // empty line
    List<Rule> rules = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String[] split = scanner.nextLine().split(" -> ");
      rules.add(Rule.of(split[0], split[1]));
    }
    return new Game(startStrain, rules);
  }

  public static class Game {

    String[] startStrain;
    List<Rule> rules;
    Map<String, String> ruleMap;
    Map<String, Long> pairs;

    public Game(String startStrain, List<Rule> rules) {
      this.startStrain = startStrain.split("");
      this.rules = rules;
      this.pairs = new HashMap<>();
      ruleMap = rules.stream().collect(Collectors.toMap(Rule::getFrom, Rule::getInject));
      for (int i = 0; i < startStrain.length() - 1; i++) {
        String pair = startStrain.substring(i, i + 2);
        Long current = pairs.get(pair);
        pairs.put(pair, current == null ? 1L : current + 1L);
      }
    }

    public void iterate2() {
      Map<String, Long> newPairs = new HashMap<>();
      for (Map.Entry<String, Long> entry : pairs.entrySet()) {
        String newLetter = ruleMap.get(entry.getKey());
        String pair1 = entry.getKey().charAt(0) + newLetter;
        String pair2 = newLetter + entry.getKey().charAt(1);
        Long current = newPairs.get(pair1);
        newPairs.put(pair1, current == null ? entry.getValue() : current + entry.getValue());
        current = newPairs.get(pair2);
        newPairs.put(pair2, current == null ? entry.getValue() : current + entry.getValue());
      }
      pairs = newPairs;
    }

    public void iterate() {
//      System.out.println(Arrays.toString(startStrain));
//      System.out.println(pairs);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < startStrain.length - 1; i++) {
        sb.append(startStrain[i]);
        String inject = ruleMap.get(startStrain[i] + startStrain[i + 1]);
        if (inject != null) {
          sb.append(inject);
        }
      }
      sb.append(startStrain[startStrain.length - 1]);
      String newStrain = sb.toString();
      startStrain = newStrain.split("");

//      pairs = new HashMap<>();
//      for (int i = 0; i < startStrain.length - 1; i++) {
//        String pair = startStrain[i] + startStrain[i + 1];
//        Long current = pairs.get(pair);
//        pairs.put(pair, current == null ? 1L : current + 1L);
//      }
    }

    public long getStat() {
      Map<String, Long> stats =
          Arrays.stream(startStrain)
              .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      Map.Entry<String, Long> min = stats.entrySet().iterator().next();
      Map.Entry<String, Long> max = stats.entrySet().iterator().next();
      for (Map.Entry<String, Long> entry : stats.entrySet()) {
        if (entry.getValue() > max.getValue()) {
          max = entry;
        }
        if (entry.getValue() < min.getValue()) {
          min = entry;
        }
      }
      return max.getValue() - min.getValue();
    }

    public long getStat2() {
      Map<String, Long> stats =
          pairs.entrySet().stream()
              .flatMap(
                  entry ->
                      entry.getKey().charAt(0) == entry.getKey().charAt(1)
                          ? Map.of(entry.getKey().substring(0, 1), entry.getValue() * 2L)
                              .entrySet()
                              .stream()
                          : Map.of(
                              entry.getKey().substring(0, 1),
                              entry.getValue(),
                              entry.getKey().substring(1, 2),
                              entry.getValue())
                              .entrySet()
                              .stream())
              .collect(
                  Collectors.groupingBy(
                      Map.Entry::getKey,
                      //                          Collectors.mapping(Map.Entry::getValue,
                      // Collectors.summingLong(val -> val))));
                      Collectors.summingLong(Map.Entry::getValue)));
      Long nCount = stats.get("N");
      Long sCount = stats.get("S");
      stats.put("N", nCount + 1);
      stats.put("S", sCount + 1);
      for (Map.Entry<String,Long> entry : stats.entrySet()) {
        stats.put(entry.getKey(), entry.getValue() / 2);
      }

      Map.Entry<String, Long> min = stats.entrySet().iterator().next();
      Map.Entry<String, Long> max = stats.entrySet().iterator().next();
      for (Map.Entry<String, Long> entry : stats.entrySet()) {
        if (entry.getValue() > max.getValue()) {
          max = entry;
        }
        if (entry.getValue() < min.getValue()) {
          min = entry;
        }
      }
      return max.getValue() - min.getValue();
    }
  }

  @Data(staticConstructor = "of")
  private static class Rule {

    final String from;
    final String inject;
  }
}
