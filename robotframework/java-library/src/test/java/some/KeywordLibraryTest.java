package some;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.robotframework.RobotFramework;

public class KeywordLibraryTest {

  @Test
  public void testKeywords() {
    // set console encoding
    System.setProperty("python.console.encoding", StandardCharsets.UTF_8.name());

    List<String> arguments = new LinkedList<>();
    arguments.add("run");
    arguments.add("--consolecolors");
    arguments.add("off");
    arguments.add("--outputdir");
    arguments.add("./target/robot");
    arguments.add("./src/test/resources");

    int exitCode = RobotFramework.run(arguments.toArray(new String[arguments.size()]));
    assertThat(exitCode, is(0));
  }
}
