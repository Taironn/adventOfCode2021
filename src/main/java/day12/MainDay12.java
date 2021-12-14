package day12;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * @author delikristof.demeter
 */
public class MainDay12 {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(readInput().calculate());
    System.out.println(readInput().calculate2());
  }

  public static Game readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day12/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    Map<Cave, List<Cave>> map = new HashMap<>();
    while (scanner.hasNextLine()) {
      String[] path = scanner.nextLine().split("-");
      if (!map.containsKey(new Cave(path[0]))) {
        List<Cave> list = new ArrayList<>();
        list.add(new Cave(path[1]));
        map.put(new Cave(path[0]), list);
      } else {
        map.get(new Cave(path[0])).add(new Cave(path[1]));
      }
      //Other direction
      if (!map.containsKey(new Cave(path[1]))) {
        List<Cave> list = new ArrayList<>();
        list.add(new Cave(path[0]));
        map.put(new Cave(path[1]), list);
      } else {
        map.get(new Cave(path[1])).add(new Cave(path[0]));
      }
    }
    return new Game(map);
  }

  @Data
  public static class Game {

    Map<Cave, List<Cave>> map;
    TreeNode fullMap;

    public Game(Map<Cave, List<Cave>> map) {
      this.map = map;
    }

    public long calculate() {
      fullMap = new TreeNode(new Cave("start"), Set.of(), false);
      fullMap.fillMap(map);
      return fullMap.countLeaves();
    }

    public long calculate2() {
      fullMap = new TreeNode(new Cave("start"), Set.of(new Cave("start")), false);
      fullMap.fillMap2(map);
      return fullMap.countLeaves();
    }
  }

  public static class Cave {

    final String name;
    final boolean big;

    public Cave(String name) {
      this.name = name;
      big = (name.toUpperCase().equals(name));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Cave cave = (Cave) o;
      return this.name.equals(cave.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }
  }

  public static class TreeNode {

    Cave currentCave;
    Set<Cave> visited;
    List<TreeNode> nextDestinations;
    boolean visitedSmallTwice = false;

    public TreeNode(Cave currentCave, Set<Cave> visited, boolean visitedSmallTwice) {
      this.currentCave = currentCave;
      this.visited = visited;
      this.visitedSmallTwice = visitedSmallTwice;
    }

    public void fillMap2(Map<Cave, List<Cave>> map) {
      if (!currentCave.name.equals("end")) {
        nextDestinations = map.get(currentCave).stream()
            .filter(cave -> !cave.name.equals("start"))
            .filter(cave -> cave.big || !visited.contains(cave) || !visitedSmallTwice)
            .map(cave -> {
              Set<Cave> caves = new HashSet<>(visited);
              boolean twice = !cave.big && visited.contains(cave);
              caves.add(cave);
              //Check for double visit
              return new TreeNode(cave, caves, twice || visitedSmallTwice);
            }).collect(Collectors.toList());
        nextDestinations.forEach(dest -> dest.fillMap2(map));
      }
    }

    public void fillMap(Map<Cave, List<Cave>> map) {
      if (!currentCave.name.equals("end") && map.containsKey(currentCave)) {
        nextDestinations = map.get(currentCave).stream()
            .filter(cave -> cave.big || !visited.contains(cave))
            .map(cave -> {
              Set<Cave> caves = new HashSet<>(visited);
              caves.add(currentCave);
              return new TreeNode(cave, caves, false);
            }).collect(Collectors.toList());
        nextDestinations.forEach(dest -> dest.fillMap(map));
      }
    }

    public long countLeaves() {
      if (nextDestinations != null && !nextDestinations.isEmpty()) {
        return nextDestinations.stream().mapToLong(TreeNode::countLeaves).sum();
      } else if (currentCave.name.equals("end")) {
        return 1L;
      } else {
        return 0L;
      }
    }
  }
}
