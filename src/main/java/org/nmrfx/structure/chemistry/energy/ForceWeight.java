/*
 * NMRFx Structure : A Program for Calculating Structures 
 * Copyright (C) 2004-2017 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.nmrfx.structure.chemistry.energy;

public class ForceWeight {

    private final double electrostatic;
    private final double dihedralProb;
    private final double dihedral;
    private final double robson;
    private final double repel;
    private final double noe;
    private final double shift;
    private final double bond = 5000.0;
    private final double irp;
    final double cutoffSq = 64.0;
    final double cutoffSwitchSq = 36.0;
    final double cutoffDem = 28.0;

    public ForceWeight() {
        this(-1.0, -1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0);
    }

    public ForceWeight(final double electrostatic, final double robson, final double repel, final double noe, double tortionAngle, double dihedral, double irp, double shift) {
        this.electrostatic = electrostatic;
        this.robson = robson;
        this.repel = repel;
        this.noe = noe;
        this.dihedralProb = tortionAngle;
        this.dihedral = dihedral;
        this.irp = irp;
        this.shift = shift;
    }

    /**
     * @return the electrostatic
     */
    public double getElectrostatic() {
        return electrostatic;
    }

    public double getRobson() {
        return robson;
    }

    public double getRepel() {
        return repel;
    }

    public double getBond() {
        return bond;
    }

    public double getNOE() {
        return noe;
    }

    public double getDihedralProb() {
        return dihedralProb;
    }

    public double getDihedral() {
        return dihedral;
    }

    public double getIrp() {
        return irp;
    }
    
    public double getShift() {
        return shift;
    }
}
