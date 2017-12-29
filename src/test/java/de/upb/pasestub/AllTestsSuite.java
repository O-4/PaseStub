package de.upb.pasestub;
import org.junit.runners.*;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        de.upb.pasestub.PaseInstanceTest.class
        //,de.upb.pasestub.DeployTest.class // Uncomment if a pase server is running on port 5000
     })
public final class AllTestsSuite {} 