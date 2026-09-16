//? if neoforge {
/*package dev.skulldogged.drop_confirm.loaders.neoforge

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common./*$ bus_subscriber_import {*/EventBusSubscriber/*$}*/
import net.neoforged.neoforge.client./*$ config_screen_factory_import {*/gui.IConfigScreenFactory as ConfigScreenFactory/*$}*/
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.common.NeoForge
import dev.skulldogged.drop_confirm.DropConfirm.TOGGLE_KEY
import dev.skulldogged.drop_confirm.DropConfirm.handleKeyPresses
import dev.skulldogged.drop_confirm.config.DropConfirmConfig
import dev.skulldogged.drop_confirm.config.screens.DropConfirmConfigScreen

@Mod("drop_confirm")
@EventBusSubscriber(
  modid = "drop_confirm",
  /*? if <=1.21.6 {*/bus = EventBusSubscriber.Bus.MOD,/*?}*/
  value = [Dist.CLIENT]
)
class NeoforgeEntrypoint {
  companion object {
    @SubscribeEvent
    @JvmStatic
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) = event.register(TOGGLE_KEY)
  }

  init {
    ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory::class.java) {
      ConfigScreenFactory { _, screen -> DropConfirmConfigScreen(screen) }
    }

    DropConfirmConfig.load()

    if (FMLEnvironment./*$ fml_env_dist {*/dist/*$}*/ == Dist.CLIENT)
      NeoForge.EVENT_BUS.register(object {
        @SubscribeEvent
        fun onKeyInput(event: InputEvent.Key) = handleKeyPresses(Minecraft.getInstance())
      })
  }
}
*///?}
