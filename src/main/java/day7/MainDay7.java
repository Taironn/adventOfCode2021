package day7;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author delikristof.demeter
 */
public class MainDay7 {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(solve(readInput(), true));
    System.out.println(solve(readInput(), false));
  }

  public static int solve(List<Integer> crabs, boolean first) {
//    double averageDouble = crabs.stream().mapToInt(Integer::intValue).average().getAsDouble();
//    int average = (int) averageDouble;
    int min = first ? countCost(crabs, 0) : countCost2(crabs,0);
    for (int i = 1; i< 2000; i++) {
      int cur = first ? countCost(crabs, i) : countCost2(crabs, i);
      if (cur < min) {
        min = cur;
        System.out.println("Iteration: " + i + " value: " + min);
      }
    }
    return min;
  }

  public static int countCost(List<Integer> crabs, int where) {
    return crabs.stream().mapToInt(crab -> Math.abs(crab - where)).sum();
  }

  public static int countCost2(List<Integer> crabs, int where) {
    return crabs.stream().mapToInt(crab -> gauss(Math.abs(crab - where))).sum();
  }

  public static int gauss(int n) {
    return Math.round((1 + n) / (float)2 * n);
  }

  public static List<Integer> readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day7/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);

    return Arrays.stream(scanner.nextLine().split(",")).map(Integer::parseInt)
        .collect(Collectors.toList());
  }
}
