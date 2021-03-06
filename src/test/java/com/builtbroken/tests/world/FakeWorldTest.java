package com.builtbroken.tests.world;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.junit.world.FakeWorld;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import org.junit.Test;
import org.junit.runner.RunWith;

/** JUnit test for {@link FakeWorld}
 * Created by robert on 11/13/2014.
 */
@RunWith(VoltzTestRunner.class)
public class FakeWorldTest extends AbstractTest
{
    World world = null;

    public FakeWorldTest()
    {

    }

    @Override
    public void setUpForEntireClass()
    {
        world = FakeWorld.newWorld("FakeWorldTest");
    }

    @Test
    public void testBlockRegistry()
    {
        Object block = Block.blockRegistry.getObject("sand");
        assertNotNull(block);
        assertEquals(Block.getIdFromBlock((Block) block), 12);
    }

    @Test
    public void testCreation()
    {
        assertNotNull("Failed to create world", world);
    }

    @Test
    public void testNullPlacement()
    {
        try
        {
            world.setBlock(0, 0, 0, null);
            fail("World didn't catch null block");
        }
        catch (NullPointerException e)
        {
            //This should be thrown :)
        }
    }

    @Test
    public void testBlockPlacement()
    {
        if (Blocks.sand != null)
        {
            world.setBlock(0, 0, 0, Blocks.sand);
            Block block = world.getBlock(0, 0, 0);
            assertEquals("World.getBlock() failed ", Blocks.sand, block);
        } else
        {
            fail("Blocks.sand is null");
        }
    }

    @Test
    public void testTilePlacement()
    {
        if (Blocks.chest != null)
        {
            world.setBlock(0, 0, 0, Blocks.chest);
            Block block = world.getBlock(0, 0, 0);
            assertEquals("World.getBlock() failed ", Blocks.chest, block);
            if (!(world.getTileEntity(0, 0, 0) instanceof TileEntityChest))
            {
                fail("world.getTileEntity() returned the wrong tile\n" + world.getTileEntity(0, 0, 0) + "  should equal TileEntityChest");
            }
        } else
        {
            fail("Blocks.chest is null");
        }
    }

    @Test
    public void testBlockRemoval()
    {
        world.setBlock(0, 0, 0, Blocks.grass);
        assertEquals("World.getBlock() failed ", Blocks.grass, world.getBlock(0, 0, 0));
        world.setBlockToAir(0, 0, 0);
        assertEquals("World.getBlock() failed ", Blocks.air, world.getBlock(0, 0, 0));
    }

    @Test
    public void testTileRemoval()
    {
        world.setBlock(0, 0, 0, Blocks.chest);
        assertEquals("World.getBlock() failed ", Blocks.chest, world.getBlock(0, 0, 0));
        assertTrue("World.getTile() should have returned a chest tile ", world.getTileEntity(0, 0, 0) instanceof TileEntityChest);
        world.setBlockToAir(0, 0, 0);
        world.updateEntities();
        assertEquals("World.getBlock() failed ", Blocks.air, world.getBlock(0, 0, 0));

        TileEntity tile = world.getTileEntity(0, 0, 0);
        //System.out.println("Tile: " + tile + " Invalid: " + tile.isInvalid());
        assertTrue("World.getTile() should be null ", tile == null);
    }
}
