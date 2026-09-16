//? if forge {
/*package dev.skulldogged.drop_confirm.loaders.forge

//? if <=1.18.2 {
/^import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.client.ClientRegistry
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory as ConfigScreenFactory
import net.minecraftforge.client.event.InputEvent.KeyInputEvent as Key
^///?} else {
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.InputEvent.Key
//?}

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import dev.skulldogged.drop_confirm.DropConfirm
import dev.skulldogged.drop_confirm.DropConfirm.TOGGLE_KEY
import dev.skulldogged.drop_confirm.DropConfirm.handleKeyPresses
import dev.skulldogged.drop_confirm.config.DropConfirmConfig
import dev.skulldogged.drop_confirm.config.screens.DropConfirmConfigScreen

@Mod("drop_confirm")
class ForgeEntrypoint {
  init {
    //? if <=1.18.2 {
    /^MOD_BUS.addListener { _: FMLClientSetupEvent -> ClientRegistry.registerKeyBinding(TOGGLE_KEY) }
    ^///?} else {
    MOD_BUS.addListener { event: RegisterKeyMappingsEvent -> event.register(TOGGLE_KEY) }
    //?}

    LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
      ConfigScreenFactory { _, screen ->
        try {
          DropConfirmConfigScreen(screen)
        } catch (e: Throwable) {
          DropConfirm.LOGGER.error("Failed to load config screen", e)
          screen
        }
      }
    }

    DropConfirmConfig.load()

    if (FMLEnvironment.dist == Dist.CLIENT)
      FORGE_BUS.register(object {
        @SubscribeEvent
        fun onKeyInput(event: Key) = handleKeyPresses(Minecraft.getInstance())
      })
  }
}
*///?}
