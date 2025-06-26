package com.dev.soarescrf.buracoscore.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Gerencia as preferências relacionadas aos nomes dos jogadores.
 *
 * Responsável por salvar e recuperar os nomes dos jogadores A e B
 * utilizando [SharedPreferences].
 */
class PlayerNamePreferenceManager(context: Context) {

    companion object {
        private const val PREF_NAME = "PlayerNamePreference"
        private const val KEY_PLAYER_NAME_A = "playerNameA"
        private const val KEY_PLAYER_NAME_B = "playerNameB"

        private const val DEFAULT_PLAYER_NAME_A = "JOGADOR A"
        private const val DEFAULT_PLAYER_NAME_B = "JOGADOR B"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Salva o nome do Jogador A.
     *
     * @param playerNameA nome do Jogador A
     */
    fun savePlayerNameA(playerNameA: String) {
        sharedPreferences.edit { putString(KEY_PLAYER_NAME_A, playerNameA) }
    }

    /**
     * Salva o nome do Jogador B.
     *
     * @param playerNameB nome do Jogador B
     */
    fun savePlayerNameB(playerNameB: String) {
        sharedPreferences.edit { putString(KEY_PLAYER_NAME_B, playerNameB) }
    }

    /**
     * Retorna o nome salvo do Jogador A.
     * Caso não exista, retorna o valor padrão "JOGADOR A".
     */
    fun getPlayerNameA(): String =
        sharedPreferences.getString(KEY_PLAYER_NAME_A, DEFAULT_PLAYER_NAME_A)
            ?: DEFAULT_PLAYER_NAME_A

    /**
     * Retorna o nome salvo do Jogador B.
     * Caso não exista, retorna o valor padrão "JOGADOR B".
     */
    fun getPlayerNameB(): String =
        sharedPreferences.getString(KEY_PLAYER_NAME_B, DEFAULT_PLAYER_NAME_B)
            ?: DEFAULT_PLAYER_NAME_B
}
