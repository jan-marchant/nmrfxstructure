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

package org.nmrfx.structure.chemistry;

import org.nmrfx.structure.chemistry.energy.AtomEnergyProp;
import org.nmrfx.structure.chemistry.energy.Dihedral;
import org.nmrfx.structure.chemistry.energy.EnergyCoords;
import org.nmrfx.structure.chemistry.search.*;
import org.nmrfx.structure.fastlinear.FastVector3D;
import org.nmrfx.structure.utilities.Util;
import java.io.*;
import java.util.*;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

public class Molecule implements Serializable {

    public static Vector atomList = null;
    public static final Vector conditions = new Vector();
    public static String defaultMol = "default";
    public static final Hashtable<String, Molecule> molecules = new Hashtable<String, Molecule>();
    public static final Hashtable sites = new Hashtable();
    public static final Vector globalSelected = new Vector(1024);
    private static final Vector bselected = new Vector(1024);
    public static int selCycleCount = 0;
    public static final int ENERGY = 0;
    public static final int SQ_SCORE = 1;
    public static final int GRADIENT = 2;
    public static final int R_FACTOR = 3;
    public static final int INTRA_ENERGY = 4;
    public static final int INTER_ENERGY = 5;
    public static final int LABEL_NONE = 0;
    public static final int LABEL_LABEL = 1;
    public static final int LABEL_FC = 2;
    public static final int LABEL_SYMBOL = 3;
    public static final int LABEL_NUMBER = 4;
    public static final int LABEL_SYMBOL_AND_NUMBER = 5;
    public static final int LABEL_FFC = 6;
    public static final int LABEL_SECONDARY_STRUCTURE = 7;
    public static final int LABEL_RESIDUE = 8;
    public static final int LABEL_CHARGE = 9;
    public static final int LABEL_VALUE = 10;
    public static final int LABEL_TITLE = 11;
    public static final int LABEL_MOLECULE_NAME = 12;
    public static final int LABEL_STRING = 13;
    public static final int LABEL_BOND = 14;
    public static final int LABEL_CUSTOM = 15;
    public static final int LABEL_NAME = 16;
    public static final int LABEL_HPPM = 17;
    public static final int LABEL_PPM = 18;
    public static final LinkedHashMap labelTypes = new LinkedHashMap();
    public static final LinkedHashSet displayTypes = new LinkedHashSet();
    public static final LinkedHashSet colorTypes = new LinkedHashSet();
    public static final LinkedHashSet shapeTypes = new LinkedHashSet();
    //public static MoleculeTableModel molTableModel = null;
    public static final Map compoundMap = new HashMap();

    static {
        labelTypes.put(Integer.valueOf(LABEL_NONE), "none");
        labelTypes.put(Integer.valueOf(LABEL_LABEL), "label");
        labelTypes.put(Integer.valueOf(LABEL_FC), "fc");
        labelTypes.put(Integer.valueOf(LABEL_SYMBOL), "symbol");
        labelTypes.put(Integer.valueOf(LABEL_NUMBER), "number");
        labelTypes.put(Integer.valueOf(LABEL_SYMBOL_AND_NUMBER), "both");
        labelTypes.put(Integer.valueOf(LABEL_FFC), "ffc");
        labelTypes.put(Integer.valueOf(LABEL_SECONDARY_STRUCTURE), "ss");
        labelTypes.put(Integer.valueOf(LABEL_RESIDUE), "residue");
        labelTypes.put(Integer.valueOf(LABEL_CHARGE), "charge");
        labelTypes.put(Integer.valueOf(LABEL_VALUE), "value");
        labelTypes.put(Integer.valueOf(LABEL_TITLE), "title");
        labelTypes.put(Integer.valueOf(LABEL_MOLECULE_NAME), "mname");
        labelTypes.put(Integer.valueOf(LABEL_STRING), "string");
        labelTypes.put(Integer.valueOf(LABEL_BOND), "bond");
        labelTypes.put(Integer.valueOf(LABEL_CUSTOM), "custom");
        labelTypes.put(Integer.valueOf(LABEL_NAME), "name");
        labelTypes.put(Integer.valueOf(LABEL_HPPM), "hppm");
        labelTypes.put(Integer.valueOf(LABEL_PPM), "ppm");

        labelTypes.put("none", Integer.valueOf(LABEL_NONE));
        labelTypes.put("fc", Integer.valueOf(LABEL_FC));
        labelTypes.put("label", Integer.valueOf(LABEL_LABEL));
        labelTypes.put("symbol", Integer.valueOf(LABEL_SYMBOL));
        labelTypes.put("number", Integer.valueOf(LABEL_NUMBER));
        labelTypes.put("both", Integer.valueOf(LABEL_SYMBOL_AND_NUMBER));
        labelTypes.put("ffc", Integer.valueOf(LABEL_FFC));

        //labelTypes.put( "ss",Integer.valueOf(LABEL_SECONDARY_STRUCTURE));
        labelTypes.put("residue", Integer.valueOf(LABEL_RESIDUE));
        labelTypes.put("charge", Integer.valueOf(LABEL_CHARGE));
        labelTypes.put("value", Integer.valueOf(LABEL_VALUE));
        labelTypes.put("title", Integer.valueOf(LABEL_TITLE));
        labelTypes.put("mname", Integer.valueOf(LABEL_MOLECULE_NAME));
        labelTypes.put("string", Integer.valueOf(LABEL_STRING));
        labelTypes.put("bond", Integer.valueOf(LABEL_BOND));
        labelTypes.put("custom", Integer.valueOf(LABEL_CUSTOM));
        labelTypes.put("name", Integer.valueOf(LABEL_NAME));
        labelTypes.put("hppm", Integer.valueOf(LABEL_HPPM));
        labelTypes.put("ppm", Integer.valueOf(LABEL_PPM));

        displayTypes.add("none");
        displayTypes.add("wire");
        displayTypes.add("hwire");
        displayTypes.add("bwire");

        //displayTypes.add("stick");
        //displayTypes.add("hstick");
        //displayTypes.add("bstick");
        displayTypes.add("ball");
        displayTypes.add("pball");
        displayTypes.add("cpk");

        //displayTypes.add("custom");
        //displayTypes.add("point");
        //colorTypes.add("solid");
        colorTypes.add("atom");

        /*
         colorTypes.add("p_atom");
         colorTypes.add("residue");
         colorTypes.add("p_residue");
         colorTypes.add("segment");
         colorTypes.add("property");
         colorTypes.add("gcharge");
         colorTypes.add("p_gcharge");
         colorTypes.add("charge");
         colorTypes.add("ffc");
         colorTypes.add("ss");
         colorTypes.add("amf");
         colorTypes.add("ntoc");
         colorTypes.add("rgb");
         colorTypes.add("custom");
         colorTypes.add("gproperty");
         */
        shapeTypes.add("circle");
        shapeTypes.add("square");
        shapeTypes.add("triangle");
    }
    public Set structures = new TreeSet();
    private int[] activeStructures = null;
    public boolean labelsCurrent = false;
    public int nResidues;
    public int lastResNum;
    public String name;
    public String originalName = null;
    public String source = null;
    public String comment = null;
    public String display = "wire";
    public String posShapeType = "sphere";
    public String negShapeType = "sphere";
    public String title = null;
    public byte label = 0;
    public String colorType = "atom";
    public float[][] model = new float[4][4];
    public float[][] view = new float[4][4];
    public float[] values = new float[10];
    public float[] color = new float[3];
    double[] center = new double[3];
    public float[] titlePosition = new float[3];
    public String energyType = null;
    public boolean deleted = false;
    float bondSpace = 12.0f;
    public boolean changed = false;
    // FIXME should be crystal object
    public String crystal = null;
    public LinkedHashMap<String, Entity> entities;
    public LinkedHashMap entityLabels = null;
    public LinkedHashMap<String, CoordSet> coordSets;
    private HashMap<String, String> propertyMap = new HashMap<String, String>();
    private ArrayList<Atom> angleAtoms = null;
    private ArrayList<Atom> pseudoAngleAtoms = null;
    ArrayList<Atom> atoms = new ArrayList<>();
    private boolean atomArrayValid = false;
    Map<String, Atom> atomMap = new HashMap<>();
    ArrayList<Bond> bonds = new ArrayList<Bond>();
    SpatialSet spSets[][] = null;
    int genVecs[][] = null;
    EnergyCoords eCoords = new EnergyCoords();
    Dihedral dihedrals = null;

// fixme    public EnergyLists energyList = null;
    public Molecule(String name) {
        this.name = name;
        entities = new LinkedHashMap<String, Entity>();
        entityLabels = new LinkedHashMap();
        coordSets = new LinkedHashMap<String, CoordSet>();
        molecules.put(name, this);
        defaultMol = this.name;
        nResidues = 0;
        Atom.resetLastAtom();
    }

    public void changed() {
        changed = true;
    }

    public void clearChanged() {
        changed = false;
    }

    public boolean isChanged() {
        return changed;
    }

    public static boolean isAnyChanged() {
        boolean anyChanged = false;
        for (Object checkMol : molecules.values()) {
            if (((Molecule) checkMol).isChanged()) {
                anyChanged = true;
                break;

            }
        }
        return anyChanged;
    }

    public static void clearAllChanged() {
        for (Object checkMol : molecules.values()) {
            ((Molecule) checkMol).clearChanged();
        }
    }

    public void reName(Molecule molecule, Compound compound, String name1,
            String name2) {
        molecule.name = name2;
        Molecule.molecules.remove(name1);
        compound.name = molecule.name;
        Molecule.molecules.put(molecule.name, molecule);
        Molecule.defaultMol = molecule.name;
    }

    public String getName() {
        return name;
    }

    public void setTitle(final String value) {
        title = value.trim();
    }

    public String getTitle() {
        String value = name;
        if ((title != null) && !title.equals("")) {
            value = title;
        }
        return value;
    }

    public static void addMoleculeModel() {
        //molTableModel = new MoleculeTableModel();
    }

    public static void removeAll() {
        // fixme need to remove each molecule from list, rather than just settng molecules to new Hashtable?
        // should at least just clear molecules
        if (atomList != null) {
            atomList.removeAllElements();
            atomList = null;
        }

        molecules.clear();
        globalSelected.removeAllElements();
        bselected.removeAllElements();
        conditions.removeAllElements();
        defaultMol = "default";
    }

    public void remove() {
        if (atomList != null) {
            atomList.removeAllElements();
            atomList = null;
        }

        molecules.remove(name);
        globalSelected.removeAllElements();
        bselected.removeAllElements();
        structures.clear();
        resetActiveStructures();
        conditions.removeAllElements();
        defaultMol = "default";

        Enumeration e = molecules.keys();

        if (e.hasMoreElements()) {
            defaultMol = (String) e.nextElement();
        } else {
            defaultMol = "default";
        }
    }

    public void clearStructures() {
        structures.clear();
        activeStructures = null;
    }

    public void resetActiveStructures() {
        activeStructures = null;
    }

    public void setActiveStructures(TreeSet selSet) {
        activeStructures = new int[selSet.size()];
        Iterator e = selSet.iterator();
        int i = 0;
        while (e.hasNext()) {
            Integer intStructure = (Integer) e.next();
            activeStructures[i++] = intStructure.intValue();
        }
    }

    public Set getStructures() {
        return structures;
    }

    public int[] getActiveStructures() {
        if (activeStructures == null) {
            activeStructures = new int[structures.size()];
            for (int i = 0; i < activeStructures.length; i++) {
                activeStructures[i] = i;
            }
        }
        return activeStructures.clone();
    }

    public Set<String> getCoordSetNames() {
        return coordSets.keySet();
    }

    public boolean coordSetExists(String setName) {
        CoordSet coordSet = (CoordSet) coordSets.get(setName);

        return (coordSet != null);
    }

    public void addCoordSet(String setName, Entity entity) {
        CoordSet coordSet = (CoordSet) coordSets.get(setName);

        if (coordSet == null) {
            coordSet = new CoordSet(setName, entity);
            coordSets.put(setName, coordSet);
            coordSet.addEntity(entity);
        } else {
            coordSet.addEntity(entity);
        }
    }

    public CoordSet getFirstCoordSet() {
        Iterator e = coordSets.values().iterator();
        CoordSet coordSet = null;
        while (e.hasNext()) {
            coordSet = (CoordSet) e.next();
            break;
        }
        return coordSet;
    }

    public String getDefaultEntity() {
        Object[] keys = entities.keySet().toArray();
        return keys[0].toString();
    }

    public void addEntity(Entity entity) {
        CoordSet coordSet = getFirstCoordSet();
        final String coordSetName;
        if (coordSet != null) {
            coordSetName = coordSet.getName();
        } else {
            coordSetName = "A";
        }
        addEntity(entity, coordSetName);
    }

    public void addEntity(Entity entity, String coordSetName) {
        entities.put(entity.name, entity);
        entityLabels.put(entity.label, entity);
        if (entity.entityID == 0) {
            entity.setIDNum(entities.size());
        }
        entity.molecule = this;
        addCoordSet(coordSetName, entity);
    }

    public Entity getEntity(String name) {
        if (name == null) {
            return null;
        } else {
            return ((Entity) entities.get(name));
        }
    }

