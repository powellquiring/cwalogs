package cwalog
import Line.*

/**
 * group lines into a transaction.  Usage:
 * Group g = new Group()
 * g.addLine(l1)
 * g.addLine(l2)
 * ... etc
 * g.processLines()
 * g.completeValues.each{Group.Value value -> ...} // process each of the complete transaction values
 * g.incompleteValues.each{Group.Value value -> ...} // process each of the incomplete values
 */
class Group {
    static class Data {
        List<ValueAbstract> completeValues = []
        Map<String,Group.ValueAbstract> incompleteValuesMap = [:] // sorted by guid
    }
    static abstract class ValueAbstract {
        ValueAbstract(Data data) {
            this.data = data
        }
        abstract ValueAbstract newValueAbstract()
        Data data
        Line start
        Line complete
        abstract boolean add(Line line);
        boolean valueIsComplete(ValueAbstract v) {
            ((v.start) && (v.complete))
        }
        protected String makeIncompleteKey(Line line) {
            line.guid
        }

        void endOfInput() {
            def iterator = data.incompleteValuesMap.entrySet().iterator()
            while (iterator.hasNext()) {
                ValueAbstract v = iterator.next().value
                if (valueIsComplete((ValueAbstract)v)) {
                    iterator.remove()
                    data.completeValues << v
                }
            }
        }
        protected ValueAbstract getOrCreateValueFromLine(Line line) {return data.incompleteValuesMap.get(makeIncompleteKey(line), newValueAbstract())}
        
        List<Group.ValueAbstract> getIncompleteValues() {
            def values = data.incompleteValuesMap.values()
            if (values) {
                return new ArrayList<Group.ValueAbstract>(values)
            } else {
                return new ArrayList<Group.ValueAbstract>()
            }
        }
    
        Map<String,Group.ValueAbstract> getIncompleteValuesMap() {
            return data.incompleteValuesMap
        }
    
        List<Group.ValueAbstract> getCompleteValues(){return data.completeValues}
    
    }

    static class Value extends ValueAbstract{
        ValueAbstract newValueAbstract() {
            return new Value(data)
        }
        Value(Data data){super(data)}
        Line notRegistered
        int packageAvailable
        int packageFailure
        int packageInCache  // 5/29/2014 logs or later
        int packagesBeingOrdered  // 5/29/2014 logs or later
        // these were found to be too big to store in the heap so just storing counts
        // List<String> fOOUpdatesAvailable            // list of updates available
        // List<String> fOOUpdatePackageNotOrdered     // list of updates that failed
        boolean add(Line line) {
            assert(line.guid)
            switch(line.patternName) {
                case (PatternName.FOOQueryStart):
                    Value v = (Value)getOrCreateValueFromLine(line)
                    if (v.start != null) { // complete the currently stored value
                        assert valueIsComplete((ValueAbstract)v)
                        data.incompleteValuesMap.remove(makeIncompleteKey(v.start))
                        data.completeValues << v
                        // new empty value
                        v = getOrCreateValueFromLine(line)
                    }
                    assert(v.start == null)
                    v.start = line
                    break

                case (PatternName.FOOUpdatesAvailable):
                    final Value v = getOrCreateValueFromLine(line)
                    v.packageAvailable += line.updatePackage.split(', ').flatten().size()
                    break
                case (PatternName.FOOUpdatePackageNotOrdered):
                    final Value v = getOrCreateValueFromLine(line)
                    v.packageFailure += 1
                    break
                    
                // new FOO in 5/29/2014
                    case (PatternName.FOOFixesFromCache):
                    final Value v = getOrCreateValueFromLine(line)
                    v.packageInCache = line.counter
                    break
                // new FOO in 5/29/2014
                    case (PatternName.FOOFixesBeingOrdered):
                    final Value v = getOrCreateValueFromLine(line)
                    v.packagesBeingOrdered = line.counter
                    break


                case (PatternName.QCProcessingErrorNotRegistered): // sometimes in middle sometimes after a complete
                    final Value v = getOrCreateValueFromLine(line)
                    v.notRegistered = line
                    break


                // completion success
                case (PatternName.FOOQueryComplete):    // completed successfully
                case (PatternName.ECCManagerBadWebId):  // completed with bad web id, successful but too bad for the guy with the bad id
                // completion with failures
                case (PatternName.ECCManagerGenOperationFailed):  // completed but failed
                case (PatternName.ECCManagerFaultCANA):  // completed but failed
                    final Value v = getOrCreateValueFromLine(line)
                    if (v.complete != null) {
                        println("Two completes without a start, can happen at beginning of day, below is 1st and 2nd")
                        println(v.complete)
                        println(line)
                    }
                    v.complete = line
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    static class ValueEntitled extends ValueAbstract {
        ValueAbstract newValueAbstract() {
            return new ValueEntitled(data)
        }
        ValueEntitled(Data data){super(data)}
        boolean cached
        boolean add(Line line){
            assert(line.guid)
            switch(line.patternName) {
                case (PatternName.EntitledRepositoryServiceStarting):
                    ValueEntitled v = getOrCreateValueFromLine(line)
                    assert(v.start == null)
                    v.start = line
                    break;
                case (PatternName.EntitledRepositoryServiceFoundCached):
                    ValueEntitled v = getOrCreateValueFromLine(line)
                    assert(!v.cached)
                    v.cached = true
                    break;
                case (PatternName.EntitledRepositoryServiceCompleted):    // completed successfully
                    ValueEntitled v = getOrCreateValueFromLine(line)
                    v.complete = line
                    if (v.start != null) {
                        incompleteValuesMap.remove(makeIncompleteKey(v.start))
                        completeValues << v
                    }
                    break;
                case (PatternName.FOOFixesBeingOrdered):    // not processing
                    break;
                default:
                    println line
                    return false;
            }
            return true;

        }
    }

    private Value valueHandler = new Value(new Data())
    private ValueEntitled valueEntitledHandler = new ValueEntitled(new Data())
    
    private final List<ValueAbstract> handlers = [
        valueHandler,
        valueEntitledHandler
    ]

    void addLine(Line line) {
        for(ValueAbstract handler in handlers) {
            if (handler.add(line)) {
                break;
            }
        }
    }
    
    void endOfInput() {
        for(ValueAbstract handler in handlers) {
            handler.endOfInput();
        }
    }
    
    ValueAbstract getValueForClass(java.lang.Class x) {
        for(ValueAbstract handler in handlers) {
            java.lang.Class c = handler.getClass()
            if (c == x) {
                return handler;
            }
        }
    }

    // For backward compatibility the stuff below was originally defined for class Value
    
    List<Group.Value> getIncompleteValues() {
        return valueHandler.getIncompleteValues()
    }

    Map<String,Group.Value> getIncompleteValuesMap() {
        return valueHandler.getIncompleteValuesMap()
    }

    // list of the completed values
    List<Group.Value> getCompleteValues(){return valueHandler.getCompleteValues()}


}
