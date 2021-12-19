package day19;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math.geometry.Vector3D;

public class MainDay19 {

  public static void main(String[] args) throws FileNotFoundException {
    NumberFormat formatter = new DecimalFormat("#0.00000");
    long start = System.currentTimeMillis();
    Game game = readInput();
    System.out.println(solve1(game));
    System.out.printf("Execution time: %s seconds%n",
        formatter.format((System.currentTimeMillis() - start) / 1000d));
    System.out.println(solve2(game));
    System.out.printf("Execution time: %s seconds%n",
        formatter.format((System.currentTimeMillis() - start) / 1000d));
  }

  public static int solve2(Game game) {
    List<Vector3D> placement = game.scanners.stream().map(Scanner::getPlacementTo0).collect(
        Collectors.toList());
    int maxDistance = 0;
    for (int i = 0; i < placement.size(); i++) {
      for (int j = 0; j < placement.size(); j++) {
        int distance = getManhattanDistance(placement.get(i), placement.get(j));
        if (distance > maxDistance) {
          maxDistance = distance;
        }
      }
    }
    return maxDistance;
  }

  public static int getManhattanDistance(Vector3D a, Vector3D b) {
    return (int) (Math.abs(a.getX() - b.getX())
        + Math.abs(a.getY() - b.getY())
        + Math.abs(a.getZ() - b.getZ()));
  }

  public static int solve1(Game game) {
    Set<Vector3D> fullMapFrom0 = new HashSet<>();
    List<Scanner> done = new ArrayList<>();
    done.add(game.scanners.get(0));
    int checkIndex = 0;
    while (done.size() < game.scanners.size()) {
      Scanner current = done.get(checkIndex);
      for (Scanner scanner : game.scanners) {
        if (done.contains(scanner)) {
          continue;
        }
        Transformation transformation = current.getTransformationIfExists(scanner);
        if (transformation != null) {
          scanner.transformationsTo0.addAll(current.transformationsTo0);
          scanner.transformationsTo0.add(transformation);
          done.add(scanner);
        }
      }
      checkIndex++;
    }
    //If everything is ok we should get here;
    done.stream().map(Scanner::getTransformedTo0).forEach(fullMapFrom0::addAll);
    return fullMapFrom0.size();
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day19/input.txt");
    File dependencies = new File(url.getFile());
    java.util.Scanner scanner = new java.util.Scanner(dependencies);
    List<Scanner> scanners = new ArrayList<>();
    while (scanner.hasNextLine()) {
      scanner.nextLine(); //Skip --- scanner 0 --- like line
      String line = scanner.nextLine();
      List<Vector3D> measurements = new ArrayList<>();
      while (!"".equals(line) && line != null) {
        List<Integer> vector = Arrays.stream(line.split(",")).map(Integer::valueOf)
            .collect(Collectors.toList());
        measurements.add(new Vector3D(vector.get(0), vector.get(1), vector.get(2)));
        if (scanner.hasNextLine()) {
          line = scanner.nextLine();
        } else {
          line = null;
        }
      }
      scanners.add(new Scanner(measurements));
    }
    return new Game(scanners);
  }

  public static class Game {

    List<Scanner> scanners;

    public Game(List<Scanner> scanners) {
      this.scanners = scanners;
    }
  }

  public static class Scanner {

    List<Vector3D> givenMeasurements;
    List<Measurement> measurements;

    List<Transformation> transformationsTo0 = new ArrayList<>();

    public Scanner(List<Vector3D> givenMeasurements) {
      this.givenMeasurements = givenMeasurements;
      measurements = givenMeasurements.stream()
          .map(mes -> new Measurement(mes, givenMeasurements))
          .collect(Collectors.toList());
    }

    public Vector3D getPlacementTo0() {
      Vector3D transformed = new Vector3D(0, 0, 0);
      for (int i = transformationsTo0.size() - 1; i >= 0; i--) {
        transformed = transformationsTo0.get(i).apply(transformed);
      }
      return transformed;
    }

