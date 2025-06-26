package com.dev.soarescrf.buracoscore.utils

import android.os.Build
import android.text.Layout
import android.widget.TextView

/**
 * Classe utilitária para manipulação e configuração de textos em TextViews.
 *
 * Contém métodos relacionados a formatação e exibição de texto.
 */
object TextUtils {

    /**
     * Aplica o alinhamento justificado ao texto do TextView, caso o dispositivo
     * esteja rodando Android Oreo (API 26) ou superior.
     *
     * @param textView O TextView ao qual será aplicado o alinhamento justificado.
     */
    fun applyJustifyAlignment(textView: TextView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
        }
    }
}
