package jmri;

/**
 * Represent a single signal, either in {@link SignalHead} or {@SignalMast} form.
 * <P>
 * This interface defines two bound parameters:
 * <DL>
 * <DT>Lit<DD>Whether the signal's lamps are lit or left dark.
 * <P>
 * This differs from the DARK color or all-off appearance. 
 * Lit is intended to allow you to extinguish a
 * signal for approach lighting, while still allowing it to be set
 * to a definite appearance or aspect for e.g. display on a panel or evaluation in higher level
 * logic.
 *
 * <DT>Held<DD>Whether the signal's lamps should be forced to a specific
 * appearance or aspect, e.g. RED or STOP, in higher-level logic.
 * <P>
 * For use in signaling systems, this is a convenient way of storing whether a
 * higher-level of control (e.g. non-vital system or dispatcher) has "held" the
 * signal at stop. It does not effect how this signal actually works; any
 * appearance can be set and will be displayed even when "held" is set.
 * </dl>
 *
 * The following can be relied on:
 * <ul>
 * <li>isCleared() && isAtStop() is false: they are disjoint.
 * <li>isAtStop() && isShowingRestricting() is false: they are disjoint.
 * <li>isShowingRestricting() && isCleared() is false: they are disjoint.
 * <li>isAtStop() || isShowingRestricting() || isCleared() is usually true, but it can be false; 
 *                   the three methods do not cover all cases
 * </ul>
 * <hr>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <P>
 *
 * @author Bob Jacobsen Copyright (C) 2002, 2008, 2017
 */
public interface Signal extends NamedBean {

    /**
     * Determine whether this signal shows an aspect or appearance
     * that allows travel past it, e.g. it's "been cleared".
     * This might be a yellow or green appearance, or an Approach or Clear
     * aspect
     */
    public boolean isCleared();

    /**
     * Determine whether this signal shows an aspect or appearance
     * that allows travel past it only at restricted speed.
     * This might be a flashing red appearance, or a 
     * Restricting aspect.
     */
    public boolean isShowingRestricting();

    /**
     * Determine whether this signal shows an aspect or appearance
     * that forbid travel past it.
     * This might be a red appearance, or a 
     * Stop aspect. Stop-and-Proceed or Restricting would return false here.
     */
    public boolean isAtStop();

    /**
     * Get whether the signal is lit or dark. Changes to this value can be
     * listened to using the {@literal Lit} property.
     *
     * @return true if lit; false if dark
     */
    public boolean getLit();

    public void setLit(boolean newLit);

    /**
     * Get whether the signal is held. Changes to this value can be listened to
     * using the {@literal Held} property. It controls what mechanisms can
     * control the signal's appearance. The actual semantics are defined by those
     * external mechanisms.
     *
     * @return true if held; false otherwise
     */
    public boolean getHeld();

    public void setHeld(boolean newHeld);

}
