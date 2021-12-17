package day16;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MainDay16 {

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(solve1(readInput()));
    System.out.println(solve2(readInput()));
  }

  public static int solve1(Transmission transmission) {
    return transmission.packet.getVersionNumberSum();
  }

  public static long solve2(Transmission transmission) {
    return transmission.packet.getValue();
  }

  public static Transmission readInput() throws FileNotFoundException {
    URL url = ClassLoader.getSystemResource("day16/input.txt");
    File dependencies = new File(url.getFile());
    Scanner scanner = new Scanner(dependencies);
    String line = scanner.nextLine();
    String data = Arrays.stream(line.split(""))
        .map(MainDay16::hexToBin)
        .collect(Collectors.joining());
    return new Transmission(data);
  }

  public static class Transmission {

    String data;
    Packet packet;

    public Transmission(String data) {
      this.data = data;
      this.packet = Packet.of(data);
      this.packet.parse();
    }
  }

  public static class Packet {

    String data;
    Integer version;
    Integer packetTypeCode;
    PacketType packetType;
    Long literalValue;

    Integer lengthTypeId;
    Integer lengthInfo;

    List<Packet> subPackets;

    Long value;

    public static Packet of(String data) {
      Packet packet = new Packet();
      packet.data = data;
      packet.subPackets = new ArrayList<>();
      return packet;
    }

    public int parse() {
      version = Integer.parseInt(data.substring(0, 3), 2);
      packetTypeCode = Integer.parseInt(data.substring(3, 6), 2);
      packetType = PacketType.of(packetTypeCode);

      int cursor = 6;
      if (packetType == PacketType.literal) {
        //Parse the 5 groups
        StringBuilder sb = new StringBuilder();
        while (data.charAt(cursor) == '1') {
          sb.append(data, cursor + 1, cursor + 1 + 4);
          cursor += 1 + 4;
        }
        //Now it is 0 -> last batch
        if (data.charAt(cursor) != '0') {
          throw new IllegalStateException("Literal packet number leading bit is not 0!");
        }
        sb.append(data, cursor + 1, cursor + 1 + 4);
        literalValue = Long.parseLong(sb.toString(), 2);
        cursor += 1 + 4;
      } else {
        lengthTypeId = Integer.parseInt(data.substring(cursor, ++cursor));
        if (lengthTypeId.equals(0)) {
          lengthInfo = Integer.parseInt(data.substring(cursor, cursor + 15), 2);
          cursor += 15;
          int upperLimit = cursor + lengthInfo - 1;
          while (cursor < upperLimit) {
            Packet packet = Packet.of(data.substring(cursor));
            int move = packet.parse();
            cursor += move;
            subPackets.add(packet);
          }
        } else {
          lengthInfo = Integer.parseInt(data.substring(cursor, cursor + 11), 2);
          cursor += 11;
          while (subPackets.size() < lengthInfo) {
            Packet packet = Packet.of(data.substring(cursor));
            int move = packet.parse();
            cursor += move;
            subPackets.add(packet);
          }
        }
      }
      return cursor;
    }

    public int getVersionNumberSum() {
      if (subPackets == null || subPackets.isEmpty()) {
        return version;
      }
      return subPackets.stream().mapToInt(Packet::getVersionNumberSum).sum() + version;
    }

    public long getValue() {
      switch (packetType) {
        case literal:
          return literalValue;
        case sum:
          return subPackets.stream().mapToLong(Packet::getValue).sum();
        case product:
          return subPackets.stream().map(Packet::getValue).reduce(1L, (a, b) -> a * b);
        case minimum:
          return subPackets.stream().mapToLong(Packet::getValue).min().getAsLong();
        case maximum:
          return subPackets.stream().mapToLong(Packet::getValue).max().getAsLong();
        case greater:
          return subPackets.get(0).getValue() > subPackets.get(1).getValue() ? 1L : 0L;
        case less:
          return subPackets.get(0).getValue() < subPackets.get(1).getValue() ? 1L : 0L;
        case equal:
          return subPackets.get(0).getValue() == subPackets.get(1).getValue() ? 1L : 0L;
      }
      throw new IllegalStateException("This shouldn't happen!");
    }
  }

  @RequiredArgsConstructor
  public enum PacketType {
    literal(4),
    sum(0),
    product(1),
    minimum(2),
    maximum(3),
    greater(5),
    less(6),
    equal(7);

    @Getter
    final int code;

    static final Map<Integer, PacketType> lookup = Arrays.stream(PacketType.values())
        .collect(Collectors.toMap(PacketType::getCode, Function.identity()));

    public static PacketType of(int num) {
      return lookup.get(num);
    }
  }

  private static String hexToBin(String hex) {
    hex = hex.replaceAll("0", "0000");
    hex = hex.replaceAll("1", "0001");
    hex = hex.replaceAll("2", "0010");
    hex = hex.replaceAll("3", "0011");
    hex = hex.replaceAll("4", "0100");
    hex = hex.replaceAll("5", "0101");
    hex = hex.replaceAll("6", "0110");
    hex = hex.replaceAll("7", "0111");
    hex = hex.replaceAll("8", "1000");
    hex = hex.replaceAll("9", "1001");
    hex = hex.replaceAll("A", "1010");
    hex = hex.replaceAll("B", "1011");
    hex = hex.replaceAll("C", "1100");
    hex = hex.replaceAll("D", "1101");
    hex = hex.replaceAll("E", "1110");
    hex = hex.replaceAll("F", "1111");
    return hex;
  }
}
