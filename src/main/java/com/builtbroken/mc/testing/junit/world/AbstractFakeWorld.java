package com.builtbroken.mc.testing.junit.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Cow Pi on 8/24/2015.
 */
public class AbstractFakeWorld extends World
{
    public boolean debugInfo = false;
    public Logger logger;

    public AbstractFakeWorld(ISaveHandler p_i45369_1_, String p_i45369_2_, WorldSettings p_i45369_3_, WorldProvider p_i45369_4_, Profiler p_i45369_5_)
    {
        super(p_i45369_1_, p_i45369_2_, p_i45369_3_, p_i45369_4_, p_i45369_5_);
        logger = LogManager.getLogger("FW-" + p_i45369_2_);
    }

    public AbstractFakeWorld(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_, WorldSettings p_i45368_4_, Profiler p_i45368_5_)
    {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
        logger = LogManager.getLogger("FW-" + p_i45368_2_);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block, int meta, int notify)
    {
        debug("");
        debug("setBlock(" + x + ", " + y + ", " + z + ", " + block + ", " + meta + ", " + notify);
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000)
        {
            if (y < 0)
            {
                debug("setBlock() y level is too low");
                return false;
            } else if (y >= 256)
            {
                debug("setBlock() y level is too high");
                return false;
            } else
            {
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                debug("setBlock() chunk = " + chunk);
                Block block1 = null;
                net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;

                if ((notify & 1) != 0)
                {
                    block1 = chunk.getBlock(x & 15, y, z & 15);
                }

                if (this.captureBlockSnapshots && !this.isRemote)
                {
                    blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(this, x, y, z, notify);
                    this.capturedBlockSnapshots.add(blockSnapshot);
                }

                boolean flag = chunk.func_150807_a(x & 15, y, z & 15, block, meta);
                debug("setBlock() flag = " + flag + " BlockSnapshot = " + blockSnapshot);

                if (!flag && blockSnapshot != null)
                {
                    this.capturedBlockSnapshots.remove(blockSnapshot);
                    blockSnapshot = null;
                }

                this.theProfiler.startSection("checkLight");
                this.func_147451_t(x, y, z);
                this.theProfiler.endSection();

                if (flag && blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    // Modularize client and physic updates
                    this.markAndNotifyBlock(x, y, z, chunk, block1, block, notify);
                }

                return flag;
            }
        } else
        {
            debug("setBlock() too far from zero zero");
            return false;
        }
    }

    @Override
    protected IChunkProvider createChunkProvider()
    {
        return null;
    }

    @Override
    protected int func_152379_p()
    {
        return 0;
    }

    @Override
    public Entity getEntityByID(int p_73045_1_)
    {
        return null;
    }

    protected void debug(String msg)
    {
        if (debugInfo)
            logger.info(msg);
    }
}
