package day8;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay8 {

  public static void main(String[] args) throws FileNotFoundException {
    List<Riddle> input = readInput();
    System.out.println(solve1(input));
    System.out.println(solve2(input));

  }

  public static long solve1(List<Riddle> input) {
    return input.stream()
        .flatMap(riddle -> riddle.getOutput().stream())
        .map(String::length)
        .filter(len -> len == 2 || len == 4 || len == 3 || len == 7)
        .count();
  }

  public static long solve2(List<Riddle> input) {
    return input.stream().mapToInt(Riddle::getNum).sum();
  }

  public static List<Riddle> readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day8/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);

    List<Riddle> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String[] inOut = scanner.nextLine().split("\\Q | \\E");
      input.add(new Riddle(
          Arrays.asList(inOut[0].split(" ")),
          Arrays.asList(inOut[1].split(" "))
      ));
    }

    return input;
  }

  @Data
  @AllArgsConstructor
  public static class Riddle {

    List<String> input;
    List<String> output;

    public Riddle(List<String> input, List<String> output) {
      this.input = input;
      this.output = output;
      solve();
    }

    public List<String> getSortedOutput() {
      return output.stream().map(str -> {
        char[] charray = str.toCharArray();
        Arrays.sort(charray);
        return new String(charray);
      }).collect(Collectors.toList());
    }

    Map<Integer, String> representationMap = new HashMap<>();
    Map<String, Integer> invReprMap = new HashMap<>();

    public int getNum() {
      return Integer.parseInt(getSortedOutput().stream().map(invReprMap::get).map(Object::toString)
          .collect(Collectors.joining()));
    }

    public void solve() {
      Map<Integer, List<String>> lengthMap = input.stream()
          .collect(Collectors.groupingBy(String::length, Collectors.toList()));
      Map<Integer, String> representationMap = new HashMap<>();
      representationMap.put(1, lengthMap.get(2).get(0));
      representationMap.put(4, lengthMap.get(4).get(0));
      representationMap.put(7, lengthMap.get(3).get(0));
      representationMap.put(8, lengthMap.get(7).get(0));
      fill325(representationMap, lengthMap);
      fill069(representationMap, lengthMap);
      invReprMap = representationMap.entrySet().stream()
          .collect(Collectors.toMap(entry -> {
            char[] charray = entry.getValue().toCharArray();
            Arrays.sort(charray);
            return new String(charray);
          }, Entry::getKey));
    }

    private static void fill325(Map<Integer, String> representationMap,
        Map<Integer, List<String>> lengthMap) {
      String three = lengthMap.get(5).stream()
          .filter(str ->
              str.contains(Character.toString(representationMap.get(1).charAt(0)))
                  && str.contains(Character.toString(representationMap.get(1).charAt(1))))
          .collect(Collectors.toList()).get(0);
      representationMap.put(3, three);
      List<String> twoAndFive = lengthMap.get(5).stream().filter(str -> !three.equals(str)).collect(
          Collectors.toList());
      if (commonChars(twoAndFive.get(0), representationMap.get(4)) == 2) {
        representationMap.put(2, twoAndFive.get(0));
        representationMap.put(5, twoAndFive.get(1));
      } else {
        representationMap.put(2, twoAndFive.get(1));
        representationMap.put(5, twoAndFive.get(0));
      }

    }

    private static void fill069(Map<Integer, String> representationMap,
        Map<Integer, List<String>> lengthMap) {
      String six = lengthMap.get(6).stream()
          .filter(str -> commonChars(str, representationMap.get(1)) == 1)
          .collect(Collectors.toList()).get(0);
      representationMap.put(6, six);
      List<String> nullAndNine = lengthMap.get(6).stream().filter(str -> !six.equals(str)).collect(
          Collectors.toList());
      if (commonChars(nullAndNine.get(0), representationMap.get(3)) == 5) {
        representationMap.put(9, nullAndNine.get(0));
        representationMap.put(0, nullAndNine.get(1));
      } else {
        representationMap.put(9, nullAndNine.get(1));
        representationMap.put(0, nullAndNine.get(0));
      }
    }

    private static int commonChars(String a, String b) {
      int common = 0;
      for (char c : a.toCharArray()) {
        for (char d : b.toCharArray()) {
          if (c == d) {
            common++;
          }
        }
      }
      return common;
    }
  }
}
