package jmri.jmrix.sprog.sprogslotmon;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jmri.InstanceManager;
import jmri.jmrix.sprog.SprogSystemConnectionMemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing action to create and register a SprogSlotMonFrame object.
 *
 * @author Bob Jacobsen Copyright (C) 2001 
 * @author      Andrew Crosland (C) 2006 ported to SPROG
 */
public class SprogSlotMonAction extends AbstractAction {

    private SprogSystemConnectionMemo _memo = null;

    public SprogSlotMonAction(String s, SprogSystemConnectionMemo memo) {
        super(s);
        _memo = memo;
    }

    public SprogSlotMonAction(SprogSystemConnectionMemo memo) {
        this(Bundle.getMessage("SprogSlotMonitorTitle"), memo);
    }

    public SprogSlotMonAction() {
        super(Bundle.getMessage("SprogSlotMonitorTitle"));
        _memo = InstanceManager.getNullableDefault(SprogSystemConnectionMemo.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (_memo != null) {
            if (_memo.getCommandStation() == null) {
                // create SlotManager if it doesn't exist
                _memo.configureCommandStation(_memo.getNumSlots());
            }
            
            SprogSlotMonFrame f = new SprogSlotMonFrame(_memo);
            f.setVisible(true);
        } else {
            log.warn("Trying to start SPROG Slot monitor for incompatible connection type");
        }
    }

    // initialize logging
    private final static Logger log = LoggerFactory.getLogger(SprogSlotMonAction.class);

}
