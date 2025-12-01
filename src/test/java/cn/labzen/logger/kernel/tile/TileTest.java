package cn.labzen.logger.kernel.tile;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

  @Test
  void testFormatterOfDateTile() {
    FormatterOfDateTile tile = new FormatterOfDateTile("yyyy-MM-dd HH:mm:ss");
    assertEquals("2023-12-25 10:30:45", tile.convert(LocalDateTime.of(2023, 12, 25, 10, 30, 45)));

    FormatterOfDateTile tile2 = new FormatterOfDateTile("yyyy/MM/dd");
    assertEquals("2023/12/25", tile2.convert(LocalDate.of(2023, 12, 25)));

    FormatterOfDateTile tile3 = new FormatterOfDateTile("HH:mm:ss");
    assertEquals("10:30:45", tile3.convert(LocalTime.of(10, 30, 45)));
  }

  @Test
  void testFormatterOfNumberTile() {
    FormatterOfNumberTile tile = new FormatterOfNumberTile("0.00");
    assertEquals("1.23", tile.convert(1.23));

    FormatterOfNumberTile tile2 = new FormatterOfNumberTile("#.##");
    assertEquals("1.23", tile2.convert(1.23));

    FormatterOfNumberTile tile3 = new FormatterOfNumberTile("0000");
    assertEquals("0010", tile3.convert(10));
  }

  @Test
  void testFunctionalWhetherTile() {
    FunctionalWhetherTile tile = new FunctionalWhetherTile("yes", "no");
    assertEquals("yes", tile.convert(true));
    assertEquals("no", tile.convert(false));
  }

  @Test
  void testFunctionalWidthControlTile() {
    // Test with min width only
    FunctionalWidthControlTile tile = new FunctionalWidthControlTile(5, null); // min 5, no max
    assertEquals("12345", tile.convert("12345")); // exact length

    FunctionalWidthControlTile tile2 = new FunctionalWidthControlTile(10, null); // min 10, no max
    assertEquals("123       ", tile2.convert("123")); // pad right to 10 (7 spaces)

    // Test with max width only
    FunctionalWidthControlTile tile3 = new FunctionalWidthControlTile(null, 3); // no min, max 3
    assertEquals("123", tile3.convert("1234567890")); // truncate to 3

    // Test with both min and max
    FunctionalWidthControlTile tile4 = new FunctionalWidthControlTile(3, 5); // min 3, max 5
    assertEquals("12345", tile4.convert("1234567890")); // truncate to 5
    assertEquals("12 ", tile4.convert("12")); // no padding since length < min but min is 3
  }

  @Test
  void testFunctionalWrapTile() {
    FunctionalWrapTile tile = new FunctionalWrapTile("()");
    assertEquals("(text)", tile.convert("text"));

    FunctionalWrapTile tile2 = new FunctionalWrapTile("[]");
    assertEquals("[text]", tile2.convert("text"));
  }

  @Test
  void testDefaultTile() {
    DefaultTile tile = new DefaultTile(0);
    assertEquals("value", tile.convert("value"));
  }

  @Test
  void testNamedTile() {
    NamedTile tile = new NamedTile("key");
    assertNotNull(tile);
  }
}
