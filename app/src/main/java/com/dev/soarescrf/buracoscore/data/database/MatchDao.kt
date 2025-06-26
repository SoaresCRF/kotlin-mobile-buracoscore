package com.dev.soarescrf.buracoscore.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.soarescrf.buracoscore.data.model.Match

/**
 * Data Access Object (DAO) para operações relacionadas à entidade [Match].
 *
 * Define as operações de acesso ao banco de dados Room.
 */
@Dao
interface MatchDao {

    /**
     * Insere uma nova partida (Match) no banco de dados.
     *
     * @param match Partida a ser inserida.
     */
    @Insert
    suspend fun insertMatch(match: Match)

    /**
     * Remove todas as partidas armazenadas no banco de dados.
     */
    @Query("DELETE FROM `match`")
    suspend fun deleteAllMatches()

    /**
     * Recupera todas as partidas salvas, ordenadas do mais recente para o mais antigo.
     *
     * @return Lista de partidas armazenadas.
     */
    @Query("SELECT * FROM `match` ORDER BY id DESC")
    suspend fun getAllMatches(): List<Match>
}
