package com.example.examplemod.events;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.event.world.BlockEvent.BreakEvent; 
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
// import net.minecraft.block.BlockOre; // minecraft 1.8
import net.minecraft.block.OreBlock; // mincraft 1.15.2
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
//import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.Entity;

import net.minecraftforge.event.world.ExplosionEvent;
//import net.minecraft.util.Vec3; // not in 1.15.2
import net.minecraft.util.math.Vec3d; // 1.15.2 version

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;


@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TNTTunnel{



    @SubscribeEvent
    public static void explodeTunnel(ExplosionEvent.Detonate event) {
        // 同樣只讓這個事件操作一次
        //if (event.world.isRemote) {
        if (event.getWorld().isRemote) {
            return;
        }

        // 取得產生爆炸的方塊位置
        Vec3d eventPos = event.getExplosion().getPosition();
        // 將要被破壞的方塊全部清空，我們自行定義
        event.getAffectedBlocks().clear();
        // 自定義一個toTunnel方法，動態加入我們想要影響的方塊
        toTunnel(event.getAffectedBlocks(), eventPos);
    }  

    private static void toTunnel(List<BlockPos> affectBlocks, Vec3d originPos) {
        // 建立一個用來存我們隧道形狀的Map
        // 這邊第一個Integer參數，指的是X軸相對TNT方塊的偏移量
        // 這邊第二個List<Integer>參數，指的是Y軸相對TNT方塊的偏移量
        Map<Integer, List<Integer>> destroyedBlockPosXY = new HashMap<Integer, List<Integer>>();

        // Z軸，也就是南北方向，偏移量都是從0到10個方塊 (也就是深度為10)
        // 這裡是正數，表示是一個"面向南方"的隧道
        final List<Integer> z_list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 製造一個X軸左右各4個方塊長、高最多4個方塊長的隧道 (倒U形狀)
        // 這邊每一個都對應下面提到的(x, y_list)
        destroyedBlockPosXY.put(-4, Arrays.asList(0));
        destroyedBlockPosXY.put(-3, Arrays.asList(0, 1));
        destroyedBlockPosXY.put(-2, Arrays.asList(0, 1, 2));
        destroyedBlockPosXY.put(-1, Arrays.asList(0, 1, 2, 3));
        destroyedBlockPosXY.put(0, Arrays.asList(0, 1, 2, 3, 4));
        destroyedBlockPosXY.put(1, Arrays.asList(0, 1, 2, 3));
        destroyedBlockPosXY.put(2, Arrays.asList(0, 1, 2));
        destroyedBlockPosXY.put(3, Arrays.asList(0, 1));
        destroyedBlockPosXY.put(4, Arrays.asList(0));

        // 透過lambda的forEach方法去輪巡我們XYZ軸的偏移量列表
        // 先取每一個上面寫的(x, y_list)的值，這邊是為了取得X偏移值
        for(Map.Entry<Integer, List<Integer>> entry : destroyedBlockPosXY.entrySet()) {
            int x = entry.getKey();
            List<Integer> y_list = entry.getValue();
            // 輪巡y_list的每一個Y偏移值
            for(int y : y_list) {
                // 輪巡z_list的每一個Z偏移值
                for(int z : z_list) {
                    // 程式碼到此處，(X,Y,Z)的點產生出來的形狀就會像是深度10、寬跟高各4的隧道
                    // 透過偏移量與TNT方塊的座標(originPos)來取得我們想要破壞的方塊位置
                    BlockPos blockPos = new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                    // 加入該被破壞的方塊到受影響的方塊列表內
                    //ExampleMod.LOGGER.info("x y z ="+x+"" + y + ""+ z) ;
                    affectBlocks.add(blockPos);
                }
            }
        }
    }
}

