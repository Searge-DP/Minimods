package me.guichaguri.bytecodetofile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * @author Guilherme Chaguri
 */
public class BTFTweaker implements ITweaker {
    protected static File output;
    private List<String> args;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = new ArrayList<String>(args);
        this.args.add("--version");
        this.args.add(profile);
        if(assetsDir != null) {
            this.args.add("--assetsDir");
            this.args.add(assetsDir.getAbsolutePath());
        }
        if(gameDir != null) {
            this.args.add("--gameDir");
            this.args.add(gameDir.getAbsolutePath());

            output = new File(gameDir, "classes");
        } else {
            output = new File("classes");
        }
    }
    @Override
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        loader.registerTransformer("me.guichaguri.bytecodetofile.BTFTransformer");
    }
    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }
    @Override
    public String[] getLaunchArguments() {

        ArrayList args = (ArrayList) Launch.blackboard.get("ArgumentList");
        if(args.isEmpty()) args.addAll(this.args);

        this.args = null;

        return new String[0];
    }
}
