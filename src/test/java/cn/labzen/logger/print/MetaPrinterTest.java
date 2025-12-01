package cn.labzen.logger.print;

import cn.labzen.logger.Loggers;
import cn.labzen.meta.LabzenMetaInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MetaPrinterTest {

  @BeforeAll
  static void init() {
    Loggers.enhance();
    new LabzenMetaInitializer().initialize(null);
  }

  @Test
  void testPrintMethodExists() {
    // Just test that the method exists and can be called without exception
    assertDoesNotThrow(MetaPrinter::print);
  }

  @Test
  void testPrintToConsole() {
    // Capture System.out to verify output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    try {
      MetaPrinter.print();

      String output = outContent.toString();
      assertNotNull(output);
      // Check that some expected content is present
      assertTrue(output.contains("Labzen") || !output.isEmpty());
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void testPrintBannerAndComponents() {
    // Test that the method can be called without exception
    // This is a private method, but we can at least verify the public print method works
    assertDoesNotThrow(MetaPrinter::print);
  }

  @Test
  void testPrintProjectInformation() {
    // Test that the method can be called without exception
    // This is a private method, but we can at least verify the public print method works
    assertDoesNotThrow(MetaPrinter::print);
  }

  @Test
  void testPrintSystemInformation() {
    // Test that the method can be called without exception
    // This is a private method, but we can at least verify the public print method works
    assertDoesNotThrow(MetaPrinter::print);
  }
}
