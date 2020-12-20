package jmri.jmrit.logixng.expressions.swing;

import java.awt.GraphicsEnvironment;

import javax.swing.JPanel;

import jmri.InstanceManager;
import jmri.TurnoutManager;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.actions.IfThenElse;
import jmri.jmrit.logixng.expressions.ExpressionTurnout;
import jmri.jmrit.logixng.swing.SwingConfiguratorInterfaceTestBase;
import jmri.util.JUnitUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.operators.*;

/**
 * Test ExpressionTurnoutSwing
 * 
 * @author Daniel Bergqvist 2018
 */
public class ExpressionTurnoutSwingTest extends SwingConfiguratorInterfaceTestBase {

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        ExpressionTurnoutSwing t = new ExpressionTurnoutSwing();
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testPanel() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        ExpressionTurnoutSwing t = new ExpressionTurnoutSwing();
        JPanel panel = t.getConfigPanel(new JPanel());
        Assert.assertNotNull("exists",panel);
    }
    
    @Test
    public void testCreatePanel() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        Assert.assertTrue("panel is not null",
            null != new ExpressionTurnoutSwing().getConfigPanel(new JPanel()));
        Assert.assertTrue("panel is not null",
            null != new ExpressionTurnoutSwing().getConfigPanel(new ExpressionTurnout("IQDE1", null), new JPanel()));
    }
    
    @Test
    public void testDialogUseExistingTurnout() throws SocketAlreadyConnectedException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        InstanceManager.getDefault(TurnoutManager.class).provide("IT1");
        InstanceManager.getDefault(TurnoutManager.class).provide("IT2");

        ConditionalNG conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class).createConditionalNG("IQC1", null);
        
        IfThenElse action = new IfThenElse("IQDA1", null, IfThenElse.Type.TRIGGER_ACTION);
        MaleSocket maleSocket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(action);
        conditionalNG.getChild(0).connect(maleSocket);
        
        ExpressionTurnout expression = new ExpressionTurnout("IQDE1", null);
        maleSocket = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expression);
        action.getChild(0).connect(maleSocket);
        
        JDialogOperator jdo = editItem(conditionalNG, "Edit ConditionalNG IQC1", "Edit ? ", 1);
        
        new JComboBoxOperator(jdo, 0).setSelectedIndex(1);
        new JComboBoxOperator(jdo, 1).setSelectedItem(Is_IsNot_Enum.IsNot);
        new JComboBoxOperator(jdo, 2).setSelectedItem(ExpressionTurnout.TurnoutState.Closed);
        new JButtonOperator(jdo, "OK").push();  // NOI18N
        
        Assert.assertEquals("IT1", expression.getTurnout().getBean().getSystemName());
        Assert.assertEquals(ExpressionTurnout.TurnoutState.Closed, expression.getBeanState());
    }
    
    @Test
    public void testDialogCreateNewTurnout() throws SocketAlreadyConnectedException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        InstanceManager.getDefault(TurnoutManager.class).provide("IT1");
        InstanceManager.getDefault(TurnoutManager.class).provide("IT2");

        ConditionalNG conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class).createConditionalNG("IQC1", null);
        
        IfThenElse action = new IfThenElse("IQDA1", null, IfThenElse.Type.TRIGGER_ACTION);
        MaleSocket maleSocket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(action);
        conditionalNG.getChild(0).connect(maleSocket);
        
        ExpressionTurnout expression = new ExpressionTurnout("IQDE1", null);
        maleSocket = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expression);
        action.getChild(0).connect(maleSocket);
        
        JDialogOperator jdo = editItem(conditionalNG, "Edit ConditionalNG IQC1", "Edit ? ", 1);
        
        new JRadioButtonOperator(jdo, 1).clickMouse();
        new JTextFieldOperator(jdo, 3).enterText("IT99");
        
        new JComboBoxOperator(jdo, 1).setSelectedItem(Is_IsNot_Enum.Is);
        new JComboBoxOperator(jdo, 2).setSelectedItem(ExpressionTurnout.TurnoutState.Thrown);
        new JButtonOperator(jdo, "OK").push();  // NOI18N
        
        Assert.assertEquals("IT99", expression.getTurnout().getBean().getSystemName());
        Assert.assertEquals(ExpressionTurnout.TurnoutState.Thrown, expression.getBeanState());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initConfigureManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        InstanceManager.getDefault(LogixNGPreferences.class).setShowSystemUserNames(true);
        JUnitUtil.initLogixNGManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
