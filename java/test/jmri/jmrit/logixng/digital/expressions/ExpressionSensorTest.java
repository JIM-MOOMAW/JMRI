package jmri.jmrit.logixng.digital.expressions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.Sensor;
import jmri.SensorManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.ConditionalNG_Manager;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.Is_IsNot_Enum;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.digital.actions.ActionAtomicBoolean;
import jmri.jmrit.logixng.digital.actions.IfThenElse;
import jmri.util.JUnitAppender;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ExpressionSensor
 * 
 * @author Daniel Bergqvist 2018
 */
public class ExpressionSensorTest extends AbstractDigitalExpressionTestBase {

    private LogixNG logixNG;
    private ConditionalNG conditionalNG;
    private ExpressionSensor expressionSensor;
    private ActionAtomicBoolean actionAtomicBoolean;
    private AtomicBoolean atomicBoolean;
    private Sensor sensor;
    
    
    @Override
    public ConditionalNG getConditionalNG() {
        return conditionalNG;
    }
    
    @Override
    public LogixNG getLogixNG() {
        return logixNG;
    }
    
    @Override
    public String getExpectedPrintedTree() {
        return String.format("Sensor IS1 is Active%n");
    }
    
    @Override
    public String getExpectedPrintedTreeFromRoot() {
        return String.format(
                "LogixNG: A new logix for test%n" +
                "   ConditionalNG: A conditionalNG%n" +
                "      ! %n" +
                "         If E then A1 else A2%n" +
                "            ? E%n" +
                "               Sensor IS1 is Active%n" +
                "            ! A1%n" +
                "               Set the atomic boolean to true%n" +
                "            ! A2%n" +
                "               Socket not connected%n");
    }
    
    @Override
    public NamedBean createNewBean(String systemName) {
        return new ExpressionSensor(systemName, null);
    }
    
