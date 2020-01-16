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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import org.nmrfx.structure.chemistry.Atom;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.util.FastMath;

public class AtomEnergyProp {

    private static boolean FILE_LOADED = false;

    final String name;
    //leonard-jones a parameter
    private final double a;
    //leonard-jones b parameter
    private final double b;
    //ideal distance
    private final double r;
    //hard sphere repulsive distance
    private final double rh;
    //energy at r
    private final double e;
    //electric charge
    private final double c;
    //atomic mass
    private final double mass;
    // hbond donor (1), acceptor (-1)
    private final int hbondMode;
    //scaling factor
    private static double rscale = 0.68;
    private static final HashMap<String, AtomEnergyProp> propMap = new HashMap<String, AtomEnergyProp>();
    private static double hbondDelta = 0.30;

    public AtomEnergyProp(final String name, final double a, final double b, final double r, final double rh, final double e, final double c, final double mass, final int hbondMode) {
        this.name = name;
        this.a = FastMath.sqrt(a);
        this.b = FastMath.sqrt(b);
        this.r = r;
        this.rh = rh;
        this.e = e;
        this.c = c;
        this.mass = mass;
        this.hbondMode = hbondMode;
    }

    public static void readPropFile() throws FileNotFoundException, IOException {
        if (!FILE_LOADED) {
            readPropFile("reslib_iu/params.txt");
        }
    }

    public static void readPropFile(String fileName) throws FileNotFoundException, IOException {
        String string;
        LineNumberReader lineReader;
        FILE_LOADED = true;
        BufferedReader bf;
        if (fileName.startsWith("reslib_iu")) {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            InputStream istream = cl.getResourceAsStream(fileName);
            bf = new BufferedReader(new InputStreamReader(istream));
        } else {
            bf = new BufferedReader(new FileReader(fileName));
        }

        lineReader = new LineNumberReader(bf);
        List<String> headerS = Arrays.asList();
        //AtomType        HardRadius      RMin    E       Mass    HBondType

        while (true) {
            string = lineReader.readLine();
            if (string != null) {
                List<String> stringS = Arrays.asList(string.split("\\s+"));
                if (string.startsWith("AtomType")) {
                    headerS = stringS;
                } else {
                    String aType = stringS.get(headerS.indexOf("AtomType"));
                    double r = Double.parseDouble(stringS.get(headerS.indexOf("RMin")));
                    double rh = Double.parseDouble(stringS.get(headerS.indexOf("HardRadius")));
                    double e = Double.parseDouble(stringS.get(headerS.indexOf("E")));
                    double m = Double.parseDouble(stringS.get(headerS.indexOf("Mass")));
                    int hType = (int) Double.parseDouble(stringS.get(headerS.indexOf("HBondType")));
                    
                    e = -e;
                    r = r * 2.0;
                    double a = 1.0;
                    double b = 1.0;
                    double c = 0.0;
                    AtomEnergyProp prop = new AtomEnergyProp(aType, a, b, r, rh, e, c, m, hType);
                    AtomEnergyProp.add(aType, prop);
                }
            } else {
                break;
            }
        }

    }

    public void clear() {
        propMap.clear();
    }

    public static void add(final String atomType, final AtomEnergyProp prop) {
        propMap.put(atomType, prop);
    }

    public static AtomEnergyProp get(final String atomType) {
        return propMap.get(atomType);
    }

    public String getName() {
        return name;
    }

    /**
     * @return the a
     */
    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getR() {
        return r;
    }

    public double getRh() {
        return rh;
    }

    public double getE() {
        return e;
    }

    public double getMass() {
        return mass;
    }

    public int getHBondMode() {
        return hbondMode;
    }

