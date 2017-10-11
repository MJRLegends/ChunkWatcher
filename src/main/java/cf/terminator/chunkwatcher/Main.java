package cf.terminator.chunkwatcher;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.*;

@Mod(modid = Main.MODID_LOWERCASE, version = Main.VERSION, name = Main.MODID, acceptableRemoteVersions = "*")
class Main
{
    static final String MODID = "ChunkWatcher";
    static final String MODID_LOWERCASE = "chunkwatcher";
    static final String VERSION = "3.0";
    static Logger LOGGER;
    static File CONFIG_FILE;
    static JsonObject CONFIG;
    private ChunkLoadListener listener = new ChunkLoadListener();

    @EventHandler
    public void preinit(FMLPreInitializationEvent event){
        LOGGER = event.getModLog();
        LOGGER.info(MODID + " version " + VERSION + " started!");
        CONFIG_FILE = event.getSuggestedConfigurationFile();
        MinecraftForge.EVENT_BUS.register(listener);
        try {
            CONFIG = loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void init(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandHandler());
    }

    static JsonObject loadConfig() throws IOException{
        if(CONFIG_FILE.exists() == false){
            JsonObject json = new JsonObject();
            json.add("Notify chunkload", new JsonPrimitive(false));
            json.add("Blacklist", new JsonArray());

            writeConfig(json);
            return loadConfig();
        }
        FileReader fr = new FileReader(CONFIG_FILE);
        JsonReader reader = new JsonReader(fr);
        JsonObject json = new Gson().fromJson(reader, JsonObject.class);
        reader.close();
        fr.close();
        return json;
    }

    static void writeConfig(JsonObject json) throws IOException{
        if(CONFIG_FILE.exists()){
            if(CONFIG_FILE.delete() == false){
                throw new IOException("Unable to replace file: " + CONFIG_FILE);
            }
        }
        if(CONFIG_FILE.createNewFile() == false){
            throw new IOException("Unable to replace file: " + CONFIG_FILE);
        }
        OutputStream out = new FileOutputStream(CONFIG_FILE);
        out.write(json.toString().getBytes());
        out.close();
    }

}
