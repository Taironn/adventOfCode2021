package day5;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay5 {

  public static void main(String[] args) throws FileNotFoundException {
    List<Line> input = readInput();
    System.out.println(solve1(input, false));
    System.out.println(solve1(input, true));
  }

  private static long solve1(List<Line> input, boolean includeDiagonal) {
    int[][] board = new int[1000][1000];
    for (Line line : input) {
      if (line.isStraight()) {
        Line line1 = line.order();
        if (line1.sourceX == line1.destX) {
          for (int i = line1.sourceY; i <= line1.destY; i++) {
            board[line1.sourceX][i] += 1;
          }
        } else {
          for (int i = line1.sourceX; i <= line1.destX; i++) {
            board[i][line1.sourceY] += 1;
          }
        }
      } else if (includeDiagonal) {
        Line line1 = line.order();
        boolean yArising = line1.destY >= line1.sourceY;
        int step = yArising ? 1 : -1;
        int startX = line1.sourceX;
        int startY = line1.sourceY;
        for (int i = 0; i < line1.destX - line1.sourceX + 1; i++){
          board[startX +i][startY + (step * i)] += 1;
        }
      }
    }
    return Arrays.stream(board).flatMapToInt(Arrays::stream).filter(value -> value > 1).count();
  }

  private static List<Line> readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day5/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    List<Line> input = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String[] split = scanner.nextLine().split(" -> ");
      input.add(new Line(
          Integer.parseInt(split[0].split(",")[0]),
          Integer.parseInt(split[0].split(",")[1]),
          Integer.parseInt(split[1].split(",")[0]),
          Integer.parseInt(split[1].split(",")[1])
      ));
    }
    return input;
  }

  @Data
  @AllArgsConstructor
  public static class Line {

    private int sourceX;
    private int sourceY;
    private int destX;
    private int destY;

    public boolean isStraight() {
      return sourceX == destX || sourceY == destY;
    }

    public Line order() {
      if (sourceX == destX) {
        return new Line(sourceX, sourceY <= destY ? sourceY : destY, destX,
            sourceY <= destY ? destY : sourceY);
      } else if (sourceY == destY) {
        return new Line(sourceX <= destX ? sourceX : destX, sourceY,
            sourceX <= destX ? destX : sourceX, destY);
      } else {
        boolean swap = sourceX >= destX;
        return swap ? new Line(destX, destY, sourceX, sourceY) : this;
      }
    }
  }
}
