package id.ui.ac.cs.ppl.motxt.main;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AggregationKind;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sort {
    
    public List<Class> topoSort(List<Class> classes) {
        Set<Class> visited = new HashSet<>();
        Set<Class> tempMark = new HashSet<>();
        List<Class> result = new ArrayList<>();
        List<Class> before = new ArrayList<>();
        for (Class umlClass : classes) {
        	before.add(umlClass);
            visit(umlClass, visited, tempMark, result);
        }
        System.out.println("Before sort: "+before.stream().map(Class::getName).collect(Collectors.joining(", ")));
        System.out.println("After sort: "+result.stream().map(Class::getName).collect(Collectors.joining(", ")));
        return result;
    }

    private void visit(Class umlClass, Set<Class> visited, Set<Class> tempMark, List<Class> result) {
        if (visited.contains(umlClass)) {
            return;
        }

        if (tempMark.contains(umlClass)) {
            throw new IllegalStateException("Cyclic Dependency Detected at " + umlClass.getName());
        }

        tempMark.add(umlClass);

        // 1. Check Inheritance Dependencies
        for (Class parent : umlClass.getSuperClasses()) {
            visit(parent, visited, tempMark, result);
        }

        // 2. Check Association/Aggregation Dependencies (Your OCL logic)
        for (Property field : umlClass.getOwnedAttributes()) {
            Type fieldType = field.getType();
            
            // Only proceed if the field type is another Class and NOT a self-reference (to prevent loops)
            if (fieldType instanceof Class && !fieldType.equals(umlClass)) {
                Class targetClass = (Class) fieldType;
                Association assoc = field.getAssociation();
                
                boolean isDependency = false;
                
                if (assoc != null) {
                    List<Property> memberEnds = assoc.getMemberEnds();
                    
                    // Safety check to ensure the association has enough ends
                    if (memberEnds.size() >= 2) {
                        // OCL: not(field.association.memberEnd->at(2) = field) and (field.aggregation.oclIsInvalid())
                        boolean condition1 = !memberEnds.get(1).equals(field) && 
                                             field.getAggregation() == AggregationKind.NONE_LITERAL;
                        
                        // OCL: (field.association.memberEnd->first() = field) and not(field.aggregation.oclIsUndefined())
                        boolean condition2 = memberEnds.get(0).equals(field) && 
                                             field.getAggregation() != AggregationKind.NONE_LITERAL;
                                             
                        if (condition1 || condition2) {
                            isDependency = true;
                        }
                    }
                } else {
                    // If there is no explicit association line but it's directly typed as another Class
                    isDependency = true; 
                }

                // If it meets your criteria, visit the target class first
                if (isDependency) {
                    visit(targetClass, visited, tempMark, result);
                }
            }
        }

        tempMark.remove(umlClass);

        visited.add(umlClass);
        result.add(umlClass); 
    }
}