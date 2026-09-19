package dev.skulldogged.drop_confirm.util

/**
 * Defines standard colors for UI components throughout the mod's interface.
 *
 * Values are `0xAARRGGBB`. Entries written without an alpha byte are treated as
 * fully opaque when resolved through [invoke], so they render the same on every
 * Minecraft version (newer versions ignore colors with a zero alpha).
 *
 * @property rgb The value of the color as a number in 0x(AA)RRGGBB format
 * @since 5.0.0
 */
@Suppress("unused")
enum class Color(val rgb: Number) {
  /** Standard text color (light gray). */
  TEXT(0xE0E0E0),

  /** Color for disabled components (gray). */
  DISABLED(0xA0A0A0),

  /** Highlight color for hovered text (yellow). */
  HOVERED(0xFFFFA0),

  /** Color for success states and confirmations (green). */
  SUCCESS(0x55FF55),

  /** Color for error states and warnings (red). */
  ERROR(0xFF5555),

  /** Placeholder text inside empty inputs (dim gray). */
  PLACEHOLDER(0x808080),

  /** Fully transparent color. */
  TRANSPARENT(0x00000000),

  /** Color used for dimming the background behind popups. */
  DIMMING(0xC0101010),

  /** Border color for popups and UI containers (purple/pink). */
  BORDER(0xDD9BA8FF),

  /** Separator color for dividing UI sections (light purple). */
  SEPARATOR(0xDDC0C9FF),

  /** Starting color for title bar gradient (medium purple). */
  TITLE_BAR_PRIMARY(0xDD4B61D1),

  /** Ending color for title bar gradient (darker purple). */
  TITLE_BAR_SECONDARY(0xDD3B4DA7),

  /** Starting color for content area gradient (dark blue-purple). */
  CONTENT_PRIMARY(0xDD242852),

  /** Ending color for content area gradient (very dark blue). */
  CONTENT_SECONDARY(0xDD1A2040),

  /** Decorative color for UI corners and accents (translucent light purple). */
  CORNER_DECORATION(0xAAC0C9FF),

  /** Confirmation button color (green). */
  BUTTON_CONFIRM(0xFF2D7D4C),

  /** Hover color for confirmation buttons (lighter green). */
  BUTTON_CONFIRM_HOVER(0xFF3A8E5A),

  /** Cancellation button color (red). */
  BUTTON_CANCEL(0xFF8D3F3F),

  /** Hover color for cancellation buttons (lighter red). */
  BUTTON_CANCEL_HOVER(0xFF9E4F4F);

  /**
   * Resolves this color to an `0xAARRGGBB` integer.
   *
   * Colors declared without an alpha byte are made fully opaque, so callers can
   * pass any entry straight into fill and text rendering methods.
   */
  operator fun invoke(): Int {
    val value = rgb.toInt()
    return if (value != 0 && (value ushr 24) == 0) value or 0xFF000000.toInt() else value
  }
}
