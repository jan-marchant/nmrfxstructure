import re
#from org.nmrfx.structure.chemistry.energy import EnergyLists
from java.util import ArrayList
from org.nmrfx.structure.chemistry.energy import AngleBoundary
from org.nmrfx.structure.chemistry.io import AtomParser
from java.lang import NullPointerException, IllegalArgumentException

def getAtomPairs(atoms,mode='shuffle'):
    ''' getAtomPairs finds all the atom pairs from two lists of atoms
        atoms must be a list of two lists of full atom names.
        This returns the list of strings for atom pairs that complies with the
        working of refine.py'''

    atomPairs = []
    startAtoms, endAtoms = atoms
    if mode == 'shuffle':
        for startAtom in startAtoms:
            for endAtom in endAtoms:
                atomPair = ' '.join([startAtom,endAtom]) if startAtom < endAtom else ' '.join([endAtom, startAtom])
                atomPairs.append(atomPair)
    elif mode == 'pairwise':
        for startAtom, endAtom in zip(startAtoms,endAtoms):
            atomPair = ' '.join([startAtom,endAtom]) if startAtom < endAtom else ' '.join([endAtom, startAtom])
            atomPairs.append(atomPair)
    return atomPairs

def getBounds(xplorBounds, type):
    '''getBounds returns the lower and upper bounds for a constraint once given
        values parsed from the constraint file'''
    a,b,c = xplorBounds.strip().split()
    if type == 'distance':
        return [float(a) - float(b), float(a) + float(c)]
    else:
        return [float(b) - float(c), float(b) + float(c)]