    /**
     * computes interaction between two molecules based on table values
     * <p>
     * This method calculates the interation between 2 molecules. It retrieves
     * the ideal energy values from the table for both atoms It then calculates
     * the radius or distance between both atoms. The rh value is calculating by
     * simply adding both the radius. Hydrogen may be removed and substited by a
     * certain number of Angstrom's indicated by AtomEnergyProp
     *
     * @param AtomEnergy iProp Properties of atom 1
     * @param AtomEnergy jProp properties of atom 2
     * @param boolean hardSphere determines if you want to calculate rh w/out
     * hydrogen
     * @param double hardSphere determines the value you want to add in
     * substitution for hydrogen
     */
    public static EnergyPair getInteraction(final Atom atom1, final Atom atom2, double hardSphere,
            boolean usehardSphere, double shrinkValue, double shrinkHValue) {

        AtomEnergyProp iProp = (AtomEnergyProp) atom1.atomEnergyProp;
        AtomEnergyProp jProp = (AtomEnergyProp) atom2.atomEnergyProp;

        double a1 = iProp.getA() * jProp.getA();
        double b1 = iProp.getB() * jProp.getB();
        double c1 = 0.0;

        double r = FastMath.sqrt(iProp.getR() * jProp.getR());
        double ea = -FastMath.sqrt(iProp.getE() * jProp.getE());
        double r2 = (rscale * r) * (rscale * r);
        double rh1 = iProp.getRh();
        double rh2 = jProp.getRh();
        if ((atom1.getAtomicNumber() != 1) || (atom2.getAtomicNumber() != 1) || jProp.getName().equals("HO")) {
            Atom testAtom1;
            Atom testAtom2;
            if (atom1.rotUnit < atom2.rotUnit) {
                testAtom1 = atom1;
                testAtom2 = atom2;
            } else {
                testAtom2 = atom1;
                testAtom1 = atom2;
            }
            Atom rotGroup1 = testAtom1.rotGroup;
            Atom rotGroup2 = testAtom2.rotGroup;
            Atom parent2 = null;
            if (rotGroup2 != null) {
                parent2 = rotGroup2.parent;
            }
            // atoms in close groups are allowed to be a little closer to add a little more flexibility since we don't allow bond angles and lengths to change
            if (parent2 != null && (parent2.parent == testAtom1)) {
                rh1 -= 0.1;
                rh2 -= 0.1;
            } else if (rotGroup1 != null) {
                Atom parent1 = rotGroup1.parent;
                if (parent1 != null) {
                    if (parent1.parent == testAtom2) {
                        rh1 -= 0.1;
                        rh2 -= 0.1;
                    } else if (parent1 == testAtom2.parent) {
                        rh1 -= 0.1;
                        rh2 -= 0.1;
                    } else if (rotGroup1 == parent2) {
                        rh1 -= 0.1;
                        rh2 -= 0.1;
                    } else if (parent1 == testAtom2.parent) {
                        rh1 -= 0.1;
                        rh2 -= 0.1;
                    } else if (parent2 == testAtom1.parent) {
                        rh1 -= 0.1;
                        rh2 -= 0.1;
                    } else if (parent1 == parent2) {
                        //  rh1 -= 0.1;
                        //  rh2 -= 0.1;
                    }
                }
            }
        }

        int hbond = iProp.hbondMode * jProp.hbondMode;
        if (hbond < 0) {
            rh1 -= hbondDelta;
            rh2 -= hbondDelta;
        }
        if (atom1.getAtomicNumber() != 1) {
            rh1 -= shrinkValue;
        } else {
            rh1 -= shrinkHValue;
        }
        if (atom2.getAtomicNumber() != 1) {
            rh2 -= shrinkValue;
        } else {
            rh2 -= shrinkHValue;
        }

        if (usehardSphere) {
            if (atom1.hydrogens > 0) {
                rh1 += hardSphere;
            }
            if (atom2.hydrogens > 0) {
                rh2 += hardSphere;
            }
        }
        double rh = rh1 + rh2;

        EnergyPair ePair = new EnergyPair(a1, b1, c1, r, r2, rh, ea);
        return ePair;
    }

    public static boolean interact(final Atom atom1, final Atom atom2) {
        final boolean value;
        AtomEnergyProp prop1 = (AtomEnergyProp) atom1.atomEnergyProp;
        AtomEnergyProp prop2 = (AtomEnergyProp) atom2.atomEnergyProp;
// fixme  only appropriate for rh mode of repel function
        if ((prop2 != null) && (prop1 != null) && (prop1.rh > 1.0e-6) && (prop2.rh > 1.0e-6)) {
            value = true;
        } else {
            value = false;
        }
        return value;
    }
}
