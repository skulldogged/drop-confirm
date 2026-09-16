//? if fabric {
package dev.skulldogged.drop_confirm.loaders.fabric

//? if 1.14.4 {
/*import net.minecraft.client.gui.screens.Screen
import io.github.prospector.modmenu.api.ModMenuApi
import java.util.function.Function
*///?} elif 1.15.2 {
/*import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
*///?} else {
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
//?}
import dev.skulldogged.drop_confirm.config.screens.DropConfirmConfigScreen

class ModMenuIntegration : ModMenuApi {
  //? if >=1.15.2 {
  override fun getModConfigScreenFactory() = ConfigScreenFactory { DropConfirmConfigScreen(it) }
  //?} else {
  /*override fun getModId(): String = "drop_confirm"
  override fun getConfigScreenFactory(): Function<Screen, out Screen> {
    return Function { DropConfirmConfigScreen(it) }
  }
  *///?}
}
//?}
