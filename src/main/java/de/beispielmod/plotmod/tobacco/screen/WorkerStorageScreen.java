package de.beispielmod.plotmod.tobacco.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.beispielmod.plotmod.PlotMod;
import de.beispielmod.plotmod.tobacco.menu.WorkerStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * GUI Screen für Arbeiter-Schrank
 */
public class WorkerStorageScreen extends AbstractContainerScreen<WorkerStorageMenu> {
    
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation(PlotMod.MOD_ID, "textures/gui/worker_storage.png");
    
    public WorkerStorageScreen(WorkerStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        
        // Zeige Geld-Info
        renderMoneyInfo(graphics);
    }
    
    private void renderMoneyInfo(GuiGraphics graphics) {
        double money = menu.getBlockEntity().getMoney();
        String moneyText = String.format("§eGeld: %.0f€", money);
        
        int x = (width - imageWidth) / 2 + 8;
        int y = (height - imageHeight) / 2 + 5;
        
        graphics.drawString(this.font, moneyText, x, y, 0xFFFFFF, false);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Titel
        graphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        
        // Slot-Labels
        graphics.drawString(this.font, "§7Geld", 8, 8, 0x404040, false);
        graphics.drawString(this.font, "§7Samen", 44, 8, 0x404040, false);
        graphics.drawString(this.font, "§7Erde", 8, 44, 0x404040, false);
        graphics.drawString(this.font, "§7Flaschen", 44, 44, 0x404040, false);
        graphics.drawString(this.font, "§7Ernte", 152, 8, 0x404040, false);
    }
}
