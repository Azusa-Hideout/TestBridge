package testbridge.gui;

import java.io.IOException;
import javax.annotation.Nonnull;

import logisticspipes.utils.gui.LogisticsBaseGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.input.Keyboard;

import logisticspipes.network.PacketHandler;
import logisticspipes.pipes.SatelliteNamingResult;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.InputBar;
import logisticspipes.utils.gui.SmallGuiButton;

import network.rs485.logisticspipes.util.TextUtil;

import testbridge.network.packets.resultpipe.ResultSetNamePacket;
import testbridge.pipes.ResultPipe;

public class GuiResultPipe extends LogisticsBaseGuiScreen {

  @Nonnull
  private final ResultPipe resultPipe;

  @Nonnull
  private String response = "";

  private InputBar input;

  public GuiResultPipe(@Nonnull ResultPipe resultPipe) {
    super(new Container() {

      @Override
      public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
        return true;
      }
    });
    xSize = 116;
    ySize = 77;
    this.resultPipe = resultPipe;
  }

  @Override
  public void initGui() {
    Keyboard.enableRepeatEvents(true);
    super.initGui();
    buttonList.add(new SmallGuiButton(0, (width / 2) - (30 / 2) + 35, (height / 2) + 20, 30, 10, "Save"));
    input = new InputBar(fontRenderer, this, guiLeft + 8, guiTop + 40, 100, 16);
  }

  @Override
  public void closeGui() throws IOException {
    super.closeGui();
    Keyboard.enableRepeatEvents(false);
  }

  @Override
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (guibutton.id == 0) {
      final TileEntity container = resultPipe.getContainer();
      if (container != null) {
        MainProxy.sendPacketToServer(PacketHandler.getPacket(ResultSetNamePacket.class).setString(input.getText()).setTilePos(container));
      }
    } else {
      super.actionPerformed(guibutton);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    super.drawGuiContainerForegroundLayer(par1, par2);
    drawCenteredString(TextUtil.translate("gui.result.ResultName"), 59, 7, 0x404040);
    String name = TextUtil.getTrimmedString(resultPipe.getSatellitePipeName(), 100, mc.fontRenderer, "...");
    int yOffset = 0;
    if (!response.isEmpty()) {
      drawCenteredString(TextUtil.translate("gui.satellite.naming_result." + response), xSize / 2, 30, response.equals("success") ? 0x404040 : 0x5c1111);
      yOffset = 4;
    }
    drawCenteredString(name, xSize / 2, 24 - yOffset, 0x404040);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
    super.drawGuiContainerBackgroundLayer(f, x, y);
    GuiGraphics.drawGuiBackGround(mc, guiLeft, guiTop, right, bottom, zLevel, true);
    input.drawTextBox();
  }

  @Override
  protected void mouseClicked(int x, int y, int k) throws IOException {
    if (!input.handleClick(x, y, k)) {
      super.mouseClicked(x, y, k);
    }
  }

  @Override
  public void keyTyped(char c, int i) throws IOException {
    if (!input.handleKey(c, i)) {
      super.keyTyped(c, i);
    }
  }

  public void handleResponse(SatelliteNamingResult result, String newName) {
    response = result.toString();
    if (result == SatelliteNamingResult.SUCCESS) {
      resultPipe.setSatellitePipeName(newName);
    }
  }

}