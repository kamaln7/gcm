package gcm.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({SearchTest.class})
public class AllTests {
    public static void main(String[] args) {
        JUnitCore.main("gcm.tests.AllTests");
    }
}
