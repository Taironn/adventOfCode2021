package day17;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.text.Position;

public class MainDay17 {

  private static final int xMin = 137;
  private static final int xMax = 171;
  private static final int yMin = -98;
  private static final int yMax = -73;

  public static void main(String[] args) {
    //    System.out.println(solve(true));
    System.out.println(solve(false));
  }

  private static int solve(boolean first) {
    int yMax = 0;
    int yCount = 0;
    for (int y = -1000; y < 1000; y++) {
      for (int x = 0; x < 172; x++) {
        Integer newMax = maxHeightIfHit(x, y);
        if (newMax != null) {
          yCount++;
        }
        if (newMax != null && newMax > yMax) {
          yMax = newMax;
        }
      }
      if (first) System.out.println("Iteration " + (y + 1) + " done. Max y: " + yMax);
      else System.out.println("Iteration " + (y) + " done. Count: " + yCount);
    }
    return first ? yMax : yCount;
  }

  private static Integer maxHeightIfHit(int xVel, int yVel) {
    Probe probe = Probe.of(Place.of(0, 0), xVel, yVel);
    int yMax = 0;
    while (!(probe.position.x > 171) && !(probe.position.y < -98)) {
      probe.iterate();
      if (probe.position.y > yMax) {
        yMax = probe.position.y;
      }
      if (probe.position.hit()) {
        return yMax;
      }
    }
    return null;
  }

  @Data
  @AllArgsConstructor
  public static class Probe {
    Place position;
    int xVel;
    int yVel;

    public static Probe of(Place start, int xVel, int yVel) {
      return new Probe(start, xVel, yVel);
    }

    public static Probe of(Probe b) {
      return new Probe(Place.of(b.position.x, b.position.y), b.xVel, b.yVel);
    }

    public void iterate() {
      position.x += xVel;
      position.y += yVel;
      if (xVel != 0) {
        if (xVel > 0) {
          xVel--;
        } else {
          xVel++;
        }
      }
      yVel--;
    }
  }

  @Data
  @AllArgsConstructor
  public static class Place {
    int x, y;

    public static Place of(int x, int y) {
      return new Place(x, y);
    }

    public boolean hit() {
      return x >= xMin && x <= xMax && y >= yMin && y <= yMax;
    }
  }
}
