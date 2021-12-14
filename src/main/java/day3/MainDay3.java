package day3;

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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author delikristof.demeter
 */
public class MainDay3 {

  public static void main(String[] args) throws FileNotFoundException {
    List<List<Integer>> input = readInput();
    System.out.println(calculate(input));
    System.out.println(calculate2(input));

  }

  private static List<List<Integer>> readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day3/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<List<Integer>> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String row = scanner.nextLine();
      Integer[] info = new Integer[row.length()];
      input.add(
          Arrays.stream(row.split("")).map(Integer::valueOf).collect(Collectors.toList())
      );
    }
    return input;
  }

  private static Integer calculate2(List<List<Integer>> input) {
    List<Integer> oxygen = calculateRecursive(input,0,true).get(0);
    List<Integer> co2 = calculateRecursive(input,0,false).get(0);

    int oxy = Integer.parseInt(oxygen.stream().map(Object::toString)
        .collect(Collectors.joining("")),2);
    int co = Integer.parseInt(co2.stream().map(Object::toString)
        .collect(Collectors.joining("")),2);

    return oxy * co;
  }

  private static List<List<Integer>> calculateRecursive(List<List<Integer>> input, int index,
      boolean oxygen) {
    int nill = 0;
    int one = 0;
    for (List<Integer> row : input) {
      int val = row.get(index);
      if (val == 0) {
        nill++;
      } else {
        one++;
      }
    }
    int toKeep = nill == one ? (oxygen ? 1 : 0) :
        nill > one ? (oxygen ? 0 : 1) : (oxygen ? 1 : 0);
    List<List<Integer>> matching = input.stream().filter(row -> row.get(index) == toKeep)
        .collect(Collectors.toList());
    if (matching.size() == 1) {
      return matching;
    } else {
      return calculateRecursive(matching,index + 1, oxygen);
    }
  }

  private static int calculate(List<List<Integer>> input) {
    Map<Integer, Map<Integer, Integer>> stats = IntStream.range(0, input.get(0).size())
        .boxed()
        .collect(Collectors.toMap(Function.identity(), num -> new HashMap<>(Map.of(0, 0, 1, 0))));

    for (int i = 0; i < input.size(); i++) {
      for (int j = 0; j < input.get(i).size(); j++) {
        Integer val = stats.get(j).get(input.get(i).get(j));
        stats.get(j).put(input.get(i).get(j), val + 1);
      }
    }
    Map<Integer, Integer> gamma = stats.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, entry -> {
          Integer maxFace = -1;
          Integer maxNum = -1000;
          for (Map.Entry<Integer, Integer> inner : entry.getValue().entrySet()) {
            if (inner.getValue() > maxNum) {
              maxFace = inner.getKey();
              maxNum = inner.getValue();
            }
          }
          return maxFace;
        }));

    Map<Integer, Integer> epsilon = stats.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, entry -> {
          Integer maxFace = -1;
          Integer maxNum = 10000000;
          for (Map.Entry<Integer, Integer> inner : entry.getValue().entrySet()) {
            if (inner.getValue() < maxNum) {
              maxFace = inner.getKey();
              maxNum = inner.getValue();
            }
          }
          return maxFace;
        }));

    String gammaBinary = gamma.values().stream().map(Object::toString)
        .collect(Collectors.joining(""));
    String epsilonBinary = epsilon.values().stream().map(Object::toString)
        .collect(Collectors.joining(""));

    int gammaDecimal = Integer.parseInt(gammaBinary, 2);
    int epsilonDecimal = Integer.parseInt(epsilonBinary, 2);

    return gammaDecimal * epsilonDecimal;
  }


}
