package day18;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class MainDay18 {

  public static void main(String[] args) throws FileNotFoundException {
    NumberFormat formatter = new DecimalFormat("#0.00000");
    long start = System.currentTimeMillis();
    System.out.println(solve1(readInput()));
    System.out.printf("Execution time: %s seconds%n",
        formatter.format((System.currentTimeMillis() - start) / 1000d));
    System.out.println(solve2(readInput()));
    System.out.printf("Execution time: %s seconds%n",
        formatter.format((System.currentTimeMillis() - start) / 1000d));
  }

  public static long solve1(Game game) {
    SfNumber sum = game.numbers.get(0);
    for (int i = 1; i < game.numbers.size(); i++) {
      sum = addSfNumbers(sum, game.numbers.get(i), false);
      System.out.println("After addition: " + sum);
      sum.reduce(false);
      System.out.println("After reduction: " + sum);
    }
    return sum.getMagnitude();
  }

  public static long solve2(Game game) {
    long largestMagnitude = 0;
    for (int i = 0; i < game.numbers.size(); i++) {
      for (int j = 0; j < game.numbers.size(); j++) {
        if (i == j) {
          continue;
        }
        long newMagnitude = addSfNumbers(game.numbers.get(i), game.numbers.get(j),
            true).getMagnitude();
        long newMagnitude2 = addSfNumbers(game.numbers.get(j), game.numbers.get(i),
            true).getMagnitude();
        if (newMagnitude > largestMagnitude) {
          largestMagnitude = newMagnitude;
        }
        if (newMagnitude2 > largestMagnitude) {
          largestMagnitude = newMagnitude2;
        }
      }
    }
    return largestMagnitude;
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day18/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<SfNumber> numbers = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String num = scanner.nextLine();
      SfNumber sfNumber = parse(num, null);
      numbers.add(sfNumber);
    }
    return new Game(numbers);
  }

  public static SfNumber parse(String s, SfNumber parent) {
    if (s.length() == 1) { //literal
      return new SfNumber(Long.parseLong(s), parent);
    }
    // Find the middle split
    int cur = 1; //First character was '['

    //a,XXX case
    if (s.charAt(cur) != '[') {
      SfNumber sfNumber = new SfNumber(null, null, parent);
      sfNumber.left = parse(s.substring(cur, cur + 1), sfNumber);
      sfNumber.right = parse(s.substring(cur + 2, s.length() - 1), sfNumber);
      return sfNumber;
    }
    cur++;
    // [...],XXX case
    int open = 1; // From the current
    while (open != 0) {
      char c = s.charAt(cur++);
      switch (c) {
        case '[':
          open++;
          break;
        case ']':
          open--;
          break;
        //Default: do nothing
      }
    }
    //Now cur is on the "," after the close
    if (s.charAt(cur) != ',') {
      throw new IllegalStateException("Not at ',' this shouldn't happen!");
    }
    String firstHalf = s.substring(1, cur);
    String secondHalf = s.substring(cur + 1, s.length() - 1);
    SfNumber sfNumber = new SfNumber();
    sfNumber.parent = parent;
    sfNumber.left = parse(firstHalf, sfNumber);
    sfNumber.right = parse(secondHalf, sfNumber);
    return sfNumber;
  }

  public static SfNumber addSfNumbers(SfNumber a, SfNumber b, boolean reduce) {
    if (!a.isTop() || !b.isTop()) {
      throw new IllegalStateException("Can only add top numbers (where parent == null)");
    }
    SfNumber sfNumber = new SfNumber(null, null, null);
    sfNumber.left = a.deepCopy(sfNumber);
    sfNumber.right = b.deepCopy(sfNumber);
    if (reduce) {
      sfNumber.reduce(false);
    }
    return sfNumber;
  }

  @AllArgsConstructor
  public static class Game {

    List<SfNumber> numbers;
  }

  @NoArgsConstructor
  public static class SfNumber {

    SfNumber left;
    SfNumber right;
    SfNumber parent;
    Long value;

    public SfNumber(Long value, SfNumber parent) {
      this.value = value;
      this.parent = parent;
    }

    public SfNumber(SfNumber left, SfNumber right, SfNumber parent) {
      this.left = left;
      this.right = right;
      this.parent = parent;
    }

    public boolean isTop() {
      return parent == null;
    }

    public boolean isLiteral() {
      return value != null;
    }

    public boolean isCompact() {
      return left != null && left.isLiteral()
          && right != null && right.isLiteral();
    }

    public SfNumber deepCopy(SfNumber parent) {
      if (this.isLiteral()) {
        return new SfNumber(Long.valueOf(this.value), parent);
      } else {
        SfNumber newNumber = new SfNumber(null, null, parent);
        newNumber.left = left.deepCopy(newNumber);
        newNumber.right = right.deepCopy(newNumber);
        return newNumber;
      }
    }

    @Override
    public String toString() {
      if (isLiteral()) {
        return value.toString();
      } else {
        return String.format("[%s,%s]", left.toString(), right.toString());
      }
    }

    public SfNumber getLeftNeighbour() {
      if (isTop()) {
        return null;
      }
      SfNumber leftStep = parent;
      boolean up = true;
      SfNumber last = this;
      while (!leftStep.isLiteral()) {
        if (leftStep.left == last) {
          if (leftStep.isTop()) {
            return null; //There is no left neighbour, reached top
          }
          last = leftStep;
          leftStep = leftStep.parent;
        } else if (up) {
          leftStep = leftStep.left;
          up = false;
        } else {
          leftStep = leftStep.right;
        }
      }
      return leftStep;
    }

    public SfNumber getRightNeighbour() {
      if (isTop()) {
        return null;
      }
      SfNumber rightStep = parent;
      boolean up = true;
      SfNumber last = this;
      while (!rightStep.isLiteral()) {
        if (rightStep.right == last) {
          if (rightStep.isTop()) {
            return null; //There is no right neighbour, reached top
          }
          last = rightStep;
          rightStep = rightStep.parent;
        } else if (up) {
          rightStep = rightStep.right;
          up = false;
        } else {
          rightStep = rightStep.left;
        }
      }
      return rightStep;
    }

    public void reduce(boolean log) {
      boolean exploded = false;
      boolean split = false;
      //1. explode
      do {
        exploded = false;
        split = false;
        SfNumber toExplode = getDeepestLeftAtLeast4DepthCompact(0); //TODO: Check if ok
        if (toExplode != null) {
          if (log) {
            System.out.println("Exploded: " + toExplode);
          }
          toExplode.explode();
          if (log) {
            System.out.println("After explode: " + this);
          }
          exploded = true;
        } else {
          SfNumber toSplit = getDeepestLeftBiggerThan9Literal();
          if (toSplit != null) {
            if (log) {
              System.out.println("Split: " + toSplit);
            }
            toSplit.split();
            if (log) {
              System.out.println("After split: " + this);
            }
            split = true;
          }
        }
      } while (exploded || split);
    }

    SfNumber getDeepestLeftBiggerThan9Literal() {
      if (isLiteral()) {
        return value > 9L ? this : null;
      }
      SfNumber leftPath = left.getDeepestLeftBiggerThan9Literal();
      if (leftPath != null) {
        return leftPath;
      }
      return right.getDeepestLeftBiggerThan9Literal();
    }

    void split() {
      if (!this.isLiteral()) {
        throw new IllegalStateException("Only literal can be split!");
      }
      Long mod2 = (this.value % 2);
      Long half = (this.value - mod2) / 2;
      this.left = new SfNumber(half, this);
      this.right = new SfNumber(half + mod2, this);
      this.value = null;
    }

    SfNumber getDeepestLeftAtLeast4DepthCompact(long depth) {
      //Depth first search left first
      if (isLiteral()) {
        return null;
      } else if (isCompact()) { //TODO
        return depth < 4 ? null : this;
      }
      SfNumber leftDeepest = left.getDeepestLeftAtLeast4DepthCompact(depth + 1);
      if (leftDeepest != null) {
        return leftDeepest;
      }
      return right.getDeepestLeftAtLeast4DepthCompact(depth + 1);
    }

    void explode() {
      if (!this.isCompact()) {
        throw new IllegalStateException("Can only call explode on compact SfNumbers!");
      }
      SfNumber leftNeighbour = this.getLeftNeighbour();
      SfNumber rightNeighbour = this.getRightNeighbour();
      if (leftNeighbour != null) {
        leftNeighbour.value += this.left.value;
      }
      if (rightNeighbour != null) {
        rightNeighbour.value += this.right.value;
      }
      this.value = 0L;
      this.left = null;
      this.right = null;
    }

    public long getMagnitude() {
      if (this.isLiteral()) {
        return value;
      } else {
        return (3L * left.getMagnitude()) + (2L * right.getMagnitude());
      }
    }
  }

}
