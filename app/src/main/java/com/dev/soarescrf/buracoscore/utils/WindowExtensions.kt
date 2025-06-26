package com.dev.soarescrf.buracoscore.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController

/**
 * Define a aparência dos ícones da barra de status para serem claros ou escuros,
 * de acordo com o valor de [enabled].
 *
 * Em dispositivos com Android R (API 30) ou superior, utiliza a API moderna
 * [WindowInsetsController.setSystemBarsAppearance]. Em versões anteriores,
 * ajusta diretamente a flag [View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR].
 *
 * @param enabled Se `true`, define ícones escuros (status bar clara);
 *                se `false`, define ícones claros (status bar escura).
 *
 * @receiver [Window] onde a aparência dos ícones da status bar será aplicada.
 */
fun Window.setLightStatusBarIcons(enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            if (enabled) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = decorView.systemUiVisibility.let { visibility ->
            if (enabled) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}
