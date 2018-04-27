package org.apache.hadoop.hbase.filter;

import java.util.regex.Pattern;

import dk.brics.automaton.RegExp;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtendedRegexStringComparatorTest {

  @Test
  public void testSerialization() throws Exception {
    // Default engine is the Java engine
    ExtendedRegexStringComparator a = new ExtendedRegexStringComparator("a|b");
    ExtendedRegexStringComparator b = ExtendedRegexStringComparator.parseFrom(a.toByteArray());
    assertTrue(a.areSerializedFieldsEqual(b));
    assertTrue(b.getEngine() instanceof ExtendedRegexStringComparator.JavaRegexEngine);

    // joni engine
    a = new ExtendedRegexStringComparator("a|b", ExtendedRegexStringComparator.EngineType.JONI);
    b = ExtendedRegexStringComparator.parseFrom(a.toByteArray());
    assertTrue(a.areSerializedFieldsEqual(b));
    assertTrue(b.getEngine() instanceof ExtendedRegexStringComparator.JoniRegexEngine);

    // re2j engine
    a = new ExtendedRegexStringComparator("a|b", ExtendedRegexStringComparator.EngineType.RE2J);
    b = ExtendedRegexStringComparator.parseFrom(a.toByteArray());
    assertTrue(a.areSerializedFieldsEqual(b));
    assertTrue(b.getEngine() instanceof ExtendedRegexStringComparator.Re2JRegexEngine);

    // brics engine
    a = new ExtendedRegexStringComparator("a|b", ExtendedRegexStringComparator.EngineType.BRICS);
    b = ExtendedRegexStringComparator.parseFrom(a.toByteArray());
    assertTrue(a.areSerializedFieldsEqual(b));
    assertTrue(b.getEngine() instanceof ExtendedRegexStringComparator.BricsRegexEngine);

    // fast brics engine
    a = new ExtendedRegexStringComparator("a|b", ExtendedRegexStringComparator.EngineType.FAST_BRICS);
    b = ExtendedRegexStringComparator.parseFrom(a.toByteArray());
    assertTrue(a.areSerializedFieldsEqual(b));
    assertTrue(b.getEngine() instanceof ExtendedRegexStringComparator.FastBricsRegexEngine);

    // fastest brics engine
    a = new ExtendedRegexStringComparator("a|b", ExtendedRegexStringComparator.EngineType.FASTEST_BRICS);
    b = ExtendedRegexStringComparator.parseFrom(a.toByteArray());
    assertTrue(a.areSerializedFieldsEqual(b));
    assertTrue(b.getEngine() instanceof ExtendedRegexStringComparator.FastBricsRegexEngine);

  }


  @Test
  public void testJavaEngine() throws Exception {
    for (TestCase t : TEST_CASES) {
      boolean result =
        new ExtendedRegexStringComparator(t.regex, Pattern.DOTALL, ExtendedRegexStringComparator.EngineType.JAVA)
          .compareTo(Bytes.toBytes(t.haystack)) == 0;
      assertEquals(t.expected, result, "Regex '" + t.regex + "' failed test '" + t.haystack + "'");
    }
  }

  @Test
  public void testJoniEngine() throws Exception {
    for (TestCase t : TEST_CASES) {
      boolean result =
        new ExtendedRegexStringComparator(t.regex, Pattern.DOTALL, ExtendedRegexStringComparator.EngineType.JONI)
          .compareTo(Bytes.toBytes(t.haystack)) == 0;
      assertEquals(t.expected, result, "Regex '" + t.regex + "' failed test '" + t.haystack + "'");
    }
  }

  @Test
  public void testRe2JEngine() throws Exception {
    for (TestCase t : TEST_CASES) {
      boolean result = new ExtendedRegexStringComparator(t.regex, com.google.re2j.Pattern.DOTALL,
        ExtendedRegexStringComparator.EngineType.RE2J)
                         .compareTo(Bytes.toBytes(t.haystack)) == 0;
      assertEquals(t.expected, result, "Regex '" + t.regex + "' failed test '" + t.haystack + "'");
    }
  }

  @Test
  public void testBricsEngine() throws Exception {
    for (TestCase t : TEST_CASES) {
      boolean result =
        new ExtendedRegexStringComparator(t.regex, RegExp.NONE, ExtendedRegexStringComparator.EngineType.BRICS)
          .compareTo(Bytes.toBytes(t.haystack)) == 0;
      assertEquals(t.expected, result, "Regex '" + t.regex + "' failed test '" + t.haystack + "'");
    }
  }

  @Test
  public void testFastBricsEngine() throws Exception {
    for (TestCase t : TEST_CASES) {
      boolean result =
        new ExtendedRegexStringComparator(t.regex, RegExp.NONE, ExtendedRegexStringComparator.EngineType.FAST_BRICS)
          .compareTo(Bytes.toBytes(t.haystack)) == 0;
      assertEquals(t.expected, result, "Regex '" + t.regex + "' failed test '" + t.haystack + "'");
    }
  }

  @Test
  public void testFastestBricsEngine() throws Exception {
    for (TestCase t : TEST_CASES) {
      boolean result =
        new ExtendedRegexStringComparator(t.regex, RegExp.NONE, ExtendedRegexStringComparator.EngineType.FASTEST_BRICS)
          .compareTo(Bytes.toBytes(t.haystack)) == 0;
      assertEquals(t.expected, result, "Regex '" + t.regex + "' failed test '" + t.haystack + "'");
    }
  }

  private static class TestCase {

    String regex;
    String haystack;
    boolean expected;

    public TestCase(String regex, String haystack, boolean expected) {
      this.regex = regex;
      this.haystack = haystack;
      this.expected = expected;
    }
  }

  // These are a subset of the regex tests from OpenJDK 7
  private static final TestCase[] TEST_CASES = {
    new TestCase("a|b", "a", true),
    new TestCase("a|b", "b", true),
    new TestCase("a|b", "z", false),
    new TestCase("a|b|cd", "cd", true),
    new TestCase("z(a|ac)b", "zacb", true),
    new TestCase("[abc]+", "ababab", true),
    new TestCase("[abc]+", "defg", false),
    new TestCase("[^abc]+", "ababab", false),
    new TestCase("[abc^b]", "b", true),
    new TestCase("[0-9]+:::LS:[0-9_]+::(1234|5678)::", "12:::LS:34::1234::", true),
    // new TestCase("1", "12", true)
    new TestCase("1.*", "12", true)
  };
}
