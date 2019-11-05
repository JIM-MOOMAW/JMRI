package jmri.jmrit.symbolicprog;

import java.awt.GraphicsEnvironment;
import jmri.jmrit.roster.RosterEntry;
import jmri.jmrit.symbolicprog.tabbedframe.PaneProgFrame;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class PrintActionTest {

    @Test
    public void testCTor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        jmri.Programmer p = jmri.InstanceManager.getDefault(jmri.GlobalProgrammerManager.class).getGlobalProgrammer();
        PaneProgFrame pFrame = new PaneProgFrame(null, new RosterEntry(),
                "test frame", "programmers/Basic.xml",
                p, false) {
            // dummy implementations
            @Override
            protected javax.swing.JPanel getModePane() {
                return null;
            }
        };
        PrintAction t = new PrintAction("Test Action", pFrame, true);
        Assert.assertNotNull("exists", t);
        JUnitUtil.dispose(pFrame);
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initRosterConfigManager();
        JUnitUtil.initDebugProgrammerManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.clearShutDownManager(); // testing of PaneProgFrame ShutDown items down in PaneProgFrame tests
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(PrintActionTest.class.getName());
}