    public ArrayList<Polymer> getPolymers() {
        ArrayList<Polymer> polymers = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (entity instanceof Polymer) {
                polymers.add((Polymer) entity);
            }
        }
        return polymers;
    }

    public ArrayList<Compound> getLigands() {
        ArrayList<Compound> compounds = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (entity instanceof Compound) {
                compounds.add((Compound) entity);
            }
        }
        return compounds;
    }

    public static Molecule get(String name) {
        if (name == null) {
            return null;
        } else {
            return ((Molecule) molecules.get(name));
        }
    }

    public void setDihedrals(Dihedral dihedrals) {
        this.dihedrals = dihedrals;
    }

    public Dihedral getDihedrals() {
        return dihedrals;
    }

    public static Point3 avgCoords(MolFilter molFilter1) throws IllegalArgumentException, InvalidMoleculeException {
        Vector selected1 = matchAtoms(molFilter1);
        Point3 pt1 = Atom.avgAtom(selected1, molFilter1.getStructureNum());
        if (pt1 == null) {
            throw new IllegalArgumentException("No coordinates for atom " + molFilter1.getString());
        }
        return pt1;
    }

    public static double calcDistance(String aname0, String aname1) {
        int structureNum = 0;
        Atom[] atoms = new Atom[2];
        atoms[0] = getAtomByName(aname0);
        atoms[1] = getAtomByName(aname1);
        SpatialSet[] spSets = new SpatialSet[2];
        Point3[] pts = new Point3[2];
        int i = 0;
        for (Atom atom : atoms) {
            if (atom == null) {
                System.out.println(aname0 + " " + aname1);
                throw new IllegalArgumentException("No atom for " + i);
            }
            spSets[i] = atom.spatialSet;
            pts[i] = spSets[i].getPoint(structureNum);
            if (pts[i] == null) {
                throw new IllegalArgumentException("No coordinates for atom " + atom.getFullName());
            }
            i++;
        }
        return Atom.calcDistance(pts[0], pts[1]);
    }

    public static double calcDistance(MolFilter molFilter1,
            MolFilter molFilter2) throws MissingCoordinatesException, InvalidMoleculeException {
        Vector selected1 = matchAtoms(molFilter1);
        Vector selected2 = matchAtoms(molFilter2);
        Point3 pt1 = Atom.avgAtom(selected1, molFilter1.getStructureNum());
        Point3 pt2 = Atom.avgAtom(selected2, molFilter2.getStructureNum());
        if (pt1 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter1.getString());
        }
        if (pt2 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter2.getString());
        }
        return (Atom.calcDistance(pt1, pt2));
    }

    public static double calcAngle(MolFilter molFilter1,
            MolFilter molFilter2, MolFilter molFilter3) throws MissingCoordinatesException {
        SpatialSet spSet1 = getSpatialSet(molFilter1);
        SpatialSet spSet2 = getSpatialSet(molFilter2);
        SpatialSet spSet3 = getSpatialSet(molFilter3);
        if (spSet1 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter1.getString());
        }
        if (spSet2 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter2.getString());
        }
        if (spSet3 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter3.getString());
        }

        Point3 pt1 = spSet1.getPoint(molFilter1.getStructureNum());
        Point3 pt2 = spSet2.getPoint(molFilter2.getStructureNum());
        Point3 pt3 = spSet3.getPoint(molFilter3.getStructureNum());
        if (pt1 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter1.getString());
        }
        if (pt2 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter2.getString());
        }
        if (pt3 == null) {
            throw new MissingCoordinatesException("No coordinates for atom " + molFilter3.getString());
        }
        return (Atom.calcAngle(pt1, pt2, pt3));
    }

    public static double calcDihedral(MolFilter molFilter1,
            MolFilter molFilter2, MolFilter molFilter3, MolFilter molFilter4) throws IllegalArgumentException {
        MolFilter[] molFilters = new MolFilter[4];
        molFilters[0] = molFilter1;
        molFilters[1] = molFilter2;
        molFilters[2] = molFilter3;
        molFilters[3] = molFilter4;
        SpatialSet[] spSets = new SpatialSet[4];
        Point3[] pts = new Point3[4];
        int i = 0;
        for (MolFilter molFilter : molFilters) {
            spSets[i] = getSpatialSet(molFilter);
            if (spSets[i] == null) {
                throw new IllegalArgumentException("No atom for " + molFilter.getString());
            }
            pts[i] = spSets[i].getPoint(molFilter.getStructureNum());
            if (pts[i] == null) {
                throw new IllegalArgumentException("No coordinates for atom " + molFilter.getString());
            }
            i++;
        }
        return (Atom.calcDihedral(pts[0], pts[1], pts[2], pts[3]));
    }

    public static double calcDihedral(final Atom[] atoms) throws MissingCoordinatesException {
        int structureNum = 0;
        SpatialSet[] spSets = new SpatialSet[4];
        Point3[] pts = new Point3[4];
        int i = 0;
        for (Atom atom : atoms) {
            spSets[i] = atom.spatialSet;
            pts[i] = spSets[i].getPoint(structureNum);
            if (pts[i] == null) {
                throw new MissingCoordinatesException("No coordinates for atom " + atom.getFullName());
            }
            i++;
        }
        return (Atom.calcDihedral(pts[0], pts[1], pts[2], pts[3]));
    }

    public double calcDihedral(final String aname0, final String aname1, final String aname2, final String aname3) {
        int structureNum = 0;
        Atom[] atoms = new Atom[4];
        atoms[0] = getAtom(aname0);
        atoms[1] = getAtom(aname1);
        atoms[2] = getAtom(aname2);
        atoms[3] = getAtom(aname3);
        SpatialSet[] spSets = new SpatialSet[4];
        Point3[] pts = new Point3[4];
        int i = 0;
        for (Atom atom : atoms) {
            if (atom == null) {
                System.out.println(aname0 + " " + aname1 + " " + aname2 + " " + aname3);
                throw new IllegalArgumentException("No atom for " + i);
            }
            spSets[i] = atom.spatialSet;
            pts[i] = spSets[i].getPoint(structureNum);
            if (pts[i] == null) {
                throw new IllegalArgumentException("No coordinates for atom " + atom.getFullName());
            }
            i++;
        }
        return (Atom.calcDihedral(pts[0], pts[1], pts[2], pts[3]));
    }

    public void nullCoords() {
        updateAtomArray();
        int iStructure = 0;
        for (Atom atom : atoms) {
            atom.setPointValidity(iStructure, false);
        }
    }

    public void nullCoords(int iStructure) {
        updateAtomArray();
        for (Atom atom : atoms) {
            atom.setPointValidity(iStructure, false);
        }
    }

    public void invalidateAtomArray() {
        atomArrayValid = false;
    }

    public void updateAtomArray() {
        if (!atomArrayValid) {
            atoms.clear();
            atomMap.clear();
            Iterator e = coordSets.values().iterator();
            CoordSet coordSet;
            while (e.hasNext()) {
                coordSet = (CoordSet) e.next();
                Iterator entIterator = coordSet.getEntities().values().iterator();
                while (entIterator.hasNext()) {
                    Entity entity = (Entity) entIterator.next();
                    for (Atom atom : entity.atoms) {
                        atoms.add(atom);
                        atomMap.put(atom.getFullName(), atom);
                    }
                }
            }
        }
    }

    public void updateBondArray() {
        bonds.clear();
        Iterator e = coordSets.values().iterator();
        CoordSet coordSet;
        while (e.hasNext()) {
            coordSet = (CoordSet) e.next();
            Iterator entIterator = coordSet.getEntities().values().iterator();
            while (entIterator.hasNext()) {
                Entity entity = (Entity) entIterator.next();
                for (Bond bond : entity.bonds) {
                    bonds.add(bond);
                }
            }
        }
    }

    public ArrayList<Bond> getBondList() {
        return new ArrayList<Bond>(bonds);
    }

    public ArrayList<Atom> getAtomList() {
        return new ArrayList<Atom>(atoms);
    }

    public int genCoords() throws RuntimeException {
        return genCoordsFast();
    }

    public int genCoords(int iStructure, boolean fillCoords) throws RuntimeException {
        return genCoords(iStructure, fillCoords, null);
    }

    public int genCoords(boolean fillCoords) throws RuntimeException {
        return genCoords(0, fillCoords, null);
    }

    public int genCoords(int iStructure, boolean fillCoords, final double[] dihedralAngles) throws RuntimeException {
        if (!fillCoords) {
            nullCoords();
        }
        updateAtomArray();
        Atom a1 = null;
        Atom a2 = null;
        Atom a4 = null;
        int nAngles = 0;
        for (Atom a3 : atoms) {
            if (!a3.getPointValidity(iStructure)) {
                if (fillCoords) {
                    continue;
                }
                a3.setPointValidity(iStructure, true);
            }
            Coordinates coords = null;
            double dihedralAngle = 0;
            for (int iBond = 0; iBond < a3.bonds.size(); iBond++) {
                Bond bond = (Bond) a3.bonds.elementAt(iBond);
                if (bond.begin == a3) {
                    a4 = bond.end;
                } else {
                    a4 = bond.begin;
                }
                if (a4 == null) {
                    continue;
                }
                if (a4 == a3.parent) {
                    continue;
                }
                if (a4.parent != a3) {
                    continue;
                }
                if (coords == null) {
                    Point3 p1 = null;
                    Point3 p2 = null;
                    Point3 p3 = null;
                    a2 = a3.parent;
                    if (a2 == null) {
                        p1 = new Point3(-1, -1, 0);
                        p2 = new Point3(-1, 0, 0);
                    } else {
                        p2 = a2.getPoint(iStructure);
                        a1 = a2.parent;
                        if (a1 == null) {
                            p1 = new Point3(-1, 0, 0);
                        } else {
                            p1 = a1.getPoint(iStructure);
                        }
                    }
                    p3 = a3.getPoint(iStructure);
                    if (p1 == null) {
                        if (fillCoords) {
                            continue;
                        }
                        p1 = new Point3(-1, -1, 0);
                    }
                    if (p2 == null) {
                        if (fillCoords) {
                            continue;
                        }
                        p2 = new Point3(-1, 0, 0);
                    }
                    if (p3 == null) {
                        if (fillCoords) {
                            continue;
                        }
                        p3 = new Point3(0, 0, 0);
                    }
                    if ((p3.getX() == p2.getX()) && (p3.getY() == p2.getY()) && (p3.getZ() == p2.getZ())) {
                        throw new RuntimeException("genCoords: coordinates the same for " + a3.getFullName() + " " + p3.toString());
                    }
                    if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()) && (p1.getZ() == p2.getZ())) {
                        throw new RuntimeException("genCoords: coordinates the same for " + a2.getFullName() + " " + p2.toString());
                    }
                    coords = new Coordinates(p1, p2, p3);
                    coords.setup();
                }
                if (dihedralAngles == null) {
                    dihedralAngle += a4.dihedralAngle;
                } else {
                    dihedralAngle += dihedralAngles[nAngles];
                }
                nAngles++;
                if (!a4.getPointValidity(iStructure)) {
                    a4.setPointValidity(iStructure, true);
                    double bondLength = a4.bondLength;
                    double valanceAngle = a4.valanceAngle;
                    double bndsin = bondLength * Math.sin(Math.PI - valanceAngle);
                    double bndcos = bondLength * Math.cos(Math.PI - valanceAngle);
                    Point3 p4 = coords.calculate(dihedralAngle, bndcos, bndsin);
                    a4.setPoint(iStructure, p4);
                }
            }
            coords = null;
        }

        structures.add(Integer.valueOf(iStructure));
        resetActiveStructures();
        updateVecCoords();
        return nAngles;
    }

    public int genCoords(boolean fillCoords, final double[] dihedralAngles) throws RuntimeException {
        if (!fillCoords) {
            nullCoords();
        }
        updateAtomArray();
        Atom a1 = null;
        Atom a2 = null;
        Atom a4 = null;
        int nAngles = 0;
        for (Atom a3 : atoms) {
            if (!a3.getPointValidity()) {
                if (fillCoords) {
                    continue;
                }
                a3.setPointValidity(true);
            }
            Coordinates coords = null;
            double dihedralAngle = 0;
            for (int iBond = 0; iBond < a3.bonds.size(); iBond++) {
                Bond bond = (Bond) a3.bonds.elementAt(iBond);
                if (bond.begin == a3) {
                    a4 = bond.end;
                } else {
                    a4 = bond.begin;
                }
                if (a4 == null) {
                    continue;
                }
                if (a4 == a3.parent) {
                    continue;
                }
                if (a4.parent != a3) {
                    continue;
                }
                if (coords == null) {
                    Point3 p1 = null;
                    Point3 p2 = null;
                    Point3 p3 = null;
                    a2 = a3.parent;
                    if (a2 == null) {
                        p1 = new Point3(-1, -1, 0);
                        p2 = new Point3(-1, 0, 0);
                    } else {
                        p2 = a2.getPoint();
                        a1 = a2.parent;
                        if (a1 == null) {
                            p1 = new Point3(-1, 0, 0);
                        } else {
                            p1 = a1.getPoint();
                        }
                    }
                    p3 = a3.getPoint();
                    if (p1 == null) {
                        if (fillCoords) {
                            continue;
                        }
                        p1 = new Point3(-1, -1, 0);
                    }
                    if (p2 == null) {
                        if (fillCoords) {
                            continue;
                        }
                        p2 = new Point3(-1, 0, 0);
                    }
                    if (p3 == null) {
                        if (fillCoords) {
                            continue;
                        }
                        p3 = new Point3(0, 0, 0);
                    }
                    if ((p3.getX() == p2.getX()) && (p3.getY() == p2.getY()) && (p3.getZ() == p2.getZ())) {
                        throw new RuntimeException("genCoords: coordinates the same for " + a3.getFullName() + " " + p3.toString());
                    }
                    if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()) && (p1.getZ() == p2.getZ())) {
                        throw new RuntimeException("genCoords: coordinates the same for " + a2.getFullName() + " " + p2.toString());
                    }
                    coords = new Coordinates(p1, p2, p3);
                    coords.setup();
                }
                if (dihedralAngles == null) {
                    dihedralAngle += a4.dihedralAngle;
                } else {
                    dihedralAngle += dihedralAngles[nAngles];
                }
                nAngles++;
                if (!a4.getPointValidity()) {
                    a4.setPointValidity(true);
                    double bondLength = a4.bondLength;
                    double valanceAngle = a4.valanceAngle;
                    double bndsin = bondLength * FastMath.sin(Math.PI - valanceAngle);
                    double bndcos = bondLength * FastMath.cos(Math.PI - valanceAngle);
                    Point3 p4 = coords.calculate(dihedralAngle, bndcos, bndsin);
                    a4.setPoint(p4);
                }
            }
            coords = null;
        }

        structures.add(Integer.valueOf(0));
        resetActiveStructures();
        updateVecCoords();

        return nAngles;
    }

    public void setupGenCoords() throws RuntimeException {
        nullCoords();
        updateAtomArray();
        int nAngles = 0;
        spSets = new SpatialSet[atoms.size()][];
        int iAtom = 0;
        ArrayList<Atom> daughterList = new ArrayList<>();
        for (Atom a3 : atoms) {
            if (!a3.getPointValidity()) {
                a3.setPointValidity(true);
            }
            double bondLength = a3.bondLength;
            double valanceAngle = a3.valanceAngle;
            a3.bndSin = (float) (bondLength * FastMath.sin(Math.PI - valanceAngle));
            a3.bndCos = (float) (bondLength * FastMath.cos(Math.PI - valanceAngle));

            Coordinates coords = null;
            double dihedralAngle = 0;
            Atom a4;
            daughterList.clear();
            for (int iBond = 0; iBond < a3.bonds.size(); iBond++) {
                Bond bond = (Bond) a3.bonds.elementAt(iBond);
                if (bond.begin == a3) {
                    a4 = bond.end;
                } else {
                    a4 = bond.begin;
                }
                if (a4 == null) {
                    continue;
                }
                if (a4 == a3.parent) {
                    continue;
                }
                if (a4.parent != a3) {
                    continue;
                }
                daughterList.add(a4);
            }
            spSets[iAtom] = new SpatialSet[daughterList.size() + 3];
            spSets[iAtom][2] = a3.getSpatialSet();
            Atom a2 = a3.parent;
            if (a2 == null) {
                spSets[iAtom][1] = new SpatialSet(-1.0f, 0.0f, 0.0f);
                spSets[iAtom][0] = new SpatialSet(-1.0f, -1.0f, 0.0f);
            } else {
                spSets[iAtom][1] = a2.getSpatialSet();
                Atom a1 = a2.parent;
                if (a1 == null) {
                    spSets[iAtom][0] = new SpatialSet(-1.0f, -1.0f, 0.0f);
                } else {
                    spSets[iAtom][0] = a1.getSpatialSet();
                }
            }
            int j = 3;
            for (Atom atom : daughterList) {
                spSets[iAtom][j++] = atom.getSpatialSet();
            }
            iAtom++;
        }
    }

    public void setupGenCoordsFast() throws RuntimeException {
        nullCoords();
        updateAtomArray();
        genVecs = new int[atoms.size()][];
        int iAtom = 0;
        ArrayList<Atom> daughterList = new ArrayList<>();
        for (Atom a3 : atoms) {
            if (!a3.getPointValidity()) {
                a3.setPointValidity(true);
            }
            double bondLength = a3.bondLength;
            double valanceAngle = a3.valanceAngle;
            a3.bndSin = (float) (bondLength * FastMath.sin(Math.PI - valanceAngle));
            a3.bndCos = (float) (bondLength * FastMath.cos(Math.PI - valanceAngle));

            Atom a4;
            daughterList.clear();
            for (int iBond = 0; iBond < a3.bonds.size(); iBond++) {
                Bond bond = (Bond) a3.bonds.elementAt(iBond);
                if (bond.begin == a3) {
                    a4 = bond.end;
                } else {
                    a4 = bond.begin;
                }
                if (a4 == null) {
                    continue;
                }
                if (a4 == a3.parent) {
                    continue;
                }
                if (a4.parent != a3) {
                    continue;
                }
                if (!daughterList.contains(a4)) { // fixme test shouldn't be neccesary unless there are duplicate bonds
                    daughterList.add(a4);
                }
            }

            genVecs[iAtom] = new int[daughterList.size() + 3];
            genVecs[iAtom][2] = a3.iAtom;
            Atom a2 = a3.parent;
            if (a2 == null) {
                genVecs[iAtom][1] = -1;
                genVecs[iAtom][0] = -2;
            } else {
                genVecs[iAtom][1] = a2.iAtom;
                Atom a1 = a2.parent;
                if (a1 == null) {
                    genVecs[iAtom][0] = -2;  // fixme  should this be -1
                } else {
                    genVecs[iAtom][0] = a1.iAtom;
                }
            }
            int j = 3;
            for (Atom atom : daughterList) {
                genVecs[iAtom][j++] = atom.iAtom;
            }
            iAtom++;
        }
    }

    public void resetGenCoords() {
        spSets = null;
        genVecs = null;
    }

    public int genCoordsFast() {
        return genCoordsFast(null);
    }

    public int genCoordsFast(final double[] dihedralAngles) throws RuntimeException {
        int nAngles = 0;
        if (spSets == null) {
            setupGenCoords();
        }
        int iStructure = 0;
        for (Atom atom : atoms) {
            atom.setPointValidity(iStructure, false);
        }
        Atom a3 = spSets[0][2].atom;
        if (!a3.getPointValidity()) {
            a3.setPointValidity(true);
        }

        for (int i = 0; i < spSets.length; i++) {
            if (spSets[i].length > 3) {
                Coordinates coords = new Coordinates(spSets[i][0].getPoint(), spSets[i][1].getPoint(), spSets[i][2].getPoint());
                if (!coords.setup()) {
                    throw new RuntimeException("genCoords: coordinates the same for " + i + " " + spSets[i][2].getPoint().toString());
                }
                double dihedralAngle = 0;
                for (int j = 3; j < spSets[i].length; j++) {
                    Atom a4 = spSets[i][j].atom;
                    if (dihedralAngles == null) {
                        dihedralAngle += a4.dihedralAngle;
                    } else {
                        dihedralAngle += dihedralAngles[nAngles];
                    }
                    nAngles++;
                    if (!a4.getPointValidity()) {
                        a4.setPointValidity(true);
//                        double bondLength = a4.bondLength;
//                        double valanceAngle = a4.valanceAngle;
//                        double bndsin = bondLength * FastMath.sin(Math.PI - valanceAngle);
//                        double bndcos = bondLength * FastMath.cos(Math.PI - valanceAngle);
                        Point3 p4 = coords.calculate(dihedralAngle, a4.bndCos, a4.bndSin);
                        a4.setPoint(p4);
                    }
                }
            }

        }

        structures.add(0);
        resetActiveStructures();
        updateVecCoords();
        return nAngles;
    }

    public int genCoordsFastVec3D(final double[] dihedralAngles) throws RuntimeException {
        int nAngles = 0;
        if (false) {
            return genCoordsFast(dihedralAngles);
        }
        if (genVecs == null) {
            setupGenCoordsFast();
        }
        FastVector3D[] vecCoords = eCoords.getVecCoords();
        FastVector3D[] origins = new FastVector3D[3];
        origins[0] = new FastVector3D(-1.0, -1.0, 0.0);
        origins[1] = new FastVector3D(-1.0, 0.0, 0.0);
        origins[2] = new FastVector3D(0.0, 0.0, 0.0);

        for (int i = 0; i < genVecs.length; i++) {
            if (genVecs[i].length > 3) {
                FastVector3D v1;
                FastVector3D v2;
                FastVector3D v3;
                if (genVecs[i][0] < 0) {
                    v1 = origins[genVecs[i][0] + 2];
                } else {
                    v1 = vecCoords[genVecs[i][0]];
                }
                if (genVecs[i][1] < 0) {
                    v2 = origins[genVecs[i][1] + 2];
                } else {
                    v2 = vecCoords[genVecs[i][1]];
                }
                v3 = vecCoords[genVecs[i][2]];

                Coordinates3DF coords = new Coordinates3DF(v1, v2, v3);
                if (!coords.setup()) {
                    throw new RuntimeException("genCoords: coordinates the same for " + i + " " + genVecs[i][2]);
                }
                double dihedralAngle = 0;
                for (int j = 3; j < genVecs[i].length; j++) {
                    FastVector3D v4 = vecCoords[genVecs[i][j]];
                    Atom a4 = atoms.get(genVecs[i][j]);
                    if (dihedralAngles == null) {
                        dihedralAngle += a4.dihedralAngle;
                    } else {
                        dihedralAngle += dihedralAngles[nAngles];
                    }
                    nAngles++;
//                    FastVector3D v5 = new FastVector3D();
                    coords.calculate(dihedralAngle, a4.bndCos, a4.bndSin, v4);
//                    boolean compare = v4.compare(v5, 1.0e-5);
//                    Atom a3 = atoms.get(genVecs[i][2]);
//
//                    if (!v4.compare(v5, 1.0e-5)) {
//                        System.out.println(compare + " " + i + " " + j + " " + a3.getShortName() + " " + a4.getShortName() + " " + v4.toString() + " " + v5.toString());
//                    }
                }
            }

        }
//        updateFromVecCoords();

        structures.add(0);
        resetActiveStructures();
        return nAngles;
    }

    public EnergyCoords getEnergyCoords() {
        return eCoords;
    }

    public void updateVecCoords() {
        FastVector3D[] vecCoords = eCoords.getVecCoords(atoms.size());
        int i = 0;
        Entity lastEntity = null;
        int resNum = -1;
        getAtomTypes();
        for (Atom atom : atoms) {
            atom.iAtom = i;
            if (atom.entity != lastEntity) {
                lastEntity = atom.entity;
                resNum++;
            }
            Point3 pt = atom.getPoint();
            if (pt == null) {
                System.out.println("null pt " + atom.getName() + " " + (i - 1));
            } else {
                eCoords.setCoords(i, pt.getX(), pt.getY(), pt.getZ(), resNum, atom);
            }
            i++;
        }
    }

    public void updateFromVecCoords() {
        FastVector3D[] vecCoords = eCoords.getVecCoords();
        int i = 0;
        for (Atom atom : atoms) {
            atom.iAtom = i;
            Point3 pt = atom.getPoint();
            if (pt == null) {
                System.out.println("null pt " + atom.getName() + " " + (i - 1));
            } else {
                FastVector3D fVec = vecCoords[i++];
                if (fVec == null) {
                    System.out.println("null vec " + atom.getName() + " " + (i - 1));
                } else {
                    Point3 newPt = new Point3(fVec.getEntry(0), fVec.getEntry(1), fVec.getEntry(2));
                    atom.setPoint(newPt);
                }

            }
        }
    }

    public void getAngles(final double[] dihedralAngles) {
        Atom a1 = null;
        Atom a2 = null;
        Atom a4 = null;
        int nAngles = 0;
        updateAtomArray();

        for (Atom a3 : atoms) {
            for (int iBond = 0; iBond < a3.bonds.size(); iBond++) {
                Bond bond = (Bond) a3.bonds.elementAt(iBond);
                if (bond.begin == a3) {
                    a4 = bond.end;
                } else {
                    a4 = bond.begin;
                }
                if (a4 == null) {
                    continue;
                }
                if (a4 == a3.parent) {
                    continue;
                }
                if (a4.parent != a3) {
                    continue;
                }
                if (dihedralAngles == null) {
                    dihedralAngles[nAngles] = a4.dihedralAngle;
                }
                nAngles++;
            }
        }
    }

    public ArrayList<Atom> getAttachedHydrogens(Atom atom) {
        ArrayList<Atom> hydrogens = new ArrayList<Atom>();
        for (int iBond = 0; iBond < atom.bonds.size(); iBond++) {
            Bond bond = (Bond) atom.bonds.elementAt(iBond);
            Atom checkAtom;
            if (bond.begin == atom) {
                checkAtom = bond.end;
            } else {
                checkAtom = bond.begin;
            }
            System.err.println(atom.getName() + " " + checkAtom.getName() + " " + checkAtom.getAtomicNumber());
            if (checkAtom.getAtomicNumber() == 1) {
                hydrogens.add(checkAtom);
            }
        }
        return hydrogens;
    }

    public int getPPMSetCount() {
        Iterator e = getSpatialSetIterator();
        int maxCount = 1;
        while (e.hasNext()) {
            SpatialSet spatialSet = (SpatialSet) e.next();
            if (spatialSet == null) {
                continue;
            }
            int nSets = spatialSet.getPPMSetCount();
            if (nSets > maxCount) {
                maxCount = nSets;
            }
        }
        return maxCount;
    }

    public static int selectResidues() {
        Vector selected = new Vector(256);
        TreeSet completedResidues = new TreeSet();

        for (int i = 0; i < Molecule.globalSelected.size(); i++) {
            SpatialSet spatialSet = (SpatialSet) Molecule.globalSelected.elementAt(i);

            if (spatialSet.selected != 1) {
                continue;
            }

            Compound compound = (Compound) spatialSet.atom.entity;

            if (completedResidues.contains(compound.number
                    + spatialSet.getName())) {
                continue;
            } else {
                completedResidues.add(compound.number + spatialSet.getName());
            }

            for (Atom atom : compound.atoms) {
                SpatialSet spatialSet2 = atom.getSpatialSet();

                if (spatialSet2 != null) {
                    selected.addElement(spatialSet2);
                }
            }
        }

        int nSelected = setSelected(selected, false, false);
        return nSelected;
    }

    public static int selectAtoms(String selectionString) throws InvalidMoleculeException {
        return selectAtoms(selectionString, false, false);
    }

    public static int selectAtoms(String selectionString, boolean append, boolean inverse) throws InvalidMoleculeException {
        MolFilter molFilter = new MolFilter(selectionString);
        Vector selected = matchAtoms(molFilter);
        int nSelected = setSelected(selected, append, inverse);
        return nSelected;
    }

    public static int selectAtoms(MolFilter molFilter,
            boolean append, boolean inverse) throws InvalidMoleculeException {
        Vector selected = matchAtoms(molFilter);
        int nSelected = setSelected(selected, append, inverse);
        return nSelected;
    }

    public static int setSelected(Vector selected,
            boolean append, boolean inverse) {
        int i;
        int j;
        Atom atom;
        SpatialSet spatialSet = null;

        if (!append) {
            for (i = 0; i < Molecule.globalSelected.size(); i++) {
                spatialSet = (SpatialSet) Molecule.globalSelected.elementAt(i);

                if (spatialSet != null) {
                    spatialSet.setSelected(0);
                }
            }
        }

        if (selected == null) {
            Molecule.globalSelected.setSize(0);

            return 0;
        }

        if (inverse) {
            if (Molecule.atomList == null) {
                Molecule.makeAtomList();
            }
            for (i = 0; i < Molecule.atomList.size(); i++) {
                atom = (Atom) Molecule.atomList.elementAt(i);
                atom.spatialSet.setSelected(1);
            }

            for (i = 0; i < selected.size(); i++) {
                spatialSet = (SpatialSet) selected.elementAt(i);
                spatialSet.setSelected(0);
            }

            Molecule.globalSelected.setSize(0);

            for (i = 0; i < Molecule.atomList.size(); i++) {
                atom = (Atom) Molecule.atomList.elementAt(i);
                spatialSet = atom.spatialSet;
                if (spatialSet.getSelected() > 0) {
                    Molecule.globalSelected.addElement(spatialSet);
                }
            }

            return (Molecule.globalSelected.size());
        } else {
            if (append) {
                j = Molecule.globalSelected.size();
            } else {
                j = 0;
            }

            Molecule.globalSelected.setSize(j + selected.size());

            for (i = 0; i < selected.size(); i++) {
                spatialSet = (SpatialSet) selected.elementAt(i);

                if (spatialSet != null) {
                    spatialSet.setSelected(spatialSet.getSelected() + 1);
                    Molecule.globalSelected.setElementAt(spatialSet, j++);
                }
            }

            Molecule.globalSelected.setSize(j);

            return (j);
        }
    }

    public static int selectBonds(String mode) {
        Vector selected = matchBonds();
        int i;
        Bond bond;

        for (i = 0; i < Molecule.bselected.size(); i++) {
            bond = (Bond) Molecule.bselected.elementAt(i);
            bond.unsetProperty(Atom.SELECT);
        }

        Molecule.bselected.setSize(selected.size());

        for (i = 0; i < selected.size(); i++) {
            bond = (Bond) selected.elementAt(i);
            bond.setProperty(Atom.SELECT);
            Molecule.bselected.setElementAt(bond, i);
        }

        return selected.size();
    }

    public static ArrayList<String> listAtoms() {
        int i;
        SpatialSet spatialSet;
        ArrayList<String> list = new ArrayList<>();

        for (i = 0; i < globalSelected.size(); i++) {
            spatialSet = (SpatialSet) globalSelected.elementAt(i);
            list.add(spatialSet.getFullName());
        }
        return list;
    }

    public static void unSelectLastAtom() {
        SpatialSet spatialSet;

        if (globalSelected.size() == 0) {
            return;
        }

        spatialSet = (SpatialSet) globalSelected.lastElement();

        if ((spatialSet != null) && (spatialSet.getSelected() > 0)) {
            spatialSet.setSelected(spatialSet.getSelected() - 1);
        }

        globalSelected.removeElementAt(globalSelected.size() - 1);
    }

    public static void makeSite(String siteName) throws IllegalArgumentException {
        if ((siteName == null) || (siteName.trim().equals(""))) {
            throw new IllegalArgumentException("makeSite: null or blank siteName");
        }

        Vector siteList = new Vector(globalSelected);
        sites.put(siteName, siteList);
    }

    public static int selectSite(String siteName)
            throws IllegalArgumentException {
        if ((siteName == null) || (siteName.trim().equals(""))) {
            throw new IllegalArgumentException("selectSite: null or blank siteName");
        }

        Vector siteList = (Vector) sites.get(siteName);

        if (siteList == null) {
            throw new IllegalArgumentException(
                    "selectSite: siteList \"" + siteName + "\" doesnt't exist");
        }

        int nSelected = setSelected(siteList, false, false);
        return nSelected;
    }

    public static void withinSite(String siteName,
            float tolerance) throws IllegalArgumentException {
        if ((siteName == null) || (siteName.trim().equals(""))) {
            throw new IllegalArgumentException("selectSite: null or blank siteName");
        }

        Vector siteList = (Vector) sites.get(siteName);

        if (siteList == null) {
            throw new IllegalArgumentException(
                    "withinSite: siteList \"" + siteName + "\" doesnt't exist");
        }

        Vector hitList = new Vector(128);

        for (int i = 0; i < globalSelected.size(); i++) {
            SpatialSet s1 = (SpatialSet) globalSelected.elementAt(i);

            if (s1.getSelected() != 1) {
                continue;
            }

            Point3 pt1 = s1.getPoint();

            if (pt1 == null) {
                continue;
            }

            // fixme  should get array of coords once
            for (int j = 0; j < siteList.size(); j++) {
                SpatialSet s2 = (SpatialSet) siteList.elementAt(j);
                Point3 pt2 = s2.getPoint();

                if (pt2 == null) {
                    continue;
                }

                double distance = Atom.calcDistance(pt1, pt2);

                if (distance < tolerance) {
                    hitList.addElement(s1);

                    break;
                }
            }
        }

        setSelected(hitList, false, false);
    }

    public List<Object> listBonds() {
        int i;
        Atom atomB;
        Atom atomE;
        Bond bond;
        List<Object> list = new ArrayList<>();

        for (i = 0; i < bselected.size(); i++) {
            bond = (Bond) bselected.elementAt(i);
            atomB = bond.begin;
            atomE = bond.end;
            if ((atomB != null) && (atomE != null)) {
                list.add(atomB.spatialSet.getFullName());
                list.add(atomE.spatialSet.getFullName());
                list.add(bond.order);
                int stereo = bond.stereo;
                list.add(stereo);
            }
        }
        return list;
    }

    public static void setAtomProperty(int property, boolean state) {
        SpatialSet spatialSet;

        for (int i = 0; i < globalSelected.size(); i++) {
            spatialSet = (SpatialSet) globalSelected.elementAt(i);

            if (spatialSet.getSelected() == 1) {
                if (state) {
                    spatialSet.setProperty(property);
                } else {
                    spatialSet.unsetProperty(property);
                }
            }
        }
    }

    public void setBondProperty(int property, boolean state) {
        Bond bond;

        for (int i = 0; i < bselected.size(); i++) {
            bond = (Bond) bselected.elementAt(i);

            if (state) {
                bond.setProperty(Bond.DISPLAY);
            } else {
                bond.unsetProperty(Bond.DISPLAY);
            }
        }
    }

    public void setProperty(String propName, String propValue) {
        propertyMap.put(propName, propValue);
    }

    public String getProperty(String propName) {
        return propertyMap.get(propName);
    }

    public static void colorAtoms(float red, float green,
            float blue) {
        Atom atom;

        for (int i = 0; i < globalSelected.size(); i++) {
            atom = ((SpatialSet) globalSelected.elementAt(i)).atom;
            atom.setColor(red, green, blue);
        }
    }

    public static void colorAtomsByType() {
        Atom atom;

        for (int i = 0; i < globalSelected.size(); i++) {
            atom = ((SpatialSet) globalSelected.elementAt(i)).atom;
            atom.setColorByType();
        }
    }

    public static void colorBonds(float red, float green,
            float blue) {
        Bond bond;

        for (int i = 0; i < bselected.size(); i++) {
            bond = (Bond) bselected.elementAt(i);
            bond.red = red;
            bond.green = green;
            bond.blue = blue;
        }
    }

    public static void radiusAtoms(float radius) {
        Atom atom;

        for (int i = 0; i < globalSelected.size(); i++) {
            atom = ((SpatialSet) globalSelected.elementAt(i)).atom;
            atom.radius = radius;
        }
    }

    public static void radiusBonds(float radius) {
        Bond bond;

        for (int i = 0; i < globalSelected.size(); i++) {
            bond = (Bond) bselected.elementAt(i);
            bond.radius = radius;
        }
    }

    public int createLineArray(int iStructure, float[] coords, int i,
            float[] colors) {
        int j;
        Atom atomB;
        Atom atomE;
        Point3 ptB;
        Point3 ptE;
        updateBondArray();
        for (Bond bond : bonds) {
            if (bond.getProperty(Bond.DISPLAY)) {
                atomB = bond.begin;
                atomE = bond.end;

                if ((atomB != null) && (atomE != null)) {

                    SpatialSet spatialSet = atomB.spatialSet;
                    ptB = atomB.getPoint(iStructure);
                    ptE = atomE.getPoint(iStructure);

                    if ((ptB != null) && (ptE != null)) {
                        j = i;

                        double dx = ptE.getX() - ptB.getX();
                        double dy = ptE.getY() - ptB.getY();
                        double dz = ptE.getZ() - ptB.getZ();
                        double x3 = -dy / bondSpace;
                        double y3 = dx / bondSpace;
                        double z3 = dz / bondSpace;

                        if (((bond.stereo == Bond.STEREO_BOND_UP)
                                && (atomE.nonHydrogens < atomB.nonHydrogens))
                                || ((bond.stereo == Bond.STEREO_BOND_DOWN)
                                && (atomE.nonHydrogens > atomB.nonHydrogens))) {
                            coords[i++] = (float) (ptB.getX());
                            coords[i++] = (float) (ptB.getY());
                            coords[i++] = (float) (ptB.getZ());
                            coords[i++] = (float) (ptE.getX() + x3);
                            coords[i++] = (float) (ptE.getY() + y3);
                            coords[i++] = (float) (ptE.getZ() + z3);

                            coords[i++] = (float) (ptB.getX());
                            coords[i++] = (float) (ptB.getY());
                            coords[i++] = (float) (ptB.getZ());
                            coords[i++] = (float) (ptE.getX() - x3);
                            coords[i++] = (float) (ptE.getY() - y3);
                            coords[i++] = (float) (ptE.getZ() - z3);

                            coords[i++] = (float) (ptE.getX() - x3);
                            coords[i++] = (float) (ptE.getY() - y3);
                            coords[i++] = (float) (ptE.getZ() - z3);
                            coords[i++] = (float) (ptE.getX() + x3);
                            coords[i++] = (float) (ptE.getY() + y3);
                            coords[i++] = (float) (ptE.getZ() + z3);

                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                        } else if (((bond.stereo == Bond.STEREO_BOND_DOWN)
                                && (atomE.nonHydrogens < atomB.nonHydrogens))
                                || ((bond.stereo == Bond.STEREO_BOND_UP)
                                && (atomE.nonHydrogens > atomB.nonHydrogens))) {
                            coords[i++] = (float) (ptB.getX() + x3);
                            coords[i++] = (float) (ptB.getY() + y3);
                            coords[i++] = (float) (ptB.getZ() + z3);
                            coords[i++] = (float) (ptB.getX() - x3);
                            coords[i++] = (float) (ptB.getY() - y3);
                            coords[i++] = (float) (ptB.getZ() - z3);

                            coords[i++] = (float) (ptB.getX() + x3
                                    + (dx / 5));
                            coords[i++] = (float) (ptB.getY() + y3
                                    + (dy / 5));
                            coords[i++] = (float) (ptB.getZ() + z3
                                    + (dz / 5));
                            coords[i++] = (float) (ptB.getX() - x3
                                    + (dx / 5));
                            coords[i++] = (float) (ptB.getY() - y3
                                    + (dy / 5));
                            coords[i++] = (float) (ptB.getZ() - z3
                                    + (dz / 5));

                            coords[i++] = (float) (ptB.getX() + x3
                                    + (dx / 5 * 2));
                            coords[i++] = (float) (ptB.getY() + y3
                                    + (dy / 5 * 2));
                            coords[i++] = (float) (ptB.getZ() + z3
                                    + (dz / 5 * 2));
                            coords[i++] = (float) (ptB.getX() - x3
                                    + (dx / 5 * 2));
                            coords[i++] = (float) (ptB.getY() - y3
                                    + (dy / 5 * 2));
                            coords[i++] = (float) (ptB.getZ() - z3
                                    + (dz / 5 * 2));

                            coords[i++] = (float) (ptB.getX() + x3
                                    + (dx / 5 * 3));
                            coords[i++] = (float) (ptB.getY() + y3
                                    + (dy / 5 * 3));
                            coords[i++] = (float) (ptB.getZ() + z3
                                    + (dz / 5 * 3));
                            coords[i++] = (float) (ptB.getX() - x3
                                    + (dx / 5 * 3));
                            coords[i++] = (float) (ptB.getY() - y3
                                    + (dy / 5 * 3));
                            coords[i++] = (float) (ptB.getZ() - z3
                                    + (dz / 5 * 3));

                            coords[i++] = (float) (ptB.getX() + x3
                                    + (dx / 5 * 4));
                            coords[i++] = (float) (ptB.getY() + y3
                                    + (dy / 5 * 4));
                            coords[i++] = (float) (ptB.getZ() + z3
                                    + (dz / 5 * 4));
                            coords[i++] = (float) (ptB.getX() - x3
                                    + (dx / 5 * 4));
                            coords[i++] = (float) (ptB.getY() - y3
                                    + (dy / 5 * 4));
                            coords[i++] = (float) (ptB.getZ() - z3
                                    + (dz / 5 * 4));

                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                            colors[j++] = atomB.getRed();
                            colors[j++] = atomB.getGreen();
                            colors[j++] = atomB.getBlue();
                            colors[j++] = atomE.getRed();
                            colors[j++] = atomE.getGreen();
                            colors[j++] = atomE.getBlue();
                        } else {
                            if ((bond.order == 1) || (bond.order == 3)
                                    || (bond.order == 7)
                                    || (bond.order == 9)) {
                                atomB.setProperty(Atom.LABEL);
                                atomE.setProperty(Atom.LABEL);

                                coords[i++] = (float) ptB.getX();
                                coords[i++] = (float) ptB.getY();
                                coords[i++] = (float) ptB.getZ();
                                coords[i++] = (float) ptE.getX();
                                coords[i++] = (float) ptE.getY();
                                coords[i++] = (float) ptE.getZ();
                                colors[j++] = atomB.getRed();
                                colors[j++] = atomB.getGreen();
                                colors[j++] = atomB.getBlue();
                                colors[j++] = atomE.getRed();
                                colors[j++] = atomE.getGreen();
                                colors[j++] = atomE.getBlue();
                            }

                            if ((bond.order == 2) || (bond.order == 3)
                                    || (bond.order == 8)) {
                                coords[i++] = (float) (ptB.getX() + x3);
                                coords[i++] = (float) (ptB.getY() + y3);
                                coords[i++] = (float) (ptB.getZ() + z3);
                                coords[i++] = (float) (ptE.getX() + x3);
                                coords[i++] = (float) (ptE.getY() + y3);
                                coords[i++] = (float) (ptE.getZ() + z3);

                                coords[i++] = (float) (ptB.getX() - x3);
                                coords[i++] = (float) (ptB.getY() - y3);
                                coords[i++] = (float) (ptB.getZ() - z3);
                                coords[i++] = (float) (ptE.getX() - x3);
                                coords[i++] = (float) (ptE.getY() - y3);
                                coords[i++] = (float) (ptE.getZ() - z3);

                                colors[j++] = atomB.getRed();
                                colors[j++] = atomB.getGreen();
                                colors[j++] = atomB.getBlue();
                                colors[j++] = atomE.getRed();
                                colors[j++] = atomE.getGreen();
                                colors[j++] = atomE.getBlue();
                                colors[j++] = atomB.getRed();
                                colors[j++] = atomB.getGreen();
                                colors[j++] = atomB.getBlue();
                                colors[j++] = atomE.getRed();
                                colors[j++] = atomE.getGreen();
                                colors[j++] = atomE.getBlue();
                            }
                        }
                    }
                }

            }
        }

        return i;
    }

    public int getBonds(int iStructure, Bond[] bondArray) {
        int k = 0;
        updateBondArray();
        for (Bond bond : bonds) {
            if (bond.getProperty(Bond.DISPLAY)) {
                Atom atomB = bond.begin;
                Atom atomE = bond.end;

                if ((atomB != null) && (atomE != null)) {

                    SpatialSet spatialSet = atomB.spatialSet;
                    Point3 ptB = atomB.getPoint(iStructure);
                    Point3 ptE = atomE.getPoint(iStructure);

                    if ((ptB != null) && (ptE != null)) {

                        if (((bond.stereo == Bond.STEREO_BOND_UP)
                                && (atomE.nonHydrogens < atomB.nonHydrogens))
                                || ((bond.stereo == Bond.STEREO_BOND_DOWN)
                                && (atomE.nonHydrogens > atomB.nonHydrogens))) {
                            bondArray[k++] = bond;
                            bondArray[k++] = bond;
                            bondArray[k++] = bond;

                        } else if (((bond.stereo == Bond.STEREO_BOND_DOWN)
                                && (atomE.nonHydrogens < atomB.nonHydrogens))
                                || ((bond.stereo == Bond.STEREO_BOND_UP)
                                && (atomE.nonHydrogens > atomB.nonHydrogens))) {
                            bondArray[k++] = bond;
                            bondArray[k++] = bond;
                            bondArray[k++] = bond;
                            bondArray[k++] = bond;
                            bondArray[k++] = bond;
                        } else {
                            if ((bond.order == 1) || (bond.order == 3)
                                    || (bond.order == 7)
                                    || (bond.order == 9)) {
                                atomB.setProperty(Atom.LABEL);
                                atomE.setProperty(Atom.LABEL);
                                bondArray[k++] = bond;

                            }

                            if ((bond.order == 2) || (bond.order == 3)
                                    || (bond.order == 8)) {
                                bondArray[k++] = bond;
                                bondArray[k++] = bond;
                            }
                        }
                    }
                }

            }
        }

        return k;
    }

    public int getLineCount(int iStructure) {
        int i = 0;
        Atom atomB;
        Atom atomE;
        Point3 ptB;
        Point3 ptE;
        updateBondArray();
        for (Bond bond : bonds) {
            if (bond.getProperty(Bond.DISPLAY)) {
                atomB = bond.begin;
                atomE = bond.end;

                if ((atomB != null) && (atomE != null)) {
                    ptB = atomB.getPoint(iStructure);
                    ptE = atomE.getPoint(iStructure);

                    if ((ptB != null) && (ptE != null)) {
                        if (((bond.stereo == Bond.STEREO_BOND_UP)
                                && (atomE.nonHydrogens < atomB.nonHydrogens))
                                || ((bond.stereo == Bond.STEREO_BOND_DOWN)
                                && (atomE.nonHydrogens > atomB.nonHydrogens))) {
                            i += 3;
                        } else if (((bond.stereo == Bond.STEREO_BOND_DOWN)
                                && (atomE.nonHydrogens < atomB.nonHydrogens))
                                || ((bond.stereo == Bond.STEREO_BOND_UP)
                                && (atomE.nonHydrogens > atomB.nonHydrogens))) {
                            i += 5;
                        } else if (bond.order < 4) {
                            i += bond.order;
                        } else if (bond.order == 8) {
                            i += 2;
                        } else {
                            i++;
                        }
                    }
                }
            }
        }

        return i;
    }

    public void calcCorner(int iStructure)
            throws MissingCoordinatesException {
        int n;
        double x = 0;
        double y = 0;
        double z = 0;
        Point3 pt;
        n = 0;
        updateAtomArray();
        for (Atom atom : atoms) {
            pt = atom.getPoint(iStructure);

            if (pt != null) {
                x += pt.getX();
                y += pt.getY();
                z += pt.getZ();
                n++;
            }

        }

        if (n == 0) {
            throw new MissingCoordinatesException("couldn't calculate center: no coordinates");
        }

        center[0] = x / n;
        center[1] = y / n;
        center[2] = z / n;
    }

    public void updateCenter(int iStructure) throws MissingCoordinatesException {
        center = getCenter(iStructure);
    }

    public double[] getCenter(int iStructure) throws MissingCoordinatesException {
        int n;
        double x = 0;
        double y = 0;
        double z = 0;
        Point3 pt;
        n = 0;

        updateAtomArray();
        for (Atom atom : atoms) {
            pt = atom.getPoint(iStructure);

            if (pt != null) {
                x += pt.getX();
                y += pt.getY();
                z += pt.getZ();
                n++;
            }
        }

        if (n == 0) {
            throw new MissingCoordinatesException("couldn't calculate center: no coordinates");
        }
        double[] mCenter = new double[3];
        mCenter[0] = x / n;
        mCenter[1] = y / n;
        mCenter[2] = z / n;
        return mCenter;
    }

    public double[] getCenterOfSelected(int iStructure)
            throws MissingCoordinatesException {
        double x = 0;
        double y = 0;
        double z = 0;
        int n = 0;
        for (int i = 0; i < globalSelected.size(); i++) {
            SpatialSet spatialSet = (SpatialSet) globalSelected.elementAt(i);
            Point3 pt = spatialSet.getPoint(iStructure);
            if (pt != null) {
                x += pt.getX();
                y += pt.getY();
                z += pt.getZ();
                n++;
            }

        }

        if (n == 0) {
            throw new MissingCoordinatesException("couldn't calculate center: no coordinates");
        }
        double[] mCenter = new double[3];
        mCenter[0] = x / n;
        mCenter[1] = y / n;
        mCenter[2] = z / n;
        return mCenter;
    }

    public Vector3D getCorner(int iStructure)
            throws MissingCoordinatesException {
        int n;
        Point3 pt;
        n = 0;
        double[] coords = new double[3];
        double[] corner = new double[3];
        double[] mCenter = getCenter(iStructure);
        for (Atom atom : atoms) {
            pt = atom.getPoint(iStructure);

            if (pt != null) {
                coords[0] = pt.getX();
                coords[1] = pt.getY();
                coords[2] = pt.getZ();
                for (int iC = 0; iC < 3; iC++) {
                    double delta = Math.abs(coords[iC] - mCenter[iC]);
                    if (delta > corner[iC]) {
                        corner[iC] = delta;
                    }
                }
                n++;
            }
        }

        if (n == 0) {
            throw new MissingCoordinatesException("couldn't calculate center: no coordinates");
        }
        return new Vector3D(corner[0], corner[1], corner[2]);
    }

    public void center(int iStructure) {
        Point3 pt;
        Point3 cPt = new Point3(center[0], center[1], center[2]);
        updateAtomArray();
        for (Atom atom : atoms) {
            pt = atom.getPoint(iStructure);

            if (pt != null) {
                Vector3D vpt = pt.subtract(cPt);
                atom.setPoint(iStructure, new Point3(vpt));
            }
        }
    }

    public void centerStructure(int iStructure) throws MissingCoordinatesException {
        Point3 pt;
        double[] mcenter = getCenter(iStructure);
        Point3 cPt = new Point3(mcenter[0], mcenter[1], mcenter[2]);
        updateAtomArray();
        for (Atom atom : atoms) {
            pt = atom.getPoint(iStructure);

            if (pt != null) {
                Vector3D vpt = pt.subtract(cPt);
                atom.setPoint(iStructure, new Point3(vpt));
            }
        }
    }

    public List<String> listAtomsWithProperty(int property) {

        List<String> list = new ArrayList<>();
        updateAtomArray();
        for (Atom atom : atoms) {
            if (atom.getProperty(property)) {
                SpatialSet spatialSet = atom.getSpatialSet();
                list.add(spatialSet.getFullName());
            }
        }

        return list;
    }

    public int createSphereArray(int iStructure, float[] coords, int i,
            float[] colors, float[] values) {
        int j;
        int k = 0;
        Point3 pt;
        updateAtomArray();
        for (Atom atom : atoms) {
            atom.unsetProperty(Atom.LABEL);

            if (atom.getProperty(Atom.DISPLAY)) {
                pt = atom.getPoint(iStructure);

                if (pt != null) {
                    atom.setProperty(Atom.LABEL);
                    j = i;
                    coords[i++] = (float) pt.getX();
                    coords[i++] = (float) pt.getY();
                    coords[i++] = (float) pt.getZ();
                    colors[j++] = atom.getRed();
                    colors[j++] = atom.getGreen();
                    colors[j++] = atom.getBlue();
                    values[k++] = atom.value;
                }
            }
        }

        return i;
    }

    public int createLabelArray(int iStructure, float[] coords, int i) {
        Point3 pt;
        Iterator e = coordSets.values().iterator();
        CoordSet coordSet;
        updateAtomArray();
        for (Atom atom : atoms) {
            if (atom.getProperty(Atom.LABEL)) {
                pt = atom.getPoint(iStructure);

                if (pt != null) {
                    coords[i++] = (float) pt.getX();
                    coords[i++] = (float) pt.getY();
                    coords[i++] = (float) pt.getZ();
                }
            }
        }

        return i;
    }

    public int createSelectionArray(int iStructure, float[] coords, int[] levels) {
        int i;
        int j;
        Point3 ptB = null;
        Point3 ptE = null;
        SpatialSet spatialSet1 = null;
        SpatialSet spatialSet2 = null;

        int n = globalSelected.size();
        j = 0;
        i = 0;

        for (int k = 0; k < n; k++) {
            spatialSet1 = (SpatialSet) globalSelected.elementAt(k);

            int selected = spatialSet1.getSelected();

            if (selected > 0) {
                ptB = spatialSet1.getPoint(iStructure);

                if (ptB != null) {
                    if ((k + 1) < n) {
                        spatialSet2 = (SpatialSet) globalSelected.elementAt(k
                                + 1);
                    }

                    if ((spatialSet1 == spatialSet2)
                            || (Molecule.selCycleCount == 0) || ((k + 1) >= n)
                            || ((Molecule.selCycleCount != 1)
                            && (((k + 1) % Molecule.selCycleCount) == 0))) {
                        coords[i++] = (float) ptB.getX();
                        coords[i++] = (float) ptB.getY();
                        coords[i++] = (float) ptB.getZ();
                        coords[i++] = (float) ptB.getX() + 0.2f;
                        coords[i++] = (float) ptB.getY() - 0.2f;
                        coords[i++] = (float) ptB.getZ();
                        coords[i++] = (float) ptB.getX() - 0.2f;
                        coords[i++] = (float) ptB.getY() - 0.2f;
                        coords[i++] = (float) ptB.getZ();
                        coords[i++] = (float) ptB.getX();
                        coords[i++] = (float) ptB.getY();
                        coords[i++] = (float) ptB.getZ();
                        coords[i++] = (float) ptB.getX() + 0.2f;
                        coords[i++] = (float) ptB.getY() - 0.2f;
                        coords[i++] = (float) ptB.getZ();
                        coords[i++] = (float) ptB.getX() - 0.2f;
                        coords[i++] = (float) ptB.getY() - 0.2f;
                        coords[i++] = (float) ptB.getZ();
                        levels[j++] = selected;
                    } else {
                        ptE = spatialSet2.getPoint(iStructure);

                        if (ptE != null) {
                            float dx = (float) (ptE.getX() - ptB.getX());
                            float dy = (float) (ptE.getY() - ptB.getY());
                            float dz = (float) (ptE.getZ() - ptB.getZ());
                            float len = (float) Math.sqrt((dx * dx)
                                    + (dy * dy) + (dz * dz));
                            float xy3 = -dy / len * 0.2f;
                            float yx3 = dx / len * 0.2f;
                            float z3 = dz / len * 0.2f;
                            float xz3 = -dz / len * 0.2f;
                            float y3 = dy / len * 0.2f;
                            float zx3 = dx / len * 0.2f;
                            coords[i++] = (float) (ptB.getX() - xy3);
                            coords[i++] = (float) (ptB.getY() - yx3);
                            coords[i++] = (float) (ptB.getZ() - z3);
                            coords[i++] = (float) (ptB.getX() + xy3);
                            coords[i++] = (float) (ptB.getY() + yx3);
                            coords[i++] = (float) (ptB.getZ() + z3);
                            coords[i++] = (float) ptB.getX() + (dx / len * 0.5f);
                            coords[i++] = (float) ptB.getY() + (dy / len * 0.5f);
                            coords[i++] = (float) ptB.getZ() + (dz / len * 0.5f);
                            coords[i++] = (float) (ptB.getX() + xz3);
                            coords[i++] = (float) (ptB.getY() + y3);
                            coords[i++] = (float) (ptB.getZ() + zx3);
                            coords[i++] = (float) (ptB.getX() - xz3);
                            coords[i++] = (float) (ptB.getY() - y3);
                            coords[i++] = (float) (ptB.getZ() - zx3);
                            coords[i++] = (float) ptB.getX() + (dx / len * 0.5f);
                            coords[i++] = (float) ptB.getY() + (dy / len * 0.5f);
                            coords[i++] = (float) ptB.getZ() + (dz / len * 0.5f);
                            levels[j++] = selected;
                        }
                    }
                }
            }
        }

        return i;
    }

    public int getSphereCount(int iStructure) {
        int i = 0;
        Point3 pt;
        updateAtomArray();
        for (Atom atom : atoms) {
            if (atom.getProperty(Atom.DISPLAY)) {
                pt = atom.getPoint(iStructure);

                if (pt != null) {
                    i++;
                }
            }
        }

        return i;
    }

    public int getLabelCount(int iStructure) {
        int i = 0;
        Point3 pt;
        updateAtomArray();
        for (Atom atom : atoms) {
            if (atom.getProperty(Atom.LABEL)) {
                pt = atom.getPoint(iStructure);

                if (pt != null) {
                    i++;
                }
            }
        }

        return i;
    }

    public int getAtoms(int iStructure, Atom[] atomArray) {
        Point3 pt;
        int i = 0;

        updateAtomArray();
        for (Atom atom : atoms) {
            if (atom.getProperty(Atom.DISPLAY)) {
                pt = atom.getPoint(iStructure);

                if (pt != null) {
                    atomArray[i] = atom;
                    i++;
                }
            }
        }

        return i;
    }

    public static Vector matchBonds() throws IllegalArgumentException {
        Vector selected = new Vector(32);
        Atom atomB;
        Atom atomE;
        String molName = Molecule.defaultMol;
        Molecule molecule = (Molecule) molecules.get(molName);

        if (molecule == null) {
            throw new IllegalArgumentException("Can't find molecule " + molName);
        }

        molecule.updateBondArray();
        for (Bond bond : molecule.bonds) {
            atomB = bond.begin;
            atomE = bond.end;

            if ((atomB != null) && (atomE != null)) {
                if ((atomB.getSelected() > 0)
                        && (atomE.getSelected() > 0)) {
                    bond.setProperty(Bond.SELECT);
                    selected.addElement(bond);
                }
            }
        }

        return (selected);
    }

    public Vector getMoleculeBonds() {

        Vector bondVector = new Vector(32);
        Atom atomB;
        Atom atomE;
        updateBondArray();
        for (Bond bond : bonds) {
            atomB = bond.begin;
            atomE = bond.end;

            if ((atomB != null) && (atomE != null)) {
                bondVector.addElement(bond);
            }
        }

        return (bondVector);
    }

    public void updateSpatialSets() {
    }

    public Vector getAtoms() {
        Vector atomVector = new Vector(32);
        updateAtomArray();
        for (Atom atom : atoms) {
            atomVector.addElement(atom);
        }

        return atomVector;
    }

    public ArrayList<Atom> getAtomArray() {
        updateAtomArray();
        return atoms;
    }

    public Vector getAtomsByProp(int property) {
        Vector selected = new Vector(32);
        updateAtomArray();
        for (Atom atom : atoms) {
            SpatialSet spatialSet = atom.getSpatialSet();

            if ((spatialSet != null) && spatialSet.getProperty(property)) {
                selected.addElement(spatialSet);
            }
        }

        return selected;
    }

    public void calcRMSD() {
        updateAtomArray();
        for (Atom atom : atoms) {
            if (atom.entity == null) {
                System.err.println("Null entity " + atom.getFullName());
            } else {
                SpatialSet spatialSet = atom.getSpatialSet();

                if (spatialSet != null) {
                    spatialSet.setBFactor((float) atom.rmsAtom(spatialSet));
                }
            }
        }
    }

    public int checkType() {
        Set<String> atomSet = new TreeSet<String>();
        Iterator e = entities.values().iterator();
        while (e.hasNext()) {
            Entity entity = (Entity) e.next();
            Residue firstResidue = null;
            Residue lastResidue = null;
            Compound compound = null;
            if (entity instanceof Polymer) {
                Polymer polymer = (Polymer) entity;
                firstResidue = polymer.firstResidue;
                lastResidue = polymer.lastResidue;
                compound = (Compound) firstResidue;
            } else {
                compound = (Compound) entity;
            }

            while (compound != null) {
                atomSet.clear();
                String resName = compound.getName();
                if (!resName.equals("ALA") && !resName.equals("GLY")) {
                    for (Atom atom : compound.atoms) {
                        atomSet.add(atom.getName());
                    }
                    if (atomSet.contains("CA") && atomSet.contains("CG")) {
                        if (atomSet.contains("HB1") && atomSet.contains("HB2")) {
                            return 1;
                        } else if (atomSet.contains("HB2") && atomSet.contains("HB3")) {
                            return 2;
                        }

                    }
                }
                if (entity instanceof Polymer) {
                    if (compound == lastResidue) {
                        break;
                    }
                    compound = ((Residue) compound).next;

                } else {
                    break;
                }
            }
        }
        return 0;
    }

    public SpatialSetIterator getSpatialSetIterator() {
        return new SpatialSetIterator(this);

    }

    public static class SpatialSetIterator implements Iterator {

        private final Iterator coordSetIterator;
        private Compound compound = null;
        private Residue firstResidue = null;
        private Residue lastResidue = null;
        private Iterator entIterator = null;
        private Entity entity = null;
        private CoordSet coordSet = null;
        private Atom atom = null;
        private Iterator<Atom> atomIterator;

        public SpatialSetIterator(Molecule molecule) {
            coordSetIterator = molecule.coordSets.values().iterator();
            if (nextCoordSet()) {
                if (nextEntity()) {
                    nextAtom();
                }
            }
        }

        public boolean nextAtom() {
            if (!atomIterator.hasNext()) {
                if (!nextCompound()) {
                    atom = null;
                    return false;
                }
            }
            atom = atomIterator.next();
            return true;
        }

        public boolean nextCompound() {
            if (entity instanceof Polymer) {
                if (compound == lastResidue) {
                    if (!nextEntity()) {
                        return false;
                    }
                } else {
                    compound = ((Residue) compound).next;
                }

            } else if (!nextEntity()) {
                return false;
            }

            atomIterator = compound.atoms.iterator();
            return true;
        }

        public boolean nextEntity() {
            if (!entIterator.hasNext()) {
                if (!nextCoordSet()) {
                    return false;
                }
            }
            entity = (Entity) entIterator.next();
            if (entity instanceof Polymer) {
                Polymer polymer = (Polymer) entity;
                firstResidue = polymer.firstResidue;
                lastResidue = polymer.lastResidue;
                compound = (Compound) firstResidue;
            } else {
                compound = (Compound) entity;
            }
            atomIterator = compound.atoms.iterator();
            return true;
        }

        public boolean nextCoordSet() {
            if (!coordSetIterator.hasNext()) {
                return false;
            }
            coordSet = (CoordSet) coordSetIterator.next();
            entIterator = coordSet.getEntities().values().iterator();
            return true;
        }

        public boolean hasNext() {
            return atom != null;
        }

        public SpatialSet next() {
            Atom currentAtom = atom;
            nextAtom();
            return currentAtom.getSpatialSet();
        }

        public void remove() {
        }
    }

    public ArrayList<HydrogenBond> hydrogenBonds(int[] structures) throws InvalidMoleculeException {
        MolFilter hydrogenFilter = new MolFilter("*.H,HN,HA");
        MolFilter acceptorFilter = new MolFilter("*.O,O*");
        return hydrogenBonds(structures, hydrogenFilter, acceptorFilter);
    }

    public ArrayList<HydrogenBond> hydrogenBonds(final int[] structures, final MolFilter hydrogenFilter, final MolFilter acceptorFilter) throws InvalidMoleculeException {
        Vector hydrogens = matchAtoms(hydrogenFilter);
        Vector acceptors = matchAtoms(acceptorFilter);
        ArrayList<HydrogenBond> hBonds = new ArrayList<HydrogenBond>();
        for (int i = 0, n = hydrogens.size(); i < n; i++) {
            SpatialSet hydrogen = (SpatialSet) hydrogens.elementAt(i);
            for (int j = 0, m = acceptors.size(); j < m; j++) {
                SpatialSet acceptor = (SpatialSet) acceptors.elementAt(j);
                HydrogenBond hBondBest = null;
                double bestShift = 0.0;
                int bestStructure = 0;
                for (int structureNum : structures) {
                    boolean valid = HydrogenBond.validate(hydrogen, acceptor, structureNum);
                    if (valid) {
                        HydrogenBond hBond = new HydrogenBond(hydrogen, acceptor);
                        hBonds.add(hBond);
                        break;
                    }
                }
            }
        }
        return hBonds;
    }

    public Map<String, HydrogenBond> hydrogenBondMap(final MolFilter hydrogenFilter, final MolFilter acceptorFilter, int structureNum) throws InvalidMoleculeException {
        Vector hydrogens = matchAtoms(hydrogenFilter);
        Vector acceptors = matchAtoms(acceptorFilter);
        Map<String, HydrogenBond> hBondMap = new HashMap<>();
        Map<String, HydrogenBond> acceptorMap = new HashMap<>();
        for (int i = 0, n = hydrogens.size(); i < n; i++) {
            SpatialSet hydrogen = (SpatialSet) hydrogens.elementAt(i);
            HydrogenBond hBondBest = null;
            double bestShift = -1.0e6;
            for (int j = 0, m = acceptors.size(); j < m; j++) {
                SpatialSet acceptor = (SpatialSet) acceptors.elementAt(j);
                boolean valid = HydrogenBond.validate(hydrogen, acceptor, structureNum);
                if (valid) {
                    HydrogenBond hBond = new HydrogenBond(hydrogen, acceptor);
                    double shift = hBond.getShift(structureNum);
                    if ((hBondBest == null) || (shift > bestShift)) {
                        hBondBest = hBond;
                        bestShift = shift;
                    }
                }
            }
            if (hBondBest != null) {
                HydrogenBond testBond = acceptorMap.get(hBondBest.acceptor.atom.getFullName());
                if (testBond != null) {
                    if (testBond.getShift(structureNum) < hBondBest.getShift(structureNum)) {
                        hBondMap.put(hydrogen.atom.getFullName(), hBondBest);
                        acceptorMap.put(hBondBest.acceptor.atom.getFullName(), hBondBest);
                        hBondMap.remove(testBond.hydrogen.atom.getFullName());
                    }
                } else {
                    hBondMap.put(hydrogen.atom.getFullName(), hBondBest);
                    acceptorMap.put(hBondBest.acceptor.atom.getFullName(), hBondBest);
                }
            }
        }
        return hBondMap;
    }

    public Map<String, Double> electroStaticShiftMap(final MolFilter targetFilter, final MolFilter sourceFilter, int structureNum) throws InvalidMoleculeException {
        Vector targets = matchAtoms(targetFilter);
        Vector sources = matchAtoms(sourceFilter);
        Map<String, Double> shiftMap = new HashMap<>();
        for (int i = 0, n = targets.size(); i < n; i++) {
            SpatialSet target = (SpatialSet) targets.elementAt(i);
            double sumShift = 0.0;
            for (int j = 0, m = sources.size(); j < m; j++) {
                SpatialSet source = (SpatialSet) sources.elementAt(j);
                boolean valid = ElectrostaticInteraction.validate(target, source, structureNum);
                if (valid) {
                    ElectrostaticInteraction eInteraction = new ElectrostaticInteraction(target, source);
                    sumShift += eInteraction.getShift(structureNum);
                }
            }
            shiftMap.put(target.atom.getFullName(), sumShift);
        }
        return shiftMap;
    }

    public void calcLCMB(final int iStruct) {
        calcLCMB(iStruct, true);
    }

    public void calcLCMB(final int iStruct, boolean scaleEnds) {
        double r0 = 3.0;
        double a = 39.3;
        updateAtomArray();
        for (Atom atom1 : atoms) {
            SpatialSet sp1 = atom1.spatialSet;
            sp1.setOrder(0.0f);
            Polymer polymer = null;
            ArrayList<Residue> residues = null;
            double endMultiplier = 1.0;
            if (atom1.entity instanceof Residue) {
                Residue residue = (Residue) atom1.entity;
                if ((polymer == null) || (polymer != residue.polymer)) {
                    polymer = residue.polymer;
                    residues = polymer.getResidues();
                }
                int nResidues = residues.size();
                int resNum = atom1.entity.entityID;
                if (scaleEnds) {
                    if (resNum < 4) {
                        endMultiplier = 1.6 - resNum / 10.0;
                    } else if ((nResidues - resNum + 1) < 4) {
                        endMultiplier = 1.6 - (nResidues - resNum + 1) / 10.0;
                    }
                }
            }
            if (atom1.getAtomicNumber() != 1) {
                Point3 pt1 = atom1.getPoint(iStruct);
                double fSum = 0.0;
                for (Atom atom2 : atoms) {
                    if ((atom1 != atom2) && (atom2.getAtomicNumber() != 1)) {
                        SpatialSet sp2 = atom2.spatialSet;
                        Point3 pt2 = atom2.getPoint(iStruct);
                        if ((pt1 != null) && (pt2 != null)) {
                            double r = Atom.calcDistance(pt1, pt2);
                            if (r < 15.0) {
                                fSum += a * Math.exp(-r / r0);
                            }
                        }
                    }
                }
                double bFactor = 1.0 / fSum * endMultiplier;
                sp1.setOrder((float) bFactor);
            }
        }
    }

    public static Vector matchAtoms(MolFilter molFilter) throws InvalidMoleculeException {
        String molName = Molecule.defaultMol;
        Molecule molecule = (Molecule) molecules.get(molName);

        if (molecule == null) {
            throw new InvalidMoleculeException("Can't find molecule " + molName);
        }
        return matchAtoms(molFilter, molecule);
    }

    public static Vector matchAtoms(MolFilter molFilter, Molecule molecule) {

        Vector selected = new Vector(32);
        if (molecule == null) {
            return selected;
        }

        Residue firstResidue = null;
        Residue lastResidue = null;
        CoordSet coordSet;

        boolean checkAll = false;

        for (int iAtom = 0; iAtom < molFilter.atomNames.size(); iAtom++) {
            String atomName = ((String) molFilter.atomNames.elementAt(iAtom));

            if (atomName.charAt(0) == '!') {
                checkAll = true;
            }
        }

        Iterator e = molecule.coordSets.values().iterator();

        while (e.hasNext()) {
            coordSet = (CoordSet) e.next();
            Iterator entIterator = coordSet.getEntities().values().iterator();

            while (entIterator.hasNext()) {
                Entity entity = (Entity) entIterator.next();
                Compound compound = null;
                if (!molFilter.matchCoordSetAndEntity(coordSet.getName(), entity.getName())) {
                    continue;
                }
                if (entity instanceof Polymer) {
                    Polymer polymer = (Polymer) entity;
                    if (molFilter.firstRes.equals("*")) {
                        firstResidue = polymer.firstResidue;
                    } else {
                        firstResidue = (Residue) polymer.residues.get(molFilter.firstRes);
                    }

                    if (molFilter.lastRes.equals("*")) {
                        lastResidue = polymer.lastResidue;
                    } else {
                        lastResidue = (Residue) polymer.residues.get(molFilter.lastRes);
                    }

                    compound = (Compound) firstResidue;
                } else {
                    compound = (Compound) entity;
                }

                if (compound == null) {
                    continue;
                }

                String atomName;

                while (compound != null) {
                    for (Atom atom : compound.atoms) {
                        boolean validRes = true;

                        if (!(entity instanceof Polymer)) {
                            if (!molFilter.firstRes.equals("*")
                                    && (!molFilter.firstRes.equals(compound.number))) {
                                validRes = false;
                            }
                        }

                        if (validRes) {
                            boolean validAtom = false;

                            for (int iAtom = 0;
                                    iAtom < molFilter.atomNames.size();
                                    iAtom++) {
                                atomName = ((String) molFilter.atomNames.elementAt(iAtom)).toLowerCase();

                                if (atomName.charAt(0) == '!') {
                                    if (!Util.stringMatch(
                                            atom.name.toLowerCase(),
                                            atomName.substring(1))) {
                                        SpatialSet spatialSet = atom.getSpatialSet();

                                        if (spatialSet != null) {
                                            validAtom = true;
                                        } else {
                                            validAtom = false;
                                        }
                                    } else {
                                        validAtom = false;

                                        break;
                                    }
                                } else if (Util.stringMatch(
                                        atom.name.toLowerCase(),
                                        atomName)) {
                                    SpatialSet spatialSet = atom.getSpatialSet();

                                    if (spatialSet != null) {
                                        validAtom = true;
                                    } else {
                                        validAtom = false;
                                        System.err.println(
                                                "null spatialset while matching atom "
                                                + atomName + " in coordset  "
                                                + coordSet.name);
                                    }

                                    if (!checkAll) {
                                        break;
                                    }
                                }
                            }

                            if (validAtom) {
                                SpatialSet spatialSet = atom.getSpatialSet();
                                selected.addElement(spatialSet);
                            }
                        }
                    }

                    if (entity instanceof Polymer) {
                        if (compound == lastResidue) {
                            break;
                        }

                        compound = ((Residue) compound).next;
                    } else {
                        break;
                    }
                }
            }
        }

        return (selected);
    }

    public static ArrayList<Atom> getMatchedAtoms(MolFilter molFilter, Molecule molecule) {
        ArrayList<Atom> selected = new ArrayList<Atom>(32);
        if (molecule == null) {
            return selected;
        }

        Residue firstResidue = null;
        Residue lastResidue = null;
        CoordSet coordSet;

        boolean checkAll = false;

        for (int iAtom = 0; iAtom < molFilter.atomNames.size(); iAtom++) {
            String atomName = ((String) molFilter.atomNames.elementAt(iAtom));

            if (atomName.charAt(0) == '!') {
                checkAll = true;
            }
        }

        Iterator e = molecule.coordSets.values().iterator();
        while (e.hasNext()) {
            coordSet = (CoordSet) e.next();
            Iterator entIterator = coordSet.getEntities().values().iterator();

            while (entIterator.hasNext()) {
                Entity entity = (Entity) entIterator.next();
                Compound compound = null;
                if (!molFilter.matchCoordSetAndEntity(coordSet.getName(), entity.getName())) {
                    continue;
                }
                if (entity instanceof Polymer) {
                    Polymer polymer = (Polymer) entity;
                    if (molFilter.firstRes.equals("*")) {
                        firstResidue = polymer.firstResidue;
                    } else {
                        firstResidue = (Residue) polymer.residues.get(molFilter.firstRes);
                    }

                    if (molFilter.lastRes.equals("*")) {
                        lastResidue = polymer.lastResidue;
                    } else {
                        lastResidue = (Residue) polymer.residues.get(molFilter.lastRes);
                    }

                    compound = (Compound) firstResidue;
                } else {
                    compound = (Compound) entity;
                }

                if (compound == null) {
                    continue;
                }

                String atomName;

                while (compound != null) {
                    String rNum = compound.getNumber();
                    try {
                        int rNumInt = Integer.parseInt(rNum);
                        if (rNumInt > molecule.lastResNum) {
                            molecule.lastResNum = rNumInt;
                        }
                    } catch (NumberFormatException nfE) {
                    }
                    for (Atom atom : compound.atoms) {
                        boolean validRes = true;

                        // fixme why is this inside atom loop
                        if (!(entity instanceof Polymer)) {
                            if (!molFilter.firstRes.equals("*")
                                    && (!molFilter.firstRes.equals(compound.number))) {
                                validRes = false;
                            }
                        }

                        if (validRes) {
                            boolean validAtom = false;

                            for (int iAtom = 0; iAtom < molFilter.atomNames.size(); iAtom++) {
                                atomName = ((String) molFilter.atomNames.elementAt(iAtom)).toLowerCase();
                                boolean isInverse = false;
                                if (atomName.charAt(0) == '!') {
                                    atomName = atomName.substring(1);
                                    isInverse = true;
                                }
                                boolean isPseudo = false;
                                if ((atomName.charAt(0) == 'm') || (atomName.charAt(0) == 'q')) {
                                    if (compound instanceof Residue) {
                                        Residue residue = (Residue) compound;
                                        Atom[] pseudoAtoms = residue.getPseudo(atomName.toUpperCase());
                                        if (pseudoAtoms == null) {
                                            System.out.println(residue.getName() + " " + atomName);
                                            System.exit(1);
                                        }
                                        for (Atom atom2 : pseudoAtoms) {
                                            if (atom.name.equalsIgnoreCase(atom2.name)) {
                                                if (!atom.isMethyl() || atom.isFirstInMethyl()) {
                                                    selected.add(atom);
                                                    break;
                                                }
                                            }

                                        }
                                        isPseudo = true;
                                    }
                                }
                                if (isPseudo) {
                                    continue;
                                }
                                boolean nameMatches = Util.stringMatch(atom.name.toLowerCase(), atomName);
                                if (isInverse) {
                                    if (!nameMatches) {
                                        SpatialSet spatialSet = atom.getSpatialSet();
                                        if (spatialSet != null) {
                                            validAtom = true;
                                        } else {
                                            validAtom = false;
                                        }
                                    } else {
                                        validAtom = false;
                                        break;
                                    }
                                } else if (nameMatches) {
                                    SpatialSet spatialSet = atom.getSpatialSet();
                                    if (spatialSet != null) {
                                        validAtom = true;
                                    } else {
                                        validAtom = false;
                                        System.err.println(
                                                "null spatialset while matching atom "
                                                + atomName + " in coordset  "
                                                + coordSet.name);
                                    }

                                    if (!checkAll) {
                                        break;
                                    }
                                }
                            }

                            if (validAtom) {
                                selected.add(atom);
                            }
                        }
                    }

                    if (entity instanceof Polymer) {
                        if (compound == lastResidue) {
                            break;
                        }

                        compound = ((Residue) compound).next;
                    } else {
                        break;
                    }
                }
            }
        }

        return (selected);
    }

    public static ArrayList<Atom> getNEFMatchedAtoms(MolFilter molFilter, Molecule molecule) {
        ArrayList<Atom> selected = new ArrayList<Atom>(32);
        if (molecule == null) {
            return selected;
        }

        Residue firstResidue = null;
        Residue lastResidue = null;
        CoordSet coordSet;

        boolean checkAll = false;

        for (int iAtom = 0; iAtom < molFilter.atomNames.size(); iAtom++) {
            String atomName = ((String) molFilter.atomNames.elementAt(iAtom));

            if (atomName.charAt(0) == '!') {
                checkAll = true;
            }
        }

        Iterator e = molecule.coordSets.values().iterator();
        while (e.hasNext()) {
            coordSet = (CoordSet) e.next();
            Iterator entIterator = coordSet.getEntities().values().iterator();

            while (entIterator.hasNext()) {
                Entity entity = (Entity) entIterator.next();
                Compound compound = null;
                if (!molFilter.matchCoordSetAndEntity(coordSet.getName(), entity.getName())) {
                    continue;
                }
                if (entity instanceof Polymer) {
                    Polymer polymer = (Polymer) entity;
                    if (molFilter.firstRes.equals("*")) {
                        firstResidue = polymer.firstResidue;
                    } else {
                        firstResidue = (Residue) polymer.residues.get(molFilter.firstRes);
                    }

                    if (molFilter.lastRes.equals("*")) {
                        lastResidue = polymer.lastResidue;
                    } else {
                        lastResidue = (Residue) polymer.residues.get(molFilter.lastRes);
                    }

                    compound = (Compound) firstResidue;
                } else {
                    compound = (Compound) entity;
                }

                if (compound == null) {
                    continue;
                }

                String atomName;
                while (compound != null) {
                    String rNum = compound.getNumber();
                    try {
                        int rNumInt = Integer.parseInt(rNum);
                        if (rNumInt > molecule.lastResNum) {
                            molecule.lastResNum = rNumInt;
                        }
                    } catch (NumberFormatException nfE) {
                    }
                    for (Atom atom : compound.atoms) {
                        boolean validRes = true;

                        // fixme why is this inside atom loop
                        if (!(entity instanceof Polymer)) {
                            if (!molFilter.firstRes.equals("*")
                                    && (!molFilter.firstRes.equals(compound.number))) {
                                validRes = false;
                            }
                        }

                        if (validRes) {
                            boolean validAtom = false;

                            for (int iAtom = 0; iAtom < molFilter.atomNames.size(); iAtom++) {
                                atomName = ((String) molFilter.atomNames.elementAt(iAtom)).toLowerCase();
                                boolean isInverse = false;
                                if (atomName.charAt(0) == '!') {
                                    atomName = atomName.substring(1);
                                    isInverse = true;
                                }
                                boolean isPseudo = false;
                                if ((atomName.charAt(0) == 'm') || (atomName.charAt(0) == 'q')) {
                                    if (compound instanceof Residue) {
                                        Residue residue = (Residue) compound;
                                        Atom[] pseudoAtoms = residue.getPseudo(atomName.toUpperCase());
                                        if (pseudoAtoms == null) {
                                            System.out.println(residue.getName() + " " + atomName);
                                            System.exit(1);
                                        }
                                        for (Atom atom2 : pseudoAtoms) {
                                            if (atom.name.equalsIgnoreCase(atom2.name)) {
                                                if (!atom.isMethyl() || atom.isFirstInMethyl()) {
                                                    selected.add(atom);
                                                    break;
                                                }
                                            }

                                        }
                                        isPseudo = true;
                                    }
                                }
                                if (isPseudo) {
                                    continue;
                                }
                                boolean nameMatches = Util.nefMatch(atom.name.toLowerCase(), atomName);
                                if (isInverse) {
                                    if (!nameMatches) {
                                        SpatialSet spatialSet = atom.getSpatialSet();
                                        if (spatialSet != null) {
                                            validAtom = true;
                                        } else {
                                            validAtom = false;
                                        }
                                    } else {
                                        validAtom = false;
                                        break;
                                    }
                                } else if (nameMatches) {
                                    SpatialSet spatialSet = atom.getSpatialSet();
                                    if (spatialSet != null) {
                                        validAtom = true;
                                    } else {
                                        validAtom = false;
                                        System.err.println(
                                                "null spatialset while matching atom "
                                                + atomName + " in coordset  "
                                                + coordSet.name);
                                    }

                                    if (!checkAll) {
                                        break;
                                    }
                                }
                            }

                            if (validAtom) {
                                selected.add(atom);
                            }
                        }
                    }

                    if (entity instanceof Polymer) {
                        if (compound == lastResidue) {
                            break;
                        }

                        compound = ((Residue) compound).next;
                    } else {
                        break;
                    }
                }
            }
        }

        return (selected);
    }

    public static void makeAtomList() {
        atomList = new Vector();

        for (Molecule molecule : molecules.values()) {
            molecule.updateAtomArray();
            for (Atom atom : molecule.atoms) {
                atomList.addElement(atom);
            }
        }

    }

    public void updateNames() {
        Residue firstResidue = null;
        Residue lastResidue = null;
        Compound compound;
        Entity entity;
        Iterator e = entities.values().iterator();

        while (e.hasNext()) {
            entity = (Entity) e.next();

            //System.err.println(entity.name);
            if (entity instanceof Polymer) {
                Polymer polymer = (Polymer) entity;
                firstResidue = polymer.firstResidue;

                lastResidue = polymer.lastResidue;
                compound = (Compound) firstResidue;
            } else {
                compound = (Compound) entity;
            }

            while (compound != null) {
                compound.updateNames();

                if (entity instanceof Polymer) {
                    if (compound == lastResidue) {
                        break;
                    }

                    compound = ((Residue) compound).next;
                } else {
                    break;
                }
            }
        }
    }

    public Atom getAtom(String name) {
        return atomMap.get(name);
    }

    public static Atom getAtomByName(String name)
            throws IllegalArgumentException {
        MolFilter molFilter = null;
        molFilter = new MolFilter(name);
        Atom atom = null;
        SpatialSet spSet = getSpatialSet(molFilter);
        if (spSet != null) {
            atom = spSet.atom;
        }
        return atom;
    }

    public static Atom getAtom(MolFilter molFilter) throws InvalidMoleculeException {
        ArrayList spatialSets = new ArrayList();
        selectAtomsForTable(molFilter, spatialSets);
        SpatialSet spSet = getSpatialSet(molFilter);
        Atom atom = null;
        if (spSet != null) {
            atom = spSet.atom;
        }

        return atom;
    }

    public static SpatialSet getSpatialSet(MolFilter molFilter)
            throws IllegalArgumentException {
        Residue firstResidue = null;
        Compound compound;
        Molecule molecule = null;
        CoordSet coordSet;

        String molName = Molecule.defaultMol;
        molecule = (Molecule) molecules.get(molName);

        if (molecule == null) {
            throw new IllegalArgumentException("Can't find molecule " + molName);
        }

        Iterator e = molecule.coordSets.values().iterator();

        while (e.hasNext()) {
            coordSet = (CoordSet) e.next();
            Iterator entIterator = coordSet.getEntities().values().iterator();

            while (entIterator.hasNext()) {
                Entity entity = (Entity) entIterator.next();
                if (!molFilter.matchCoordSetAndEntity(coordSet.getName(), entity.getName())) {
                    continue;
                }

                if (entity instanceof Polymer) {
                    Polymer polymer = (Polymer) entity;
                    firstResidue = (Residue) polymer.residues.get(molFilter.firstRes);
                    compound = (Compound) firstResidue;
                } else {
                    compound = (Compound) entity;

                    if (!molFilter.firstRes.equals("*")
                            && (!molFilter.firstRes.equals(compound.number))) {
                        continue;
                    }
                }

                Atom atom;

                if (compound != null) {
                    atom = compound.getAtomLoose((String) molFilter.atomNames.elementAt(0));
                    if (atom != null) {
                        return atom.getSpatialSet();
                    } else {
                        return null;
                    }
                }
            }
        }

        return (null);
    }

    public void setupRotGroups() {
        int rotUnit = 0;
        for (Atom iAtom : atoms) {
            iAtom.rotUnit = -1;
            if ((iAtom.getParent() != null) && (iAtom.irpIndex > 0) && iAtom.rotActive) {
                //if (iAtom.irpIndex > 0) {
                iAtom.rotUnit = rotUnit++;
            }
            Atom jAtom = iAtom;
            while ((jAtom = jAtom.getParent()) != null) {
                //if (jAtom.irpIndex != 0) {
                if ((jAtom.irpIndex > 0) && jAtom.rotActive) {
                    iAtom.rotGroup = jAtom;
                    break;
                }
            }
        }
    }

    public void setMethylRotationActive(boolean state) {
        updateAtomArray();
        findEquivalentAtoms();
        for (Atom iAtom : atoms) {
            if (iAtom.isMethyl()) {
                Atom parent = iAtom.getParent();
                if (parent != null) {
                    parent.rotActive = state;
                }
            }
        }
        setupRotGroups();
        setupAngles();
    }

    public ArrayList<Atom> getAngleAtoms() {
        return angleAtoms;
    }

    public ArrayList<Atom> setupAngles() {
        angleAtoms = new ArrayList<Atom>();
        for (Atom iAtom : atoms) {
            if ((iAtom.getParent() != null) && (iAtom.irpIndex > 0) && iAtom.rotActive) {
                //if (iAtom.irpIndex > 0) {
                Atom jAtom = iAtom.getAngleChild();
                if (jAtom != null) {
                    angleAtoms.add(jAtom);
                }
            }
        }
        return angleAtoms;
    }

    public ArrayList<Atom> getPseudoAngleAtoms() {
        return pseudoAngleAtoms;
    }

    public ArrayList<Atom> setupPseudoAngles() {
        pseudoAngleAtoms = new ArrayList<Atom>();
        for (Atom iAtom : atoms) {
            if (iAtom.getName().equals("O3'") || iAtom.getName().equals("C1'")) {
                pseudoAngleAtoms.add(iAtom);
            } else if ((iAtom.getName().charAt(0) == 'N') && (iAtom.getParent() != null) && (iAtom.getParent().getName().equals("C1'"))) {
                pseudoAngleAtoms.add(iAtom);
            }
        }
        return pseudoAngleAtoms;
    }

    public void getBondsBroken() {
        /*
         Residue residue = null;
         int i;
         String atomName1;
         String atomName2;
         Atom atom1 = null;
         Atom atom2 = null;
         Vector residueBonds = null;
         Entity entity;

         Iterator e = entities.values().iterator();

         while (e.hasNext()) {
         entity = (Entity) e.next();

         if (entity instanceof Polymer) {
         Polymer polymer = (Polymer) entity;
         residue = polymer.firstResidue;
         } else {
         continue;
         }
         while (residue != null) {
         // fixme  need to get residueBonds
         //              residueBonds = (Vector) residueTable(residue.name);
         for (i = 0; i < residueBonds.size(); i += 2) {
         atomName1 = (String) residueBonds.elementAt(i);
         atomName2 = (String) residueBonds.elementAt(i + 1);
         atom1 = residue.getAtom(atomName1);

         if (atom1 == null) {
         continue;
         }

         atom2 = residue.getAtom(atomName2);

         if (atom2 == null) {
         continue;
         }

         Bond bond = new Bond(atom1, atom2);
         atom1.addBond(bond);

         if (residue.polymer.firstBond == null) {
         residue.polymer.firstBond = bond;
         }

         bond.previous = residue.polymer.lastBond;

         if (residue.polymer.lastBond != null) {
         residue.polymer.lastBond.next = bond;
         }

         bond.next = null;
         residue.polymer.lastBond = bond;
         }

         residue = residue.next;
         }
         }
         *
         */
    }

    public static boolean isDisulfide(Atom sg1, int iStruct) {
        boolean result = false;
        if (sg1.getName().equals("SG")) {
            if (Molecule.atomList == null) {
                Molecule.makeAtomList();
            }
            Point3 pt1 = sg1.getPoint(iStruct);

            for (int i = 0; i < Molecule.atomList.size(); i++) {
                Atom sg2 = (Atom) Molecule.atomList.elementAt(i);
                if ((sg1 != sg2) && sg2.getName().equals("SG")) {
                    Point3 pt2 = sg2.getPoint(iStruct);
                    double distance = Atom.calcDistance(pt1, pt2);
                    if (distance < 3.0) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static void calcAllBonds() {
        if (Molecule.atomList == null) {
            Molecule.makeAtomList();
        }

        Atom atom1 = null;
        Atom atom2 = null;
        int result;
        int nBonds = 0;

        for (int i = 0; i < Molecule.atomList.size(); i++) {
            for (int j = i + 1; j < Molecule.atomList.size(); j++) {
                atom1 = (Atom) Molecule.atomList.elementAt(i);
                atom2 = (Atom) Molecule.atomList.elementAt(j);
                result = Atom.calcBond(atom1, atom2, 1);

                if (result == 2) {
                    break;
                }

                if (result == 0) {
                    nBonds++;
                }
            }
        }

        System.err.println("Added " + nBonds + " bonds");
    }

    public void calcBonds() {
        Atom atom1 = null;
        Atom atom2 = null;
        int result;
        int nBonds = 0;

        for (int i = 0; i < globalSelected.size(); i++) {
            for (int j = i + 1; j < globalSelected.size(); j++) {
                atom1 = ((SpatialSet) globalSelected.elementAt(i)).atom;

                if (atom1.getSelected() != 1) {
                    continue;
                }

                atom2 = ((SpatialSet) globalSelected.elementAt(j)).atom;

                if (atom2.getSelected() != 1) {
                    continue;
                }

                result = Atom.calcBond(atom1, atom2, 1);

                if (result == 2) {
                    break;
                }

                if (result == 0) {
                    nBonds++;
                }
            }
        }

        System.err.println("Added " + nBonds + " bonds");
    }

    public ArrayList<AtomPairDistance> getDistancePairs(double tolerance, boolean requireActive) {
        int[] structures = getActiveStructures();
        if (structures.length == 0) {
            structures = new int[1];
        }
        Atom atom1 = null;
        Atom atom2 = null;
        int result;
        int nBonds = 0;
        ArrayList<AtomPairDistance> pairs = new ArrayList<AtomPairDistance>();
        for (int i = 0; i < globalSelected.size(); i++) {
            atom1 = ((SpatialSet) globalSelected.elementAt(i)).atom;
            if (atom1.getSelected() != 1) {
                continue;
            }
            if (requireActive && !atom1.active) {
                continue;
            }
            if (atom1.isMethyl()) {
                if (!atom1.isFirstInMethyl()) {
                    continue;
                }
            }
            // skip hydrogens that are likely to be in rapid exchange
            if ((atom1.getAtomicNumber() == 1) && atom1.getParent().getType().equals("N+")) {
                continue;
            }
            if ((atom1.getAtomicNumber() == 1) && atom1.getParent().getType().startsWith("O")) {
                continue;
            }
            for (int j = i + 1; j < globalSelected.size(); j++) {
                double extra = 0.0;
                if (atom1.isMethyl()) {
                    extra += 0.7;
                }
                atom2 = ((SpatialSet) globalSelected.elementAt(j)).atom;
                if (atom2.getSelected() != 1) {
                    continue;
                }
                // skip hydrogens that are likely to be in rapid exchange
                if ((atom2.getAtomicNumber() == 1) && atom2.getParent().getType().equals("N+")) {
                    continue;
                }
                if ((atom2.getAtomicNumber() == 1) && atom2.getParent().getType().startsWith("O")) {
                    continue;
                }
                if (requireActive && !atom2.active) {
                    continue;
                }
                if (atom2.isMethyl()) {
                    if (!atom2.isFirstInMethyl()) {
                        continue;
                    }
                    extra += 0.7;
                }
                boolean foundPair = false;
                double minDis = Double.MAX_VALUE;
                for (int iStruct : structures) {
                    Point3 pt1;
                    if (atom1.isMethyl()) {
                        pt1 = atom1.getMethylCenter(iStruct);
                    } else {
                        pt1 = atom1.getPoint(iStruct);
                    }
                    Point3 pt2;
                    if (atom2.isMethyl()) {
                        pt2 = atom2.getMethylCenter(iStruct);
                    } else {
                        pt2 = atom2.getPoint(iStruct);
                    }
                    if (pt1 == null) {
                        System.out.println("null point for " + atom1.getShortName() + " " + iStruct);
                        continue;
                    }
                    if (pt2 == null) {
                        System.out.println("null point for " + atom2.getShortName() + " " + iStruct);
                        continue;
                    }
                    double distance = Atom.calcDistance(pt1, pt2);
                    if (distance < (tolerance + extra)) {
                        if (distance < minDis) {
                            minDis = distance;
                        }
                        foundPair = true;
                    }
                }
                if (foundPair) {
                    AtomPairDistance pair = new AtomPairDistance(atom1, atom2, minDis);
                    pairs.add(pair);
                }
            }
        }
        return pairs;
    }

    public static void findEquivalentAtoms(String atomName)
            throws IllegalArgumentException {
        Atom atom = getAtomByName(atomName);

        if (atom == null) {
            throw new IllegalArgumentException(
                    "Can't find atom \"" + atomName + "\"");
        }

        atom.getEquivalency();
    }

    public void findEquivalentAtoms() {
        updateAtomArray();
        ArrayList<Atom> atoms2 = new ArrayList<>(atoms);
        for (Atom atom : atoms2) {
            Entity entity = atom.entity;
            if (!entity.hasEquivalentAtoms()) {
                findEquivalentAtoms(entity);
            }
        }
    }

    public static void findEquivalentAtoms(Entity entity) {
        Molecule molecule = entity.molecule;
        molecule.getAtomTypes();

        MTree mTree = new MTree();
        Hashtable hash = new Hashtable();
        Vector eAtomList = new Vector();
        int i = 0;

        for (Atom atom : entity.atoms) {
            // entity check ensures that only atoms in same residue are used
            if (atom.entity == entity) {
                hash.put(atom, Integer.valueOf(i));
                eAtomList.add(atom);

                mTree.addNode();
                atom.equivAtoms = null;

                //mNode.atom = atom;
                i++;
            }
        }

        for (Atom atom : entity.atoms) {
            for (int iBond = 0; iBond < atom.bonds.size(); iBond++) {
                Bond bond = (Bond) atom.bonds.elementAt(iBond);
                Integer iNodeBegin = (Integer) hash.get(bond.begin);
                Integer iNodeEnd = (Integer) hash.get(bond.end);

                if ((iNodeBegin != null) && (iNodeEnd != null)) {
                    mTree.addEdge(iNodeBegin.intValue(), iNodeEnd.intValue());

                }
            }
        }

        class TreeGroup {

            int iAtom = 0;
            int[] path = null;
            ArrayList treeValues = null;

            TreeGroup(int iAtom, int[] path, ArrayList treeValues) {
                this.iAtom = iAtom;
                this.path = path;
                this.treeValues = treeValues;
            }
        }

        ArrayList treeGroups = new ArrayList();

        // get breadth first path from each atom
        for (int j = 0, n = eAtomList.size(); j < n; j++) {
            Atom atom = (Atom) eAtomList.elementAt(j);

            int[] path = mTree.broad_path(j);
            int shell;
            int value;

            ArrayList treeValues = new ArrayList(path.length);

            for (int k = 0; k < path.length; k++) {
                atom = (Atom) eAtomList.elementAt(path[k] & 0xFF);
                shell = (path[k] >> 8);

                // value ensures that only atoms of same type in same shell are equivalent
                // type has contribution from atomic number and number of pi bonds
                value = (shell * 4096) + (16 * atom.aNum)
                        + ((4 * atom.nPiBonds) / 2);
                treeValues.add(Integer.valueOf(value));
            }

            Collections.sort(treeValues);
            treeGroups.add(new TreeGroup(j, path, treeValues));
        }

        ArrayList equivAtoms = new ArrayList();
        Map groupHash = new TreeMap();
        Map uniqueMap = new TreeMap();
        int nGroups = 0;

        for (int j = 0; j < treeGroups.size(); j++) {
            equivAtoms.clear();

            TreeGroup jGroup = (TreeGroup) treeGroups.get(j);

            for (int k = 0; k < treeGroups.size(); k++) {
                if (j == k) {
                    continue;
                }

                TreeGroup kGroup = (TreeGroup) treeGroups.get(k);
                boolean ok = false;

                // atoms are equivalent only if all the atoms on each atoms tree are the same type
                if (kGroup.treeValues.size() == jGroup.treeValues.size()) {
                    ok = true;

                    for (int kj = 0; kj < jGroup.treeValues.size(); kj++) {
                        int kVal = ((Integer) kGroup.treeValues.get(kj)).intValue();
                        int jVal = ((Integer) jGroup.treeValues.get(kj)).intValue();

                        if (kVal != jVal) {
                            ok = false;

                            break;
                        }
                    }
                }

                if (ok) {
                    Atom jAtom = (Atom) eAtomList.elementAt(jGroup.path[0]
                            & 0xFF);
                    Atom kAtom = (Atom) eAtomList.elementAt(kGroup.path[0]
                            & 0xFF);
                    int shell = -1;

                    for (int jj = 0; jj < kGroup.treeValues.size(); jj++) {
                        Atom atomTest = (Atom) eAtomList.elementAt(kGroup.path[jj]
                                & 0xFF);

                        if (atomTest.getName().equals(jAtom.getName())) {
                            shell = (kGroup.path[jj] >> 8);
                        }
                    }

                    String groupName = shell + "_" + jAtom.getName();
                    String jUniq = shell + "_" + jAtom.getName();
                    String kUniq = shell + "_" + kAtom.getName();

                    if (!uniqueMap.containsKey(jUniq)
                            && !uniqueMap.containsKey(kUniq)) {
                        nGroups++;
                        uniqueMap.put(jUniq, Integer.valueOf(nGroups));
                        uniqueMap.put(kUniq, Integer.valueOf(nGroups));
                    } else if (!uniqueMap.containsKey(jUniq)) {
                        uniqueMap.put(jUniq, uniqueMap.get(kUniq));
                    } else if (!uniqueMap.containsKey(kUniq)) {
                        uniqueMap.put(kUniq, uniqueMap.get(jUniq));
                    }

                    AtomEquivalency atomEquiv = (AtomEquivalency) groupHash.get(groupName);

                    if (atomEquiv == null) {
                        atomEquiv = new AtomEquivalency();
                        atomEquiv.setShell(shell);
                        atomEquiv.setIndex(((Integer) uniqueMap.get(jUniq)).intValue());
                        atomEquiv.setAtoms(new ArrayList<Atom>());
                        atomEquiv.getAtoms().add(jAtom);
                        groupHash.put(groupName, atomEquiv);
                    }

                    atomEquiv.getAtoms().add(kAtom);
                }
            }
        }

        Iterator iter = groupHash.entrySet().iterator();

        while (iter.hasNext()) {
            nGroups++;

            AtomEquivalency atomEquiv = (AtomEquivalency) ((Map.Entry) iter.next()).getValue();
            Atom eAtom = atomEquiv.getAtoms().get(0);

            if (eAtom.equivAtoms == null) {
                eAtom.equivAtoms = new ArrayList(2);
            }

            eAtom.equivAtoms.add(atomEquiv);
        }

        entity.setHasEquivalentAtoms(true);
    }

    public static void getCouplings(final Entity entity,
            final ArrayList<JCoupling> jCouplings, final ArrayList<JCoupling> tocsyLinks,
            final ArrayList<JCoupling> hmbcLinks, int nShells, int minShells) {
        Molecule molecule = entity.molecule;
        molecule.getAtomTypes();

        MTree mTree = new MTree();
        MTree mTreeJ = new MTree();
        HashMap<Atom, Integer> hash = new HashMap<Atom, Integer>();
        HashMap<Atom, Integer> hashJ = new HashMap<Atom, Integer>();
        ArrayList<Atom> eAtomList = new ArrayList<Atom>();
        ArrayList<Atom> eAtomListJ = new ArrayList<Atom>();
        int i = 0;

        for (Atom atom : entity.atoms) {
            // entity check ensures that only atoms in same residue are used
            if (atom.entity == entity) {
                if (atom.isMethyl() && !atom.isFirstInMethyl()) {
                    continue;
                }
                hash.put(atom, Integer.valueOf(i));
                eAtomList.add(atom);

                MNode mNode = mTree.addNode();
                atom.equivAtoms = null;
                mNode.setAtom(atom);

                //mNode.atom = atom;
                i++;
            }

        }

        for (Atom atom : entity.atoms) {
            for (int iBond = 0; iBond < atom.bonds.size(); iBond++) {
                Bond bond = (Bond) atom.bonds.elementAt(iBond);
                Integer iNodeBegin = (Integer) hash.get(bond.begin);
                Integer iNodeEnd = (Integer) hash.get(bond.end);

                if ((iNodeBegin != null) && (iNodeEnd != null)) {
                    mTree.addEdge(iNodeBegin.intValue(), iNodeEnd.intValue());
                }
            }

        }

        // get breadth first path from each atom
        Atom[] atoms = new Atom[nShells + 1];
        int iAtom = 0;
        for (int j = 0, n = eAtomList.size(); j < n; j++) {
            Atom atomStart = (Atom) eAtomList.get(j);
            if (atomStart.aNum != 1) {
                continue;
            }

            mTree.broad_path(j);
            ArrayList<MNode> pathNodes = mTree.getPathNodes();
            for (MNode mNode : pathNodes) {
                Atom atomEnd = mNode.getAtom();
                int shell = mNode.getShell();
                if (shell > nShells) {
                    break;
                } else if (shell < minShells) {
                    continue;
                }

                MNode nNode = mNode;
                boolean pathOK = true;
                for (int iShell = shell; iShell >= 0; iShell--) {
                    atoms[iShell] = nNode.getAtom();
                    // fixme what elements are acceptable on path?
                    if (atoms[iShell].getAtomicNumber() == 8) {
                        pathOK = false;
                        break;
                    }
                    nNode = nNode.getParent();
                }
                if (!pathOK) {
                    continue;
                }
                boolean gotJ = false;

                if ((shell > 1) && (atoms[shell].aNum == 1)) {
                    gotJ = true;
                    JCoupling jCoupling = JCoupling.couplingFromAtoms(atoms, shell + 1, shell);
                    jCouplings.add(jCoupling);
                } else if (atoms[shell].aNum == 6) {
                    JCoupling jCoupling = JCoupling.couplingFromAtoms(atoms, shell + 1, shell);
                    hmbcLinks.add(jCoupling);
                }
                if (gotJ) {
                    if (!hashJ.containsKey(atomStart)) {
                        hashJ.put(atomStart, iAtom);
                        eAtomListJ.add(atomStart);
                        mTreeJ.addNode();
                        iAtom++;
                    }
                    if (!hashJ.containsKey(atomEnd)) {
                        hashJ.put(atomEnd, iAtom);
                        eAtomListJ.add(atomEnd);
                        mTreeJ.addNode();
                        iAtom++;
                    }
                    Integer iNodeBegin = hashJ.get(atomStart);
                    Integer iNodeEnd = hashJ.get(atomEnd);

                    if ((iNodeBegin != null) && (iNodeEnd != null)) {
                        if (iNodeBegin != iNodeEnd.intValue()) {
                            mTreeJ.addEdge(iNodeBegin, iNodeEnd);
                        }
                    }
                }

            }
        }

        for (int j = 0, n = eAtomListJ.size(); j < n; j++) {
            Atom atomStart = (Atom) eAtomListJ.get(j);
            if (atomStart.aNum != 1) {
                continue;
            }

            int[] path = mTreeJ.broad_path(j);
            int shell;
            int value;

            for (int k = 1; k < path.length; k++) {
                Atom atomEnd = (Atom) eAtomListJ.get(path[k] & 0xFF);
                shell = (path[k] >> 8);
                if ((shell > 0) && (shell < 6)) {
                    atoms[0] = atomStart;
                    atoms[1] = atomEnd;
                    JCoupling jCoupling = JCoupling.couplingFromAtoms(atoms, 2, shell);
                    tocsyLinks.add(jCoupling);
                }
            }
        }

    }

    public static Atom getStartAtom(Molecule molecule) {
        Vector atoms = molecule.getAtoms();
        int maxValue = 0;
        int maxAtom = 0;

        for (int i = 0, n = atoms.size(); i < n; i++) {
            Atom atom = (Atom) atoms.elementAt(i);

            if (atom.canonValue > maxValue) {
                maxValue = atom.canonValue;
                maxAtom = i;
            }
        }

        return (Atom) atoms.elementAt(maxAtom);
    }

    public static int buildTree(Molecule molecule,
            Atom startAtom, Vector atomList, MTree mTree) {
        Hashtable hash = new Hashtable();

        Entity entity = startAtom.entity;
        int i = 0;
        int iStart = 0;

        for (Atom atom : entity.atoms) {
            if (atom == startAtom) {
                iStart = i;
            }

            if (atom.entity == entity) {
                hash.put(atom, Integer.valueOf(i));
                atomList.add(atom);

                MNode mNode = mTree.addNode();
                mNode.setValue(atom.canonValue);

                //mNode.atom = atom;
                i++;
            }

        }

        for (Atom atom : entity.atoms) {
            for (int iBond = 0; iBond < atom.bonds.size(); iBond++) {
                Bond bond = (Bond) atom.bonds.elementAt(iBond);
                Integer iNodeBegin = (Integer) hash.get(bond.begin);
                Integer iNodeEnd = (Integer) hash.get(bond.end);

                if ((iNodeBegin != null) && (iNodeEnd != null)) {
                    mTree.addEdge(iNodeBegin.intValue(), iNodeEnd.intValue());
                }
            }
        }

        mTree.sortNodes();

        return iStart;
    }

    public static void writeXYZ() {
        Molecule molecule = (Molecule) molecules.get(defaultMol);

        if (molecule == null) {
            return;
        }
        molecule.updateAtomArray();
        int i = 0;
        for (Atom atom : molecule.atoms) {
            SpatialSet spSet = atom.spatialSet;
            atom.iAtom = i;
            String result = spSet.toPDBString(i + 1, 0);
            System.out.println(result);
            i++;
        }
    }

    public static void writeXYZToXML(FileWriter chan,
            int whichStruct) throws InvalidMoleculeException, IOException {
        int i;
        int iStruct = 0;
        String result = null;

        Molecule molecule = (Molecule) molecules.get(defaultMol);

        if (molecule == null) {
            throw new InvalidMoleculeException("Molecule " + defaultMol + " doesn't exist");
        }
        molecule.updateAtomArray();

        int[] structureList = molecule.getActiveStructures();
        for (int jStruct = 0; jStruct < structureList.length; jStruct++) {
            iStruct = structureList[jStruct];
            if ((whichStruct >= 0) && (iStruct != whichStruct)) {
                continue;
            }

            i = 0;

            for (Atom atom : molecule.atoms) {
                result = atom.xyzToXMLString(iStruct, i);

                if (result != null) {
                    chan.write(result + "\n");
                    i++;
                }
            }
        }
    }

    public static void writePPMToXML(FileWriter chan,
            int whichStruct) throws IOException, InvalidMoleculeException {
        int i;
        String result = null;

        Molecule molecule = (Molecule) molecules.get(defaultMol);
        molecule.updateAtomArray();

        if (molecule == null) {
            throw new InvalidMoleculeException("Molecule " + defaultMol + " doesn't exist");
        }

        i = 0;

        for (Atom atom : molecule.atoms) {
            result = atom.ppmToXMLString(0, i);

            if (result != null) {
                chan.write(result + "\n");
                i++;
            }
        }
    }

    public void writeXYZToPDB(String fileName, int whichStruct) throws IOException {
        int i;
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {

            updateAtomArray();

            int[] structureList = getActiveStructures();
            if (structureList.length == 0) {
                structureList = new int[1];
                structureList[0] = 0;
            }
            ArrayList<Atom> bondList = new ArrayList<Atom>();
            StringBuilder outString = new StringBuilder();
            ArrayList<Integer> iAtoms = new ArrayList<Integer>();
            for (int iStruct : structureList) {
                if ((whichStruct >= 0) && (iStruct != whichStruct)) {
                    continue;
                }
                bondList.clear();
                i = 0;
                for (Atom atom : atoms) {
                    SpatialSet spSet = atom.spatialSet;
                    if (atom.isCoarse()) {
                        continue;
                    }
                    atom.iAtom = i;
                    String result = spSet.toPDBString(i + 1, iStruct);
                    if (!(spSet.atom.entity instanceof Residue) || !((Residue) spSet.atom.entity).isStandard()) {
                        bondList.add(spSet.atom);
                    }

                    if (result != null) {
                        out.print(result + "\n");
                        i++;
                    }
                }
                for (Atom bAtom : bondList) {
                    Vector bondedAtoms = bAtom.getConnected();
                    if (bondedAtoms.size() > 0) {
                        outString.setLength(0);
                        outString.append("CONECT");
                        outString.append(String.format("%5d", bAtom.iAtom + 1));
                        iAtoms.clear();
                        for (Object aObj : bondedAtoms) {
                            Atom bAtom2 = (Atom) aObj;
                            iAtoms.add(bAtom2.iAtom);
                        }
                        Collections.sort(iAtoms);
                        for (Integer iAtom : iAtoms) {
                            outString.append(String.format("%5d", iAtom + 1));
                        }
                        out.print(outString.toString() + "\n");
                    }
                }
            }
        }
    }

    public static String writeXYZToPDBString(int whichStruct) throws InvalidMoleculeException, IOException {
        StringWriter stringWriter = new StringWriter();
        writeXYZToPDB(stringWriter, whichStruct);
        return stringWriter.toString();
    }

    public static void writeXYZToPDB(Writer chan,
            int whichStruct) throws InvalidMoleculeException, IOException {
        int i;

        Molecule molecule = (Molecule) molecules.get(defaultMol);

        if (molecule == null) {
            throw new InvalidMoleculeException("Molecule " + defaultMol + " doesn't exist");
        }
        molecule.updateAtomArray();

        int[] structureList = molecule.getActiveStructures();
        if (structureList.length == 0) {
            structureList = new int[1];
            structureList[0] = 0;
        }
        ArrayList<Atom> bondList = new ArrayList<Atom>();
        StringBuilder outString = new StringBuilder();
        ArrayList<Integer> iAtoms = new ArrayList<Integer>();
        for (int iStruct : structureList) {
            if ((whichStruct >= 0) && (iStruct != whichStruct)) {
                continue;
            }
            bondList.clear();
            i = 0;
            for (Atom atom : molecule.atoms) {
                SpatialSet spSet = atom.spatialSet;
                if (atom.isCoarse()) {
                    continue;
                }
                atom.iAtom = i;
                String result = spSet.toPDBString(i + 1, iStruct);
                if (!(spSet.atom.entity instanceof Residue) || !((Residue) spSet.atom.entity).isStandard()) {
                    bondList.add(spSet.atom);
                }

                if (result != null) {
                    chan.write(result + "\n");
                    i++;
                }
            }
            for (Atom bAtom : bondList) {
                Vector bondedAtoms = bAtom.getConnected();
                if (bondedAtoms.size() > 0) {
                    outString.setLength(0);
                    outString.append("CONECT");
                    outString.append(String.format("%5d", bAtom.iAtom + 1));
                    iAtoms.clear();
                    for (Object aObj : bondedAtoms) {
                        Atom bAtom2 = (Atom) aObj;
                        iAtoms.add(bAtom2.iAtom);
                    }
                    Collections.sort(iAtoms);
                    for (Integer iAtom : iAtoms) {
                        outString.append(String.format("%5d", iAtom + 1));
                    }
                    chan.write(outString.toString() + "\n");
                }
            }
        }
    }

    public void getAtomTypes() {

        updateAtomArray();
        for (Atom atom : atoms) {
            atom.nPiBonds = 0;
            atom.nonHydrogens = 0;
            atom.hydrogens = 0;
        }

        for (Atom atom : atoms) {
            for (int iBond = 0; iBond < atom.bonds.size(); iBond++) {
                Bond bond = (Bond) atom.bonds.elementAt(iBond);
                if ((bond.begin == atom) && (bond.end.aNum == 1)) {
                    atom.hydrogens++;
                } else if ((bond.end == atom) && (bond.begin.aNum == 1)) {
                    atom.hydrogens++;
                }

                if (bond.begin.aNum > 1) {
                    bond.end.nonHydrogens++;
                }

                if (bond.end.aNum > 1) {
                    bond.begin.nonHydrogens++;
                }

                if (bond.order < 5) {
                    bond.begin.nPiBonds += (2 * (bond.order - 1));
                    bond.end.nPiBonds += (2 * (bond.order - 1));
                } else if ((bond.order == 8)) {
                    bond.begin.nPiBonds += 1;
                    bond.end.nPiBonds += 1;
                } else if ((bond.order == 7)) {
                    bond.begin.nPiBonds += 1;
                    bond.end.nPiBonds += 1;
                }

                //System.err.println (atom.name+" "+bond.begin.name + " " + bond.end.name);
            }

            //System.err.println (atom.name+" "+atom.nPiBonds);
        }

    }

    static Bond findBond(Atom atomB, Atom atomE) {
        Bond bond = null;

        for (int iBond = 0; iBond < atomB.bonds.size(); iBond++) {
            bond = (Bond) atomB.bonds.elementAt(iBond);

            if (((bond.begin == atomB) && (bond.end == atomE))
                    || ((bond.begin == atomE) && (bond.end == atomB))) {
                return bond;
            }
        }

        for (int iBond = 0; iBond < atomE.bonds.size(); iBond++) {
            bond = (Bond) atomE.bonds.elementAt(iBond);

            if (((bond.begin == atomB) && (bond.end == atomE))
                    || ((bond.begin == atomE) && (bond.end == atomB))) {
                return bond;
            }
        }

        System.err.println("no bond");

        return null;
    }

    public static List<String> getLabelTypes() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i <= LABEL_PPM; i++) {
            list.add((String) labelTypes.get(i));
        }

        return list;
    }

    public static List<String> getDisplayTypes() {
        List<String> list = new ArrayList<>();
        Iterator iter = displayTypes.iterator();

        while (iter.hasNext()) {
            list.add((String) iter.next());
        }

        return list;
    }

    public static List<String> getShapeTypes() {
        List<String> list = new ArrayList<>();
        Iterator iter = shapeTypes.iterator();

        while (iter.hasNext()) {
            list.add((String) iter.next());
        }

        return list;
    }

    public static List<String> getColorTypes() {
        List<String> list = new ArrayList<>();
        Iterator iter = colorTypes.iterator();
        while (iter.hasNext()) {
            list.add((String) iter.next());
        }
        return list;
    }

    public Set<String> getPropertyNames() {
        return propertyMap.keySet();
    }

    public void updateLabels() {
        updateAtomArray();
        for (Atom atom : atoms) {
            atom.label = "";

            switch (label) {
                case LABEL_NONE: {
                    atom.label = "";

                    break;
                }

                case LABEL_LABEL: {
                    atom.label = atom.getFullName();

                    break;
                }

                case LABEL_FC: {
                    if (atom.fcharge != 0.0) {
                        atom.label = String.valueOf(atom.fcharge);
                    }

                    break;
                }

                case LABEL_SYMBOL: {
                    atom.label = String.valueOf(Atom.getElementName(atom.aNum));

                    break;
                }

                case LABEL_NUMBER: {
                    atom.label = String.valueOf(atom.iAtom);

                    break;
                }

                case LABEL_SYMBOL_AND_NUMBER: {
                    atom.label = String.valueOf(Atom.getElementName(atom.aNum))
                            + " " + atom.iAtom;

                    break;
                }

                case LABEL_FFC: {
                    atom.label = String.valueOf(atom.forceFieldCode);

                    break;
                }

                case LABEL_SECONDARY_STRUCTURE: {
                    atom.label = "";

                    break;
                }

                case LABEL_RESIDUE:
                    break;

                case LABEL_CHARGE: {
                    if (atom.charge != 0.0) {
                        atom.label = String.valueOf(atom.charge);
                    }

                    break;
                }

                case LABEL_VALUE: {
                    atom.label = String.valueOf(atom.value);

                    break;
                }

                case LABEL_TITLE: {
                    atom.label = "";

                    break;
                }

                case LABEL_MOLECULE_NAME: {
                    atom.label = name;

                    break;
                }

                case LABEL_STRING: {
                    atom.label = "";

                    break;
                }

                case LABEL_CUSTOM: {
                    atom.label = "";

                    break;
                }

                case LABEL_NAME: {
                    if (atom.aNum == 6) {
                        atom.label = atom.getName().substring(1);
                    } else {
                        atom.label = atom.getName();
                    }

                    break;
                }
                case LABEL_HPPM: {
                    ArrayList<Atom> hydrogens = getAttachedHydrogens(atom);
                    int nH = hydrogens.size();
                    System.err.println("nH " + nH);
                    PPMv ppmV0 = null;
                    PPMv ppmV1 = null;
                    Atom hAtom = null;
                    if (nH > 0) {
                        hAtom = hydrogens.get(0);
                        ppmV0 = hAtom.getPPM(0);
                        if (nH == 2) {
                            hAtom = hydrogens.get(1);
                            ppmV1 = hAtom.getPPM(0);
                        }
                    }
                    if (ppmV0 == null) {
                        atom.label = "";
                    } else if (ppmV1 == null) {
                        atom.label = String.valueOf(ppmV0.getValue());
                    } else {
                        atom.label = String.valueOf(ppmV0.getValue()) + "," + String.valueOf(ppmV1.getValue());
                    }
                    break;
                }
                case LABEL_PPM: {
                    PPMv ppmV = atom.getPPM(0);
                    if (ppmV == null) {
                        atom.label = "";
                    } else {
                        atom.label = String.valueOf(ppmV.getValue());
                    }
                    break;
                }
                default:
                    atom.label = "";
            }
        }

        labelsCurrent = true;
    }

    public static void selectAtomsForTable(MolFilter molFilter, ArrayList selected) throws InvalidMoleculeException {
        selected.clear();
        Vector fselected = Molecule.matchAtoms(molFilter);

        for (int i = 0, n = fselected.size(); i < n; i++) {
            SpatialSet spatialSet = (SpatialSet) fselected.elementAt(i);
            if (spatialSet.atom.isMethyl() && !spatialSet.atom.isFirstInMethyl()) {
                continue;
            }
            selected.add(spatialSet);
        }
    }

    public void copyStructure(int source, int target) {
        Point3 ptS;
        Point3 ptT;
        structures.add(Integer.valueOf(target));
        for (Atom atom : atoms) {
            ptS = atom.getPoint(source);
            ptT = new Point3(ptS.getX(), ptS.getY(), ptS.getZ());
            atom.setPointValidity(target, true);
            atom.setPoint(target, ptT);
        }
    }
}
