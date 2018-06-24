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

package org.nmrfx.structure.chemistry.io;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.nmrfx.processor.datasets.peaks.InvalidPeakException;
import org.nmrfx.structure.chemistry.constraints.Constraint;
import org.nmrfx.structure.chemistry.constraints.ConstraintSet;

/**
 *
 * @author brucejohnson
 */
public class ConstraintSTARWriter {

    public static void writeConstraintsSTAR3(FileWriter chan, ConstraintSet cSet, int setNum)
            throws IOException, InvalidPeakException {
        String saveFrameName = cSet.getCategory() + setNum;
        String saveFrameCategory = cSet.getCategory();
        String thisCategory = cSet.getListType();
        String constraintType = cSet.getType();
        chan.write("save_" + saveFrameName + "\n");

        chan.write(thisCategory + ".Sf_category    ");
        chan.write(saveFrameCategory + "\n");

        chan.write(thisCategory + ".Sf_framecode   ");
        chan.write(saveFrameName + "\n");

        chan.write(thisCategory + ".Constraint_type   ");
        chan.write('\'' + constraintType + "\'\n");

        chan.write(thisCategory + ".Details        ");
        chan.write(".\n");

        chan.write("\n");

        String[] loopStrings = cSet.getLoopStrings();
        chan.write("loop_\n");
        for (int j = 0; j < loopStrings.length; j++) {
            chan.write(loopStrings[j] + "\n");
        }
        chan.write("\n");
        Iterator iter = cSet.iterator();
        while (iter.hasNext()) {
            Constraint constraint = (Constraint) iter.next();
            if (constraint == null) {
                throw new InvalidPeakException("writeConstraints: constraint null at ");
            }
            chan.write(constraint.toSTARString() + "\n");
        }
        chan.write("stop_\n");
        chan.write("\n");

        chan.write("save_\n\n");
    }
}
