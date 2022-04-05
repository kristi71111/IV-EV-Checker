package kristi71111.ivevchecker;

import kristi71111.ivevchecker.commands.MainCommand;
import kristi71111.ivevchecker.events.EntityInteract;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = Ivevchecker.MOD_ID,
        name = Ivevchecker.MOD_NAME,
        version = Ivevchecker.VERSION,
        acceptableRemoteVersions = "*",
        dependencies = "after:pixelmon"
)
public class Ivevchecker {

    public static final String MOD_ID = "ivevchecker";
    public static final String MOD_NAME = "Ivevchecker";
    public static final String VERSION = "1.0.0";
    public static Logger logger;

    @Mod.Instance(MOD_ID)
    public static Ivevchecker INSTANCE;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        File configDir = new File(new File(event.getModConfigurationDirectory(), MOD_ID), MOD_ID + ".cfg");
        ConfigRegistry.init(new Configuration(configDir));
        MinecraftForge.EVENT_BUS.register(new EntityInteract());
    }

    @Mod.EventHandler
    public void postinit(FMLServerStartingEvent event) {
        event.registerServerCommand(new MainCommand());
    }

}
