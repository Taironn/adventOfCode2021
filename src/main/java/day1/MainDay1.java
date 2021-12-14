package day1;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author delikristof.demeter
 */
public class MainDay1 {

  public static void main(String[] args) throws FileNotFoundException {
    List<Integer> input = readInput();
    System.out.println(countIncreaseWithSlidingWindow(input, 1));
    System.out.println(countIncreaseWithSlidingWindow(input, 3));
  }

  private static int countIncreaseWithSlidingWindow(List<Integer> input, int windowSize) {
    int accum = 0;
    int last = input.stream().limit(windowSize).mapToInt(Integer::intValue).sum();
    for (int i = 1; i < input.size(); i++) {
      int currentSum = input.stream().skip(i).limit(windowSize).mapToInt(Integer::intValue).sum();
      if (currentSum > last) {
        accum ++;
      }
      last = currentSum;
    }
    return accum;
  }

  private static List<Integer> readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day1/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<Integer> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      input.add(Integer.parseInt(scanner.nextLine()));
    }
    return input;
  }

}
