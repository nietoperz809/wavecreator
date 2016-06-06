package com.WaveCreator.Helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 *
 * @author Administrator
 */
public class DynamicFunction
{
    String dir = System.getProperty("java.io.tmpdir");
    String classname = "Hello";
    String fname = classname + ".java";
    File sourceFile = new File(dir + fname);
    Method thisMethod = null;
    Object iClass = null;


    public void createSource(String func) throws IOException
    {
        FileWriter writer = new FileWriter(sourceFile);
        writer.write(
                "import static java.lang.Math.*; \n"
                        + "public class Hello{ \n"
                        + " public double doit(Double x) { \n"
                        + "   double d = " + func + ";\n"
                        + "   return d;\n"
                        + " }\n"
                        + "}"
        );
        writer.close();
    }

    public void compile() throws IOException
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager
                = compiler.getStandardFileManager(null, null, null);

        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(new File(dir)));
        // Compile the file
        compiler.getTask(null,
                fileManager,
                null,
                null,
                null,
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
                .call();
        fileManager.close();
    }

    public double runIt(double in) throws Exception
    {
        if (thisMethod == null)
        {
            Class<?> params[] =
                    {
                            Double.class,
                    };

            File f = new File(dir);
            URL[] cp =
                    {
                            f.toURI().toURL()
                    };
            URLClassLoader urlcl = new URLClassLoader(cp);
            Class<?> thisClass = urlcl.loadClass(classname);

            iClass = thisClass.newInstance();
            thisMethod = thisClass.getDeclaredMethod("doit", params);
        }
        Object paramsObj[] = {in};
        Double d = (Double) thisMethod.invoke(iClass, paramsObj);
        return d;
    }

    // Test
    public static void main(String[] args) throws Exception
    {
        DynamicFunction df = new DynamicFunction();
        df.createSource("2000*sin(2*PI+x)");
        df.compile();
        for (int s=0; s<100; s++)
        {
            System.out.println(df.runIt(s));
        }
    }

}
