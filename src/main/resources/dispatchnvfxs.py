import sys
import os
import runpy

sys.argv.pop(0)
if len(sys.argv) > 0:
    if sys.argv[0] == "batch":
        import runall
    elif sys.argv[0] == "score":
        import runTests
    elif sys.argv[0] == "summary":
        import checke
        checke.outDir = os.getcwd()
        checke.summary(sys.argv[1:])
    elif sys.argv[0] == "gen":
        import gennvfx
    elif sys.argv[0] == "predict":
        import predictor
    elif sys.argv[0] == "super":
        import super
        args = super.parseArgs()
        excludeRes = args[0]
        excludeAtoms = args[1]
        includeRes = args[2]
        includeAtoms = args[3]
        files = args[4]
        if len(files) > 1:
            super.runSuper(excludeRes, excludeAtoms, includeRes, includeAtoms, files)


    elif sys.argv[0] == "train":
        print sys.argv
        if (len(sys.argv) > 2) and (sys.argv[1] == "rna"):
            sys.argv.pop(0)
            import train_rna
    else:
        scriptName = sys.argv[0]
        runpy.run_path(scriptName)