    List<Vector3D> getTransformedTo0() {
      return givenMeasurements.stream().map(gm -> {
        Vector3D transformed = gm;
        for (int i = transformationsTo0.size() - 1; i >= 0; i--) {
          transformed = transformationsTo0.get(i).apply(transformed);
        }
        return transformed;
      }).collect(Collectors.toList());
    }

    public Transformation getTransformationIfExists(Scanner scanner) {
      Pair<List<VectorDistance>, List<VectorDistance>> commons = null;
      for (Measurement m : measurements) {
        if (commons != null) {
          break;
        }
        for (Measurement s : scanner.measurements) {
          if (commons != null) {
            break;
          }
          commons = m.has12Common(s);
        }
      }
      if (commons == null) {
        return null;
      }
      return calculateTransformation(commons);
    }

    private Transformation calculateTransformation(
        Pair<List<VectorDistance>, List<VectorDistance>> commons) {
      for (int i = 1; i < 25; i++) {
        Transformation transformation = Transformation.of(i);
        Vector3D move = transformation.matches(commons);
        if (move != null) {
          transformation.setMove(move);
          return transformation;
        }
      }
//      throw new IllegalStateException("No transformation with 12 common?");
      return null;
    }

  }

  public static class Transformation {

    Function<Vector3D, Double> getX;
    Function<Vector3D, Double> getY;
    Function<Vector3D, Double> getZ;

    int num;

    @Setter
    Vector3D move;

    public Transformation(Function<Vector3D, Double> getX,
        Function<Vector3D, Double> getY,
        Function<Vector3D, Double> getZ,
        int num) {
      this.getX = getX;
      this.getY = getY;
      this.getZ = getZ;
      this.num = num;
    }

    public Vector3D apply(Vector3D to) {
      Vector3D rotated = new Vector3D(getX.apply(to), getY.apply(to), getZ.apply(to));
      return move == null ? rotated : rotated.add(move);
    }

    public static Transformation of(int num) {
      switch (num) {
        case 1:
          return new Transformation(v -> v.getX(), v -> v.getY(), v -> v.getZ(), 1);
        case 2:
          return new Transformation(v -> v.getX(), v -> v.getZ(), v -> -v.getY(), 2);
        case 3:
          return new Transformation(v -> v.getX(), v -> -v.getY(), v -> -v.getZ(), 3);
        case 4:
          return new Transformation(v -> v.getX(), v -> -v.getZ(), v -> v.getY(), 4);///
        case 5:
          return new Transformation(v -> -v.getX(), v -> -v.getY(), v -> v.getZ(), 5);
        case 6:
          return new Transformation(v -> -v.getX(), v -> v.getZ(), v -> v.getY(), 6);
        case 7:
          return new Transformation(v -> -v.getX(), v -> v.getY(), v -> -v.getZ(), 7);
        case 8:
          return new Transformation(v -> -v.getX(), v -> -v.getZ(), v -> -v.getY(), 8);///
        case 9:
          return new Transformation(v -> v.getY(), v -> -v.getX(), v -> v.getZ(), 9);
        case 10:
          return new Transformation(v -> v.getY(), v -> v.getZ(), v -> v.getX(), 10);
        case 11:
          return new Transformation(v -> v.getY(), v -> v.getX(), v -> -v.getZ(), 11);
        case 12:
          return new Transformation(v -> v.getY(), v -> -v.getZ(), v -> -v.getX(), 12);///
        case 13:
          return new Transformation(v -> -v.getY(), v -> v.getX(), v -> v.getZ(), 13);
        case 14:
          return new Transformation(v -> -v.getY(), v -> -v.getZ(), v -> v.getX(), 14);
        case 15:
          return new Transformation(v -> -v.getY(), v -> -v.getX(), v -> -v.getZ(), 15);
        case 16:
          return new Transformation(v -> -v.getY(), v -> v.getZ(), v -> -v.getX(), 16);///
        case 17:
          return new Transformation(v -> v.getZ(), v -> v.getY(), v -> -v.getX(), 17);
        case 18:
          return new Transformation(v -> v.getZ(), v -> v.getX(), v -> v.getY(), 18);
        case 19:
          return new Transformation(v -> v.getZ(), v -> -v.getY(), v -> v.getX(), 19);
        case 20:
          return new Transformation(v -> v.getZ(), v -> -v.getX(), v -> -v.getY(), 20);///
        case 21:
          return new Transformation(v -> -v.getZ(), v -> v.getY(), v -> v.getX(), 21);
        case 22:
          return new Transformation(v -> -v.getZ(), v -> -v.getX(), v -> v.getY(), 22);
        case 23:
          return new Transformation(v -> -v.getZ(), v -> -v.getY(), v -> -v.getX(), 23);
        case 24:
          return new Transformation(v -> -v.getZ(), v -> v.getX(), v -> -v.getY(), 24);
      }
      throw new IllegalStateException("Transformation num should be between 1 and 24!");
    }

