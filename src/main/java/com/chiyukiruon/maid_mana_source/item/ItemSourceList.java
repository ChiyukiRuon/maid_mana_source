package com.chiyukiruon.maid_mana_source.item;

import com.chiyukiruon.maid_mana_source.memory.ScannedSourceListMemory;
import com.chiyukiruon.maid_mana_source.registry.ItemRegistry;
import com.chiyukiruon.maid_mana_source.task.MaidManaSourceTask;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.item.AbstractStoreMaidItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.chiyukiruon.maid_mana_source.util.TargetUtil.isTargetInList;

public class ItemSourceList extends AbstractStoreMaidItem implements IMaidBauble {
    public ItemSourceList() {
        super((new Properties()).stacksTo(1));
    }

    public static ItemStack getSourceList(@NotNull EntityMaid maid) {
        BaubleItemHandler handler = maid.getMaidBauble();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.is(ItemRegistry.SOURCE_LIST.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onTick(@NotNull EntityMaid maid, @NotNull ItemStack stack) {
        if (maid.level().isClientSide()) return;
        if (!maid.getTask().getUid().equals(MaidManaSourceTask.UID)) return;
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        if (!tag.getUUID("BoundMaid").equals(maid.getUUID())) return;
        if (!tag.getString("BoundMaidName").equals(maid.getName().getString())) {
            CompoundTag maidTag = new CompoundTag();
            maid.saveWithoutId(maidTag);
            maidTag.putString("id", Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(InitEntities.MAID.get())).toString());
            tag.put("MaidInfo", maidTag);
            tag.putString("BoundMaidName", maid.getName().getString());
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (target instanceof EntityMaid maid && !player.level().isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag maidTag = new CompoundTag();
            if (tag.hasUUID("BoundMaid")) {
                if (!tag.getUUID("BoundMaid").equals(maid.getUUID())) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.maid_mana_source.source_list.already_bound", tag.getString("BoundMaidName")),
                            true
                    );
                    return InteractionResult.CONSUME;
                }
                return InteractionResult.PASS;
            } else {
                maid.saveWithoutId(maidTag);
                maidTag.putString("id", Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(InitEntities.MAID.get())).toString());
                tag.put("MaidInfo", maidTag);
                tag.putString("BoundMaidName", maid.getName().getString());
                tag.putUUID("BoundMaid", maid.getUUID());
                tag.put("SourceList", ScannedSourceListMemory.initializeSourceListNBT(maid));
                player.displayClientMessage(
                        Component.translatable("tooltip.maid_mana_source.source_list.bind_success", maid.getName()),
                        true
                );
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            CompoundTag tag = stack.getOrCreateTag();
            // 解绑
            if (serverPlayer.isShiftKeyDown()) {
                BlockHitResult hitResult = (BlockHitResult) player.pick(player.getEntityReach(), 0.0F, false);

                if (hitResult.getType() == HitResult.Type.BLOCK && isTargetInList(hitResult.getBlockPos(), tag.getList("SourceList", Tag.TAG_COMPOUND))) {
                    return InteractionResultHolder.success(stack);
                }

                if (tag.hasUUID("BoundMaid")) {
                    serverPlayer.displayClientMessage(
                            Component.translatable("tooltip.maid_mana_source.source_list.unbind_success", tag.getString("BoundMaidName")),
                            true
                    );
                    tag.remove("MaidInfo");
                    tag.remove("BoundMaidName");
                    tag.remove("BoundMaid");
                    tag.remove("SourceList");
                    return InteractionResultHolder.success(stack);
                } else {
                    serverPlayer.displayClientMessage(
                            Component.translatable("tooltip.maid_mana_source.source_list.not_bound"),
                            true
                    );
                    return InteractionResultHolder.fail(stack);
                }
            }
            return InteractionResultHolder.pass(stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResult.PASS;

        if (context.getPlayer() instanceof ServerPlayer) {
            ItemStack stack = context.getItemInHand();
            CompoundTag tag = stack.getOrCreateTag();

            if (stack.is(ItemRegistry.SOURCE_LIST.get())
                    && tag.hasUUID("BoundMaid")) {

                BlockPos pos = context.getClickedPos();
                updateSourceList(stack, pos);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.maid_mana_source.source_list.desc").withStyle(ChatFormatting.GRAY));
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.hasUUID("BoundMaid")) {
            tooltip.add(Component.translatable("tooltip.maid_mana_source.source_list.bind_success", tag.getString("BoundMaidName")).withStyle(ChatFormatting.ITALIC));
        } else {
            tooltip.add(Component.translatable("tooltip.maid_mana_source.source_list.not_bound").withStyle(ChatFormatting.ITALIC));
        }

    }

    private static void updateSourceList(@NotNull ItemStack stack, BlockPos pos) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList("SourceList", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int x = entry.getInt("x");
            int y = entry.getInt("y");
            int z = entry.getInt("z");

            if (x == pos.getX() && y == pos.getY() && z == pos.getZ()) {
                boolean oldEnabled = entry.getBoolean("enabled");
                entry.putBoolean("enabled", !oldEnabled);
                list.set(i, entry);
                tag.put("SourceList", list);
                stack.setTag(tag);
                break;
            }
        }
    }

    public static void moveEntry(ListTag list, BlockPos pos, int direction) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            if (entry.getInt("x") == pos.getX()
                    && entry.getInt("y") == pos.getY()
                    && entry.getInt("z") == pos.getZ()) {

                int newIndex = i + direction;
                if (newIndex < 0 || newIndex >= list.size()) return;

                list.remove(i);
                list.add(newIndex, entry);
                return;
            }
        }
    }
}
