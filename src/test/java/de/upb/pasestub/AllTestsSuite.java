package de.upb.pasestub;
import org.junit.runners.*;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        de.upb.pasestub.DeployTest.class,  // Uncomment if a pase server is running on port 5000
        de.upb.pasestub.PaseInstanceTest.class,
        de.upb.pasestub.PaseImInstanceTest.class
     })
public final class AllTestsSuite {} 