    public Vector3D matches(Pair<List<VectorDistance>, List<VectorDistance>> commons) {
      Vector3D newOrigin = this.apply(commons.getRight().get(0).from);
      List<VectorDistance> transformed = commons.getRight().stream()
          .map(vd -> new VectorDistance(newOrigin, this.apply(vd.to), vd.distance))
          .collect(Collectors.toList());
      int errorLimit = transformed.size() - 11;
      //If there are common distances, it will yield error, since it checks with all
      Map<Integer, Long> grouped = commons.getLeft().stream().map(VectorDistance::getDistance)
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      for (Long val : grouped.values()) {
        if (val > 1) {
          //For 2 it will fail once on both runs, for 3 it will fail 2 out of 3 on all 3 runs
          errorLimit += val * (val - 1);
        }
      }
      Vector3D dist = commons.getLeft().get(0).from.add(transformed.get(0).from.negate());
      int errCount = 0;
      for (int i = 0; i < commons.getLeft().size(); i++) {
        VectorDistance left = commons.getLeft().get(i);
        for (int j = 1; j < transformed.size(); j++) {
          VectorDistance right = transformed.get(j);
          if (left.distance.equals(right.distance)) {
            Vector3D currentDist = left.to.add(right.to.negate());
            if (!dist.equals(currentDist)) {
              errCount++;
            }
          }
          if (errCount > errorLimit) {
            return null;
          }
        }
      }
      //Transformation is good
      return dist;
    }
  }

  public static class Measurement {

    Vector3D measurement;

    List<VectorDistance> relative;

    public Measurement(Vector3D measurement, List<Vector3D> all) {
      this.measurement = measurement;
      relative = new ArrayList<>();
      for (Vector3D v : all) {
        if (v != measurement) {
          relative.add(new VectorDistance(measurement, v));
        }
      }
    }

    public Pair<List<VectorDistance>, List<VectorDistance>> has12Common(Measurement m2) {
      Set<Integer> m2Dists = m2.relative.stream().map(VectorDistance::getDistance).collect(
          Collectors.toSet());
      List<Integer> commonDists = relative.stream().map(VectorDistance::getDistance)
          .filter(m2Dists::contains).collect(Collectors.toList());
      if (commonDists.size() < 11) {
        return null;
      }
      List<VectorDistance> ofThis = relative.stream()
          .filter(vd -> commonDists.contains(vd.distance))
          .collect(Collectors.toList());
      List<VectorDistance> ofOther = m2.relative.stream()
          .filter(vd -> commonDists.contains(vd.distance))
          .collect(Collectors.toList());
      return Pair.of(ofThis, ofOther);
    }
  }

  public static class VectorDistance {

    Vector3D from;
    @Getter
    Vector3D to;
    @Getter
    Integer distance;

    public VectorDistance(Vector3D from, Vector3D to) {
      this.from = from;
      this.to = to;
      distance = (int) to.add(from.negate()).getNorm1();
    }

    public VectorDistance(Vector3D from, Vector3D to, Integer distance) {
      this.from = from;
      this.to = to;
      this.distance = distance;
    }
  }
}
