//? if fabric {
package dev.skulldogged.drop_confirm.loaders.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
//?}
import dev.skulldogged.drop_confirm.DropConfirm.TOGGLE_HELD_KEY
import dev.skulldogged.drop_confirm.DropConfirm.TOGGLE_KEY
import dev.skulldogged.drop_confirm.DropConfirm.handleKeyPresses
import dev.skulldogged.drop_confirm.config.DropConfirmConfig

class FabricEntrypoint : ModInitializer {
  override fun onInitialize() {
    DropConfirmConfig.load()
    //? if >=26.1 {
    /*KeyMappingHelper.registerKeyMapping(TOGGLE_KEY)
    KeyMappingHelper.registerKeyMapping(TOGGLE_HELD_KEY)
    *///?} else {
    KeyBindingHelper.registerKeyBinding(TOGGLE_KEY)
    KeyBindingHelper.registerKeyBinding(TOGGLE_HELD_KEY)
    //?}
    ClientTickEvents.END_CLIENT_TICK.register { handleKeyPresses(it) }
  }
}
//?}
