//? if <1.20.1 || forge {
/*package dev.skulldogged.drop_confirm.config.widgets

class EnumControl<T : Enum<T>>(
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  private val baseText: String,
  initialValue: T,
  private val valueFormatter: (T) -> String = { it.name },
  private val onValueChanged: (T) -> Unit = {}
) : ButtonControl(x, y, width, height, "", {}) {
  private val values = initialValue.javaClass.enumConstants as Array<T>

  private var currentValue: T = initialValue
    set(value) {
      field = value
      updateButtonText()
      onValueChanged(value)
    }

  init {
    updateButtonText()
    setOnClick { cycleToNextValue() }
  }

  private fun updateButtonText() {
    controlMessage = "$baseText: ${valueFormatter(currentValue)}"
  }

  fun cycleToNextValue() {
    val currentIndex = values.indexOf(currentValue)
    val nextIndex = (currentIndex + 1) % values.size
    currentValue = values[nextIndex]
  }
}
*///?}
