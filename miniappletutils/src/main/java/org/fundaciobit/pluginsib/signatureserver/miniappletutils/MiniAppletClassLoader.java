package org.fundaciobit.pluginsib.signatureserver.miniappletutils;

import java.lang.reflect.Method;

/**
 * 
 * @author anadal
 *
 */
public class MiniAppletClassLoader {

  public Class<?>  loadClass(String name) throws Exception {
    return Class.forName(name);
  }
  
  public Method getMethod(Class<?> cls, String methodName) throws Exception {
    Method method = null;

    for(Method m : cls.getMethods()) {
      if (m.getName().equals(methodName)) {
        method = m;
        break;
      }
    }
    
    if (method == null) {
      throw new Exception("No s'ha trobat el mètode '" + methodName + "' dins la classe "
          + cls);
    }
    return method;
  }

}