class XPLOR:
    def __init__(self, f):
	self.fileName = f
        self.f = open(self.fileName, 'r')
        self.s = ""
	self.invalidDistAtomPairs = []
        self.invalidAtomSelections = []
        self.regex = r"\([^\(]*resi\w*\s+([0-9]+)\s+[\w\s]+\s(\w+[0-9'\*#]*)\s*\)|(or)|(-?[0-9\.]+\s+-?[0-9\.]+\s+-?[0-9\.]+)"
        ''' Four matching groups within regex: residueNum , atomName, "or", bounds'''

    def addElements(self, captures, resNames, segId):
        ''' Takes in a list of lists. Each internal list has 4 items.
            capture[0] : Residue number
            capture[1] : Atom name
            capture[2] : "or"
            capture[3] : bounds, a string of three numbers
            Each element will either have a value or an empty string. An empty string
            means nothing was found of that type in that capture. This function
            produces a single list of alternating atomnames and ors and possibly ends in
            a capture with just bounds information.  Captures comes from an entire
            line and resNames is needed to rename atoms in IUPAC standards'''
	
        appending = []
        for capture in captures:
            item = ""
            if capture[0]:
                resNum = capture[0]
                try:
                    resName = resNames[resNum]
                except KeyError:
                    errMsg = "\nPlease review the XPLOR sequence information and/or constraint file [{}].".format(
                        self.fileName)
                    errMsg += "\nWe couldn't find residue number '{0}' for residue name '{1}'.".format(resNum,
                                                                                                       capture[1])
                    errMsg += "\n\nNote: Make sure the residue number exists in the sequence input file."
                    raise LookupError(errMsg)
                atomName = AtomParser.xplorToIUPAC(resName, capture[1])
                atomName = atomName if atomName else capture[1]
                item = '.'.join([capture[0], atomName])
                if segId:
		    item = ':'.join([segId, item])
            else:
                item = capture[2] if capture[2] else capture[3]
            appending.append(item)
        return appending

    def parseConstraints(self, constraints, type):
        ''' parseConstraints takes the xplorInternalized constraint lists and
            creates more easy to parse contraint dictionaries to be used in either
            refine.py (when type is "distance") or internally in xplor.py (type is "angles")'''
        constraintDicts = []
        for constraint in constraints:
            atoms = [[], []] if type == 'distance' else constraint[:-1]
            constraintValues = ""
            if type == 'distance':
                prevAtom = False
                placement = 0
                afterValues = False
                pairMode = 'pairwise'
                for element in constraint:
                    if len(element.split()) == 3:
                        ''' This indicates that the values here are the constraint
                            values. We set an afterValues bool as we treat ors
                            before slightly differently than the ors after
                        '''
                        afterValues = True
                        constraintValues = element
                        placement = 0;
                    elif element.lower() != 'or':
                        if prevAtom:
                            placement += 1
                        atoms[placement].append(element)
                        prevAtom = True
                    else:
                        if not afterValues:
                            pairMode = 'shuffle'
                            prevAtom = False
                        else:
                            placement = 0;
                            prevAtom = False
                atomPairs = getAtomPairs(atoms, mode=pairMode)
                if not atomPairs:
                    atom1, atom2 = atoms
                    if atom1 and atom2:
                        if atoms not in self.invalidDistAtomPairs:
                            self.invalidDistAtomPairs.append(atoms)
                    elif atom1 or atom2:
                        if atom1:
                            if atom1 not in self.invalidDistAtomPairs:
                                self.invalidDistAtomPairs.append(atom1)
                        else:
                            if atom2 not in self.invalidDistAtomPairs:
                                self.invalidDistAtomPairs.append(atom2)
            else:
                atomPairs = atoms
                constraintValues = constraint[-1]
            lower, upper = getBounds(constraintValues, type)
            constraint = {'atomPairs': atomPairs, 'lower': lower, 'upper': upper}
            constraintDicts.append(constraint)
        return constraintDicts

    def getNextString(self):
        ''' getNextString finds the next data-containing line in the file'''
        while True:
            temp = self.f.readline()
            if temp == '':
                ''' reached EOF '''
                self.s = None
                break
            temp = temp.strip()
            if len(temp) == 0:
                continue
            elif temp[0] == '!':
                continue;
            else:
                self.s = temp
                break

    def processAngleConstraints(self, dihedral, atomsSels, bounds, scale=1):
        ''' processAngleConstraints verifies an angle boundary can exist between
            four provide atoms (atomsSels) and then adds in the constraint'''
        # EX: fullAtoms = ["2koc:1.C5'","2koc:1.C4'","2koc:1.C3'","2koc:1.O3'"]
	try:
            validAtomSelections = AngleBoundary.allowRotation(atomsSels)
	except NullPointerException:
	    errMsg = "Invalid list of atom selections passed to 'AngleBoundary.allowRotation(...)': {}".format(atomsSels)
	    raise ValueError(errMsg)
	except IllegalArgumentException as IAE:
	    errMsg = IAE.getMessage()
	    raise ValueError(errMsg)

	if validAtomSelections:
            lower, upper = bounds
            if lower == upper:
                lower = lower - 20
                upper = upper + 20
            if (lower < -180) and (upper < 0.0):
                lower += 360
                upper += 360
            dihedral.addBoundary(atomsSels, lower, upper, scale)
        else:
            self.invalidAtomSelections.append(atomsSels)
            #raise ValueError("Rotation about atom selections not permissible.")

    def getSegmentId(self, string):
        segIdRegex = r'[^\s]*?segid\s[^.]?\s*([A-Z]).?'
        pat = re.compile(segIdRegex, re.I)
        match = pat.search(string)
	if match:
            return match.group(1)
	else:
	    return None

    def parseXPLORFile(self, resNames):
        ''' parseXPLORFile parses the xplor file to produce constraint lists
            in the xplor.py internalized format: a series of atoms and "or"
            followed by a final element that stores the bounds in a string with
            each value delimited by whitespace'''
        regex = self.regex
        pat = re.compile(regex, re.IGNORECASE)
        constraints = []
        f1 = self.f
        self.distances = True
        elements = []
        while True:
            self.getNextString()
            if not self.s:
                if elements:
                    constraints.append(elements)
                break
            elif 'assi' in self.s or 'ASSI' in self.s:
                if elements:
                    constraints.append(elements)
                elements = []
            m = pat.findall(self.s)
            segmentId = self.getSegmentId(self.s)
            elements += self.addElements(m, resNames, segmentId)
        self.f.close()
        return constraints

    def readXPLORDistanceConstraints(self, resNames):
        ''' readXPLORDistanceConstraitns parses an xplor distance file and
            returns a list of dictionaries containing the keys atomPairs, lower,
            and upper. This format is easy to parse in the refine.py code '''
        constraintInfo = self.parseXPLORFile(resNames)
        constraints = self.parseConstraints(constraintInfo, 'distance')
	
	if self.invalidDistAtomPairs:
	    # XXX: Is this really the best way to write out the error message? Are we taking all possibilities into account
	    errMsg = "\nPlease evaluate the following XPLOR distance constraints [{}]:\n".format(self.fileName)
	    strAtom = ""
	    for atom in self.invalidDistAtomPairs:
		if len(atom) > 1:
		    if atom[0] and atom[1]:
			# case 1: both lists in atoms arent empty
			strAtom = ', '.join(self._resStringConverter(atom))
		    else:
			# case 2: 1 of the 2 lists in atoms isnt empty
			strAtom = ''.join(self._resStringConverter(atom))
		else:
		    # special case: only 1 list (product of ensuring there's no repetition)
                    strAtom = ''.join(self._resStringConverter(atom))
		errMsg += "\t- Check invalid atom(s): {}\n".format(strAtom)
	    raise LookupError(errMsg)

        return constraints

    def _resStringConverter(self, resName):
	"""
	Input -> resName (list): list of strings atoms with our internal format (i.e [1.HA, 2.HB, etc.])
	Output -> _ (list): list of reformatted strings (i.e ['(residue #: 1, residue name: HA)', '(residue #: 2, residue name: HB)', etc.]) 
	"""
	return map(lambda x: "(residue #: {0}, residue name: {1})".format(x.split('.')[0], x.split('.')[-1]), resName)

    def readXPLORAngleConstraints(self, dihedral, resNames):
        ''' readXPLORAngleConstraints parses an xplor angle file and adds in the
            constraints directly into the provided dihedral object '''
        constraints = self.parseXPLORFile(resNames)
        constraints = self.parseConstraints(constraints,'angles')
        for constraint in constraints:
            atomSels = constraint['atomPairs']
            bounds = [constraint['lower'],constraint['upper']]
            self.processAngleConstraints(dihedral, atomSels, bounds)
	# XXX: Could we maybe print out the line number too?
        if self.invalidAtomSelections:
            invalidAtoms = ''
            for atomSels in self.invalidAtomSelections:
		atomSels = self._resStringConverter(atomSels)
                atoms = ', '.join(atomSels)
                invalidAtoms += "\n\t- Dih. Atom Selections: " + atoms + '\n'
            errMsg = "\nPlease check invalid atom selection(s) in your XPLOR angle constraint file [{0}].".format(self.fileName)
	    errMsg += "{}".format(invalidAtoms)
	    errMsg += "\nNote: These atom selections could be invalid due to the order of point atoms."
            raise ValueError(errMsg)
