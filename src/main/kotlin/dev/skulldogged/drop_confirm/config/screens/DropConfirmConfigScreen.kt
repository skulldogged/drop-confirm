package dev.skulldogged.drop_confirm.config.screens

import java.util.Locale
import kotlin.math.max
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import dev.skulldogged.drop_confirm.config.ConfirmationMode
import dev.skulldogged.drop_confirm.config.DropConfirmConfig
import dev.skulldogged.drop_confirm.config.widgets.ValueSlider
import dev.skulldogged.drop_confirm.config.widgets.VanillaWidgets
import dev.skulldogged.drop_confirm.util.ClientGuiUtils

/**
 * The main settings screen.
 *
 * Edits are applied to [DropConfirmConfig] immediately so the list editor sees
 * them; Cancel restores the values captured when the screen was opened.
 */
class DropConfirmConfigScreen(parent: Screen?) : ConfigScreenBase("config.drop_confirm.title", parent) {
  private val config = DropConfirmConfig

  private val originalEnabled = config.enabled
  private val originalPlaySounds = config.shouldPlaySounds
  private val originalTreatAsWhitelist = config.treatAsWhitelist
  private val originalResetDelay = config.confirmationResetDelay
  private val originalConfirmationMode = config.confirmationMode

  private lateinit var listButton: Button

  private val listKey: String
    get() = if (config.treatAsWhitelist) WHITELIST_KEY else BLACKLIST_KEY

  override fun buildWidgets() {
    val leftX = width / 2 - COLUMN_SPACING / 2 - CONTROL_WIDTH
    val rightX = width / 2 + COLUMN_SPACING / 2
    val rowStep = CONTROL_HEIGHT + CONTROL_SPACING
    val contentHeight = ROWS * CONTROL_HEIGHT + (ROWS - 1) * CONTROL_SPACING

    val bottomY = height - CONTROL_HEIGHT - BOTTOM_MARGIN
    val startY = CONTENT_TOP + max(0, (bottomY - BOTTOM_MARGIN - CONTENT_TOP - contentHeight) / 2)

    // Left column
    add(VanillaWidgets.checkbox(leftX, startY, CONTROL_WIDTH, CONTROL_HEIGHT, t(ENABLED_KEY), config.enabled) {
      config.enabled = it
    }.withTooltip(t("$ENABLED_KEY.description")))

    add(VanillaWidgets.checkbox(leftX, startY + rowStep, CONTROL_WIDTH, CONTROL_HEIGHT, t(PLAY_SOUNDS_KEY), config.shouldPlaySounds) {
      config.shouldPlaySounds = it
    }.withTooltip(t("$PLAY_SOUNDS_KEY.description")))

    add(VanillaWidgets.checkbox(leftX, startY + 2 * rowStep, CONTROL_WIDTH, CONTROL_HEIGHT, t(WHITELIST_TOGGLE_KEY), config.treatAsWhitelist) {
      config.treatAsWhitelist = it
      listButton.setMessage(VanillaWidgets.label(t(listKey)))
      listButton.withTooltip(t("$listKey.description"))
    }.withTooltip(t("$WHITELIST_TOGGLE_KEY.description")))

    // Right column
    add(VanillaWidgets.button(rightX, startY, CONTROL_WIDTH, CONTROL_HEIGHT, modeLabel(config.confirmationMode)) { button ->
      val modes = ConfirmationMode.entries
      config.confirmationMode = modes[(config.confirmationMode.ordinal + 1) % modes.size]
      button.setMessage(VanillaWidgets.label(modeLabel(config.confirmationMode)))
    }.withTooltip(t("$MODE_KEY.description")))

    add(
      ValueSlider(
        rightX, startY + rowStep, CONTROL_WIDTH, CONTROL_HEIGHT,
        t(DELAY_KEY),
        config.confirmationResetDelay,
        1.0f, 5.0f, 0.1f,
        { "%.1fs".format(Locale.ROOT, it) }
      ) { config.confirmationResetDelay = it }.withTooltip(t("$DELAY_KEY.description"))
    )

    listButton = add(VanillaWidgets.button(rightX, startY + 2 * rowStep, CONTROL_WIDTH, CONTROL_HEIGHT, t(listKey)) {
      ClientGuiUtils.setScreen(minecraft, DropConfirmListEditorScreen(this))
    }.withTooltip(t("$listKey.description")))

    // Bottom row
    val groupX = width / 2 - (CONTROL_WIDTH * 2 + COLUMN_SPACING) / 2

    add(VanillaWidgets.button(groupX, bottomY, CONTROL_WIDTH, CONTROL_HEIGHT, t("option.drop_confirm.cancel")) {
      restoreOriginalValues()
      ClientGuiUtils.setScreen(minecraft, parent)
    })

    add(VanillaWidgets.button(groupX + CONTROL_WIDTH + COLUMN_SPACING, bottomY, CONTROL_WIDTH, CONTROL_HEIGHT, t("option.drop_confirm.save_and_close")) {
      config.save()
      ClientGuiUtils.setScreen(minecraft, parent)
    })
  }

  private fun modeLabel(mode: ConfirmationMode): String = "${t(MODE_KEY)}: ${t(mode.translationKey)}"

  private fun restoreOriginalValues() {
    config.enabled = originalEnabled
    config.shouldPlaySounds = originalPlaySounds
    config.treatAsWhitelist = originalTreatAsWhitelist
    config.confirmationResetDelay = originalResetDelay
    config.confirmationMode = originalConfirmationMode
  }

  /** Escape behaves like Cancel. */
  override fun onClose() {
    restoreOriginalValues()
    super.onClose()
  }

  companion object {
    private const val CONTROL_WIDTH = 150
    private const val COLUMN_SPACING = 8
    private const val BOTTOM_MARGIN = 8
    private const val ROWS = 3

    private const val ENABLED_KEY = "option.drop_confirm.enabled"
    private const val PLAY_SOUNDS_KEY = "option.drop_confirm.play_sounds"
    private const val WHITELIST_TOGGLE_KEY = "option.drop_confirm.treat_as_whitelist"
    private const val DELAY_KEY = "option.drop_confirm.confirmation_reset_delay"
    private const val MODE_KEY = "option.drop_confirm.confirmation_mode"
    private const val BLACKLIST_KEY = "option.drop_confirm.blacklisted_items"
    private const val WHITELIST_KEY = "option.drop_confirm.whitelisted_items"
  }
}