    @Test
    public void testCtor() {
        ExpressionSensor t = new ExpressionSensor("IQDE321", null);
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testSensorState() {
        Assert.assertEquals("String matches", "Inactive", ExpressionSensor.SensorState.INACTIVE.toString());
        Assert.assertEquals("String matches", "Active", ExpressionSensor.SensorState.ACTIVE.toString());
        Assert.assertEquals("String matches", "Other", ExpressionSensor.SensorState.OTHER.toString());
        
        Assert.assertTrue("objects are equal", ExpressionSensor.SensorState.INACTIVE == ExpressionSensor.SensorState.get(Sensor.INACTIVE));
        Assert.assertTrue("objects are equal", ExpressionSensor.SensorState.ACTIVE == ExpressionSensor.SensorState.get(Sensor.ACTIVE));
        Assert.assertTrue("objects are equal", ExpressionSensor.SensorState.OTHER == ExpressionSensor.SensorState.get(-1));
        
        boolean hasThrown = false;
        try {
            ExpressionSensor.SensorState.get(Sensor.UNKNOWN);
        } catch (IllegalArgumentException ex) {
            hasThrown = true;
            Assert.assertTrue("Error message is correct", "invalid sensor state".equals(ex.getMessage()));
        }
        Assert.assertTrue("Exception is thrown", hasThrown);
    }
    
    @Test
    public void testDescription() {
        expressionSensor.setSensor((Sensor)null);
        Assert.assertTrue("Get sensor".equals(expressionSensor.getShortDescription()));
        Assert.assertTrue("Sensor Not selected is Active".equals(expressionSensor.getLongDescription()));
        expressionSensor.setSensor(sensor);
        expressionSensor.set_Is_IsNot(Is_IsNot_Enum.IS);
        expressionSensor.setSensorState(ExpressionSensor.SensorState.INACTIVE);
        Assert.assertTrue("Sensor IS1 is Inactive".equals(expressionSensor.getLongDescription()));
        expressionSensor.set_Is_IsNot(Is_IsNot_Enum.IS_NOT);
        Assert.assertTrue("Sensor IS1 is not Inactive".equals(expressionSensor.getLongDescription()));
        expressionSensor.setSensorState(ExpressionSensor.SensorState.OTHER);
        Assert.assertTrue("Sensor IS1 is not Other".equals(expressionSensor.getLongDescription()));
    }
    
    @Test
    public void testExpression() throws SocketAlreadyConnectedException, JmriException {
        expressionSensor.setSensor(sensor);
        expressionSensor.set_Is_IsNot(Is_IsNot_Enum.IS);
        expressionSensor.setSensorState(ExpressionSensor.SensorState.ACTIVE);
        
        // The action is not yet executed so the atomic boolean should be false
        Assert.assertFalse("atomicBoolean is false",atomicBoolean.get());
        // Throw the switch. This should not execute the conditional.
        sensor.setCommandedState(Sensor.ACTIVE);
        // The conditionalNG is not yet enabled so it shouldn't be executed.
        // So the atomic boolean should be false
        Assert.assertFalse("atomicBoolean is false",atomicBoolean.get());
        // Close the switch. This should not execute the conditional.
        sensor.setCommandedState(Sensor.INACTIVE);
        // Enable the conditionalNG and all its children.
        conditionalNG.setEnabled(true);
        // The action is not yet executed so the atomic boolean should be false
        Assert.assertFalse("atomicBoolean is false",atomicBoolean.get());
        // Throw the switch. This should execute the conditional.
        sensor.setCommandedState(Sensor.ACTIVE);
        // The action should now be executed so the atomic boolean should be true
        Assert.assertTrue("atomicBoolean is true",atomicBoolean.get());
    }
    
    @Test
    public void testSetSensor() {
        expressionSensor.unregisterListeners();
        
        Sensor otherSensor = InstanceManager.getDefault(SensorManager.class).provide("IM99");
        Assert.assertNotEquals("Sensors are different", otherSensor, expressionSensor.getSensor().getBean());
        expressionSensor.setSensor(otherSensor);
        Assert.assertEquals("Sensors are equal", otherSensor, expressionSensor.getSensor().getBean());
        
        NamedBeanHandle<Sensor> otherSensorHandle =
                InstanceManager.getDefault(NamedBeanHandleManager.class)
                        .getNamedBeanHandle(otherSensor.getDisplayName(), otherSensor);
        expressionSensor.setSensor((Sensor)null);
        Assert.assertNull("Sensor is null", expressionSensor.getSensor());
        expressionSensor.setSensor(otherSensorHandle);
        Assert.assertEquals("Sensors are equal", otherSensor, expressionSensor.getSensor().getBean());
        Assert.assertEquals("SensorHandles are equal", otherSensorHandle, expressionSensor.getSensor());
    }
    
    @Test
    public void testSetSensorException() {
        // Test setSensor() when listeners are registered
        Assert.assertNotNull("Sensor is not null", sensor);
        expressionSensor.setSensor(sensor);
        
        Assert.assertNotNull("Sensor is not null", expressionSensor.getSensor());
        expressionSensor.registerListeners();
        boolean thrown = false;
        try {
            expressionSensor.setSensor((String)null);
        } catch (RuntimeException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        JUnitAppender.assertErrorMessage("setSensor must not be called when listeners are registered");
        
        thrown = false;
        try {
            expressionSensor.setSensor((NamedBeanHandle<Sensor>)null);
        } catch (RuntimeException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        JUnitAppender.assertErrorMessage("setSensor must not be called when listeners are registered");
        
        thrown = false;
        try {
            expressionSensor.setSensor((Sensor)null);
        } catch (RuntimeException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        JUnitAppender.assertErrorMessage("setSensor must not be called when listeners are registered");
    }
    
    @Test
    public void testVetoableChange() throws PropertyVetoException {
        // Get the expressionSensor and set the sensor
        Assert.assertNotNull("Sensor is not null", sensor);
        expressionSensor.setSensor(sensor);
        
        // Get some other sensor for later use
        Sensor otherSensor = InstanceManager.getDefault(SensorManager.class).provide("IM99");
        Assert.assertNotNull("Sensor is not null", otherSensor);
        Assert.assertNotEquals("Sensor is not equal", sensor, otherSensor);
        
        // Test vetoableChange() for some other propery
        expressionSensor.vetoableChange(new PropertyChangeEvent(this, "CanSomething", "test", null));
        Assert.assertEquals("Sensor matches", sensor, expressionSensor.getSensor().getBean());
        
        // Test vetoableChange() for a string
        expressionSensor.vetoableChange(new PropertyChangeEvent(this, "CanDelete", "test", null));
        Assert.assertEquals("Sensor matches", sensor, expressionSensor.getSensor().getBean());
        expressionSensor.vetoableChange(new PropertyChangeEvent(this, "DoDelete", "test", null));
        Assert.assertEquals("Sensor matches", sensor, expressionSensor.getSensor().getBean());
        
        // Test vetoableChange() for another sensor
        expressionSensor.vetoableChange(new PropertyChangeEvent(this, "CanDelete", otherSensor, null));
        Assert.assertEquals("Sensor matches", sensor, expressionSensor.getSensor().getBean());
        expressionSensor.vetoableChange(new PropertyChangeEvent(this, "DoDelete", otherSensor, null));
        Assert.assertEquals("Sensor matches", sensor, expressionSensor.getSensor().getBean());
        
        // Test vetoableChange() for its own sensor
        boolean thrown = false;
        try {
            expressionSensor.vetoableChange(new PropertyChangeEvent(this, "CanDelete", sensor, null));
        } catch (PropertyVetoException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        Assert.assertEquals("Sensor matches", sensor, expressionSensor.getSensor().getBean());
        expressionSensor.vetoableChange(new PropertyChangeEvent(this, "DoDelete", sensor, null));
        Assert.assertNull("Sensor is null", expressionSensor.getSensor());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() throws SocketAlreadyConnectedException {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        
        _category = Category.ITEM;
        _isExternal = true;
        
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A new logix for test");  // NOI18N
        conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class)
                .createConditionalNG("A conditionalNG");  // NOI18N
        conditionalNG.setRunOnGUIDelayed(false);
        logixNG.addConditionalNG(conditionalNG);
        logixNG.activateLogixNG();
        IfThenElse ifThenElse = new IfThenElse("IQDA321", null, IfThenElse.Type.TRIGGER_ACTION);
        MaleSocket maleSocket =
                InstanceManager.getDefault(DigitalActionManager.class).registerAction(ifThenElse);
        conditionalNG.getChild(0).connect(maleSocket);
        
        expressionSensor = new ExpressionSensor("IQDE321", null);
        MaleSocket maleSocket2 =
                InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionSensor);
        ifThenElse.getChild(0).connect(maleSocket2);
        
        _base = expressionSensor;
        _baseMaleSocket = maleSocket2;
        
        atomicBoolean = new AtomicBoolean(false);
        actionAtomicBoolean = new ActionAtomicBoolean(atomicBoolean, true);
        MaleSocket socketAtomicBoolean = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionAtomicBoolean);
        ifThenElse.getChild(1).connect(socketAtomicBoolean);
        
        sensor = InstanceManager.getDefault(SensorManager.class).provide("IS1");
        expressionSensor.setSensor(sensor);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
