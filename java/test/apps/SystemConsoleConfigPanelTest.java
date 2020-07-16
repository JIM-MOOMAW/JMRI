package apps;

import jmri.swing.PreferencesPanelTestBase;
import jmri.util.JUnitUtil;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class SystemConsoleConfigPanelTest extends PreferencesPanelTestBase {

    @Override
    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
        jmri.InstanceManager.setDefault(apps.systemconsole.SystemConsolePreferencesManager.class,new apps.systemconsole.SystemConsolePreferencesManager());
        prefsPanel = new SystemConsoleConfigPanel();
    }

    @Override
    @Test
    public void isPersistant() {
        assertThat(prefsPanel.isPersistant()).isTrue();
    }

    @Override
    @Test
    public void getPreferencesTooltip() {
        // should this actually return null?
        assertThat(prefsPanel.getPreferencesTooltip()).isNull();
    }
    // private final static Logger log = LoggerFactory.getLogger(SystemConsoleConfigPanelTest.class);

}
