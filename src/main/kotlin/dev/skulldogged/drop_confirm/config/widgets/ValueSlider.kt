package dev.skulldogged.drop_confirm.config.widgets

import kotlin.math.roundToInt
import net.minecraft.client.gui.components.AbstractSliderButton
import dev.skulldogged.drop_confirm.util.ComponentUtils

/**
 * A vanilla slider over a float range, snapped to [step].
 */
class ValueSlider(
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  private val baseLabel: String,
  initial: Float,
  private val min: Float,
  private val max: Float,
  private val step: Float,
  private val formatter: (Float) -> String = { it.toString() },
  private val onChange: (Float) -> Unit = {}
) : AbstractSliderButton(
  x, y, width, height,
  /*? if >=1.16.5 {*/ComponentUtils.literal(baseLabel),/*?}*/
  fractionOf(initial, min, max)
) {
  var current: Float = snap(initial)
    private set

  init {
    updateMessage()
  }

  private fun snap(raw: Float): Float {
    var result = raw
    if (step > 0f) result = (result / step).roundToInt() * step
    return result.coerceIn(min, max)
  }

  override fun updateMessage() = setMessage(VanillaWidgets.label("$baseLabel: ${formatter(current)}"))

  override fun applyValue() {
    val next = snap((min + (max - min) * value).toFloat())
    value = fractionOf(next, min, max)

    if (next != current) {
      current = next
      onChange(next)
    }
  }

  companion object {
    private fun fractionOf(value: Float, min: Float, max: Float): Double =
      if (max > min) ((value - min) / (max - min)).toDouble().coerceIn(0.0, 1.0) else 0.0
  }
}
