def getAnnealStages(dOpt, settings):
    """
    # Parameters:

    * dOpt (dict); dictionary containing parameters and values for the dynamic simulation
    * settings (LinkedHashMap); java HashMap that contains force and dynamic program parameters and their values

    # Returns:

    stages (list); a list of 5 dictionarys that have values for running each stage of the dynamics program

    See also: `refine.anneal(...)` in refine.py
    """
    from refine import createStrictDict
    steps = dOpt['steps']
    stepsEnd = dOpt['stepsEnd']
    stepsHigh = int(round(steps*dOpt['highFrac']))
    stepsAnneal1 = int(round((steps-stepsEnd-stepsHigh)*dOpt['toMedFrac']))
    stepsAnneal2 = steps-stepsHigh-stepsEnd-stepsAnneal1
    medTemp = round(dOpt['highTemp'] * dOpt['medFrac'])

    stage1 = {
        'tempVal'        : dOpt['highTemp'],
        'econVal'        : dOpt['econHigh'],
        'nStepVal'       : stepsHigh,
        'gMinSteps'      : None,
        'switchFracVal'  : None,
        'param'   : {
                                'end':1000,
                                'useh':False,
                                'hardSphere':0.15,
                                'shrinkValue':0.20,
                           },
        'force'   : {
                                'repel':0.5,
                                'dis':1.0,
                                'dih':5,
                            },
        'timestep'       : dOpt['timeStep']
    }

    stage2 = {
        'tempVal'        : [dOpt['highTemp'], medTemp, dOpt['timePowerHigh']],
        'econVal'        : dOpt['econHigh'],
        'nStepVal'       : int(round((steps-stepsEnd-stepsHigh)*dOpt['toMedFrac'])),
        'gMinSteps'      : None,
        'switchFracVal'  : None,
        'param'          : None,
        'force'          : None
    }

    stage3 = {
        'tempVal'        : [medTemp, 1.0, dOpt['timePowerMed']],
        'econVal'        : lambda f: dOpt['econHigh']*(pow(0.5,f)),
        'nStepVal'       : steps-stepsHigh-stepsEnd-stepsAnneal1,
        'gMinSteps'      : dOpt['minSteps'],
        'switchFracVal'  : dOpt['switchFrac'],
        'param'          : {
                            'useh' : False,
                            'hardSphere' : 0.0,
                            'shrinkValue' : 0.0,
                           },
        'force'          : None
    }

    stage4 = {
        'tempVal'        : None,
        'econVal'        : None,
        'nStepVal'       : None,
        'gMinSteps'      : dOpt['minSteps'],
        'switchFracVal'  : None,
        'param'          : {
                            'useh' : True,
                            'hardSphere' : 0.0,
                            'shrinkValue' : 0.0,
                            'shrinkHValue' : 0.0,
                            },
        'force'          : {
                            'repel'  : 1.0,
                            'bondWt' : 25.0,
                            }
    }

    stage5 = {
        'tempVal'        : 0.0,
        'econVal'        : dOpt['econLow'],
        'nStepVal'       : stepsEnd,
        'gMinSteps'      : dOpt['minSteps'],
        'switchFracVal'  : None,
        'param'          : None,
        'force'          : {
                            'repel'  : 2.0,
                            }
    }
    stages = [stage1, stage2, stage3, stage4, stage5]
    stageDict = {}
    for i,stage in enumerate(stages):

        ''' The default settings have the lowest priority '''
        stage['param'] = createStrictDict(stage['param'],'param')
        stage['force'] = createStrictDict(stage['force'],'force')

        ''' General settings made by the user have a medium priority '''
        stage['param'].strictUpdate(settings.get('param'))
        stage['force'].strictUpdate(settings.get('force'))

        ''' Specific stage settings have the highest priority '''
        userStageKey = "stage"+str(i+1)
        userStage = settings.get(userStageKey, {})
        if userStage:
            stage['param'].strictUpdate(userStage.get('param'))
            stage['force'].strictUpdate(userStage.get('force'))
        stageDict[i+1] = stage
    for key in settings:
        if len(key) > 7 and key[0:5] == "stage" and key[6] == ".":
            index = float(key[5:])
            index1 = int(round(index))
            baseStage = stages[index1-1]

            newStage = dict(baseStage)
            newStage['param'] = createStrictDict(baseStage['param'],'param')
            newStage['force'] = createStrictDict(baseStage['force'],'force')

            userStage = settings.get(key, {})
            if userStage:
                for kk in userStage:
                    if kk != 'param' and kk != 'force':
                        newStage[kk] = userStage.get(kk)
                    else:
                        newStage[kk].strictUpdate(userStage.get(kk))
                stageDict[index] = newStage
    keys = stageDict.keys()
    keys.sort()
    stages=[]
    for key in keys:
        stages.append(stageDict[key])
    return stages


initialize = True
def runStage(stage, refiner, rDyn):
    """
    Runs a stage of the dynamics using a stage dictionary that has
    keys as demonstrated above in the getAnnealStages. This function uses a
    global variable stored in the module to assess whether it is the first
    time the method is called. If so, it will initialize the dynamics and
    all other calls will continue the dynamics.

    # Parameters:

    * stage (dict);
    * refiner (refine); instance object of refine class
    * rDyn (RotationalDynamics); instance rotational dynamics object to run dynamics simulation

    # Returns:

    _ (None); Performs

    See also: `anneal(...)` in refine.py
    """
    refiner.setPars(stage['param'])
    refiner.setForces(stage['force'])
    timeStep = rDyn.getTimeStep()/2.0
    gminSteps = stage['gMinSteps']
    if gminSteps:
        refiner.gmin(nsteps=gminSteps, tolerance=1.0e-6)
    tempFunc = stage.get('tempVal')
    if tempFunc is not None:
        if callable(tempFunc):
            tempLambda = tempFunc
        else:
            if isinstance(tempFunc,float):
                tempLambda = tempFunc
            else:
                if len(tempFunc) == 1:
                    upTemp = tempFunc[0]
                    downTemp = 1.0
                    powVal = 4.0
                elif len(tempFunc) == 2:
                    upTemp, downTemp = tempFunc
                    powVal = 4.0
                else:
                    upTemp, downTemp, powVal = tempFunc
                tempLambda = lambda f: (upTemp - downTemp) * pow((1.0 - f), powVal) + downTemp
        econLambda = stage['econVal']
        if econLambda == None:
            econLambda = 0.001
        nSteps = stage['nStepVal']
        global initialize
        if initialize:
            rDyn.initDynamics2(tempLambda, econLambda, nSteps, stage['timestep'])
        else:
            rDyn.continueDynamics2(tempLambda, econLambda, nSteps, timeStep)
        initialize=False
    else:
        rDyn.continueDynamics2(timeStep)
    switchFrac = stage.get('switchFracVal')
    if switchFrac:
        rDyn.run(switchFrac)
    else:
        rDyn.run()
