package com.chiyukiruon.maid_mana_source.client.key;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.network.Network;
import com.chiyukiruon.maid_mana_source.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = MaidManaSource.MODID, value = Dist.CLIENT)
public class ScrollHandler {
    @SubscribeEvent
    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // 必须按下键位
        if (!Keybinds.SORT_KEY.isDown()) return;

        // 必须手持 SourceList
        ItemStack held = player.getMainHandItem();
        if (!held.is(ItemRegistry.SOURCE_LIST.get())) {
            held = player.getOffhandItem();
            if (!held.is(ItemRegistry.SOURCE_LIST.get())) return;
        }

        // 玩家视线的方块
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHit)) return;
        BlockPos pos = blockHit.getBlockPos();

        // 滚轮方向
        int direction = event.getScrollDeltaY() > 0 ? 1 : -1;

        // 发包给服务端
        Network.sendSortSourcePacket(pos, direction);

        // 拦截默认物品栏切换
        event.setCanceled(true);
    }
}
