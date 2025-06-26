package com.dev.soarescrf.buracoscore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa uma partida (Match) no banco de dados Room.
 *
 * @property id Identificador único da partida (gerado automaticamente pelo Room).
 * @property playerA Nome do Jogador A.
 * @property pointsA Pontuação do Jogador A.
 * @property playerB Nome do Jogador B.
 * @property pointsB Pontuação do Jogador B.
 * @property date Data da partida.
 */
@Entity(tableName = "match")
data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerA: String,
    val pointsA: String,
    val playerB: String,
    val pointsB: String,
    val date: String
) {
    companion object {

        /**
         * Cria uma nova instância de [Match] com o ID inicializado como 0 (Room irá auto-gerar).
         *
         * @param playerA Nome do jogador A.
         * @param pointsA Pontuação do jogador A.
         * @param playerB Nome do jogador B.
         * @param pointsB Pontuação do jogador B.
         * @param date Data da partida.
         * @return Instância de [Match].
         */
        fun create(
            playerA: String,
            pointsA: String,
            playerB: String,
            pointsB: String,
            date: String
        ): Match = Match(
            id = 0,
            playerA = playerA,
            pointsA = pointsA,
            playerB = playerB,
            pointsB = pointsB,
            date = date
        )
    }
}
