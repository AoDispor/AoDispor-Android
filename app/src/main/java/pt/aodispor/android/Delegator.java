package pt.aodispor.android;

import org.javatuples.Pair;
import org.javatuples.Triplet;
//import org.javatuples.Triplet;

import java.lang.reflect.Method;
import java.util.ArrayList;

import pt.aodispor.android.api.HttpRequest;

public class Delegator {

    Class[] args;
    //ArrayList<Triplet<Class,Object,Method>> methods;
    ArrayList<Triplet<Boolean,Object, Method>> methods;
    //method.invoke(foo, null);
    //Class methodClass, String methodName,

    public Delegator(Class... args) {
        this.args = args;
        methods = new ArrayList<>();
        /*
        try {
            method = methodClass.getClass().getMethod(methodName, args);
        } catch (Exception e) {}*/
    }


    public void addMethod(boolean isPrivate, Class theClass, String methodName) {
        try {
            Method method = isPrivate ?
                    theClass.getClass().getDeclaredMethod(methodName, args) :
                    theClass.getClass().getMethod(methodName, args);
            if (isPrivate) method.setAccessible(true);
            methods.add(
                    new Triplet<Boolean, Object, Method>(
                            true,
                            theClass,
                            method
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("failed to add method " + methodName + " - of class - " + theClass
                    + "\n:: Exception ::\n" + e.toString());
        }
    }

    public void addMethod(boolean isPrivate, Object object, String methodName) {
        try {
            Method method = isPrivate ?
                    object.getClass().getDeclaredMethod(methodName, args) :
                    object.getClass().getMethod(methodName, args);
            if (isPrivate) method.setAccessible(true);
            methods.add(
                    new Triplet<Boolean, Object, Method>(
                            false,
                            object,
                            method
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("failed to add method " + methodName + " - of object - " + object.toString());
        }
    }

    public void execute(Object... args) {
        try {
            for (Triplet<Boolean, Object, Method> method : methods) {
            /*if(method.getValue0() == Class.class){
                if(theClass)
            }*/
                method.getValue2().invoke(
                        method.getValue0() ? null: method.getValue1()
                        , args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}