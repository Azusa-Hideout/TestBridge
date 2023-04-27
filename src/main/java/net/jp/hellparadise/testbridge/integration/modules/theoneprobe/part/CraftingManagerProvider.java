package net.jp.hellparadise.testbridge.integration.modules.theoneprobe.part;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.Config;

import net.jp.hellparadise.testbridge.helpers.TextHelper;
import net.jp.hellparadise.testbridge.helpers.interfaces.ITranslationKey;
import net.jp.hellparadise.testbridge.part.PartCraftingManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import appeng.api.parts.IPart;

public class CraftingManagerProvider implements IPartProbInfoProvider, ITranslationKey {

    @Override
    public void addProbeInfo(IPart part, ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
        IBlockState blockState, IProbeHitData data) {
        if (part instanceof PartCraftingManager) {
            addConnectInfo((PartCraftingManager) part, probeInfo);

            IItemHandler patternInv = ((PartCraftingManager) part).getCMDuality()
                .getInventoryByName("patterns");
            int patternCount = 0;

            for (int i = 0; i < patternInv.getSlots(); i++) {
                if (!patternInv.getStackInSlot(i)
                    .isEmpty()) {
                    patternCount++;
                }
            }

            if (patternCount != 0) {
                IProbeInfo inv = probeInfo.vertical(
                    probeInfo.defaultLayoutStyle()
                        .borderColor(Config.chestContentsBorderColor)
                        .spacing(0));
                IProbeInfo infoRow = null;
                for (int i = 0; i < patternCount; i++) {
                    if (i % 9 == 0) infoRow = inv.horizontal();
                    infoRow.item(patternInv.getStackInSlot(i));
                }
            } else probeInfo.text(new TextHelper(top$cm_prefix + "no_patterns").getTranslated());
        }
    }

    private void addConnectInfo(PartCraftingManager part, IProbeInfo probeInfo) {
        String satName = part.getSatelliteName();
        probeInfo.text(
            new TextHelper(top$cm_prefix + "select_sat")
                .addArgument(
                    new TextHelper(
                        satName.isEmpty() ? top$cm_prefix + "none"
                            : (part.getSatellitePart() != null ? top$cm_prefix + "valid"
                                : top$cm_prefix + "router_error")).addArgument(satName)
                                    .getTranslated())
                .getTranslated());
    }
}
