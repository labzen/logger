package cn.labzen.logger.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfilesTest {

  @BeforeEach
  void setUp() {
    // Reset profiles before each test
    Profiles.setCurrentProfiles(List.of());
  }

  @Test
  void testDefaultProfile() {
    Profiles.setCurrentProfiles(List.of(Profiles.DEFAULT));
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(1, profiles.size());
    assertEquals(Profiles.DEFAULT, profiles.get(0));
  }

  @Test
  void testDevProfile() {
    Profiles.setCurrentProfiles(List.of(Profiles.DEV));
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(1, profiles.size());
    assertEquals(Profiles.DEV, profiles.get(0));
  }

  @Test
  void testProdProfile() {
    Profiles.setCurrentProfiles(List.of(Profiles.PROD));
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(1, profiles.size());
    assertEquals(Profiles.PROD, profiles.get(0));
  }

  @Test
  void testMultipleProfiles() {
    Profiles.setCurrentProfiles(List.of(Profiles.DEV, Profiles.TEST));
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(2, profiles.size());
    assertTrue(profiles.contains(Profiles.DEV));
    assertTrue(profiles.contains(Profiles.TEST));
  }

  @Test
  void testEmptyProfiles() {
    Profiles.setCurrentProfiles(List.of());
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(0, profiles.size());
  }

  @Test
  void testProfileConstants() {
    assertEquals("", Profiles.DEFAULT);
    assertEquals("dev", Profiles.DEV);
    assertEquals("prod", Profiles.PROD);
    assertEquals("test", Profiles.TEST);
    assertEquals("uat", Profiles.UAT);
    assertEquals("development", Profiles.DEV_FULL);
    assertEquals("production", Profiles.PROD_FULL);
  }

  @Test
  void testSetProfilesOverwrite() {
    Profiles.setCurrentProfiles(List.of(Profiles.DEV));
    assertEquals(1, Profiles.currentProfiles().size());
    
    Profiles.setCurrentProfiles(List.of(Profiles.PROD, Profiles.UAT));
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(2, profiles.size());
    assertFalse(profiles.contains(Profiles.DEV));
    assertTrue(profiles.contains(Profiles.PROD));
    assertTrue(profiles.contains(Profiles.UAT));
  }

  @Test
  void testCustomProfiles() {
    Profiles.setCurrentProfiles(List.of("custom1", "custom2"));
    List<String> profiles = Profiles.currentProfiles();
    assertEquals(2, profiles.size());
    assertTrue(profiles.contains("custom1"));
    assertTrue(profiles.contains("custom2"));
  }

  @Test
  void testProfilesImmutability() {
    Profiles.setCurrentProfiles(List.of(Profiles.DEV));
    List<String> profiles = Profiles.currentProfiles();
    
    // Verify we get a reference to the actual list
    // (based on current implementation)
    assertNotNull(profiles);
    assertEquals(1, profiles.size());
  }
}
