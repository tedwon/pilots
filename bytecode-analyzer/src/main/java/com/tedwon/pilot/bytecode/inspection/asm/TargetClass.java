package com.tedwon.pilot.bytecode.inspection.asm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ted
 * Date: 11. 05. 21
 * Time: PM 9:03
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class TargetClass extends javax.accessibility.AccessibleResourceBundle implements org.xml.sax.AttributeList  {

    private String[] testValue = javax.management.openmbean.OpenType.ALLOWED_CLASSNAMES;

    @Deprecated
    public List<Integer> myMethod(String value) {

        List<Integer> list = new ArrayList<Integer>();


        try {
            Thread t = new Thread();
            t.countStackFrames();
        } catch (Exception e) {
            throw new java.rmi.RMISecurityException("");
        }

        return list;

    }

    public int getLength() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getType(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getValue(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getType(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getValue(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
