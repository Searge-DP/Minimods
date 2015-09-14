package me.guichaguri.villagerlanterns;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Guilherme Chaguri
 */
public class VillagerLanterns extends DummyModContainer implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"me.guichaguri.villagerlanterns.LanternTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "me.guichaguri.villagerlanterns.VillagerLanterns$LanternContainer";
    }

    @Override
    public String getSetupClass() {
        return "me.guichaguri.villagerlanterns.VillagerLanterns";
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    public static class LanternContainer extends DummyModContainer {
        private static ModMetadata createMetadata() {
            ModMetadata meta = new ModMetadata();
            meta.modId = "villagerlanterns";
            meta.name = "Villager Lanterns";
            meta.version = "1.0.0";
            meta.authorList = Arrays.asList("Guichaguri");
            meta.description = "Replaces villager torch posts with ImmersiveEngineering's lanterns";
            return meta;
        }

        public LanternContainer() {
            super(createMetadata());
        }

        @Override
        public boolean registerBus(EventBus bus, LoadController controller) {
            bus.register(this);
            return true;
        }

        @Subscribe
        public void serverStart(FMLServerStartedEvent event) {
            LanternHooks.serverStart();
        }

        @Subscribe
        public void serverStop(FMLServerStoppingEvent event) {
            LanternHooks.serverStop();
        }
    }

}
