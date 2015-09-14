package me.guichaguri.bytecodetofile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * @author Guilherme Chaguri
 */
public class BTFTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        File output = new File(BTFTweaker.output, transformedName.replaceAll("\\.", "/") + ".class");

        try {
            if(!output.exists()) {
                output.getParentFile().mkdirs();
                output.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(bytes);
            fos.close();
        } catch(IOException ex) {
            System.out.println("Could not save " + name + " to " + output.getAbsolutePath());
            ex.printStackTrace();
        }

        return bytes;
    }

}
