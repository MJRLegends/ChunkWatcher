package cf.terminator.chunkwatcher;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class ChunkLoadListener {

    @SubscribeEvent
    public void onLoad(ForgeChunkManager.ForceChunkEvent event){
        if(TicketManager.checkTicket(event.getTicket()) == false){
            for(ChunkPos chunkPos : event.getTicket().getChunkList()) {
                new unloader(chunkPos, event.getTicket()).unload();
            }
        }
    }
}

class unloader{
    private final ChunkPos pos;
    private final ForgeChunkManager.Ticket ticket;

    unloader(ChunkPos p, ForgeChunkManager.Ticket t){
        pos = p;
        ticket = t;
    }

    public void unload(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void run(TickEvent.ServerTickEvent e){
        ForgeChunkManager.unforceChunk(ticket, pos);
        MinecraftForge.EVENT_BUS.unregister(this);
        Main.LOGGER.info("Unloaded chunk: (X=" + pos.chunkXPos + ", Z=" + pos.chunkZPos + ") mod: " + ticket.getModId());
    }
}
