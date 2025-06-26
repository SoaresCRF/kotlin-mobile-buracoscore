package com.dev.soarescrf.buracoscore.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dev.soarescrf.buracoscore.data.model.Match

/**
 * Representa o banco de dados Room da aplicação.
 *
 * Contém a definição das entidades e fornece acesso aos DAOs.
 */
@Database(
    entities = [Match::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Fornece o DAO para operações na tabela Match.
     */
    abstract fun matchDao(): MatchDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "matches_db"

        /**
         * Retorna uma instância singleton da base de dados.
         *
         * Garante thread-safety usando double-checked locking com o bloco synchronized.
         *
         * @param context Contexto da aplicação para inicializar a instância.
         * @return Instância única de AppDatabase.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        /**
         * Cria uma nova instância do banco de dados Room.
         *
         * Inclui o fallback para destruição da base em caso de mudança de versão sem migração.
         *
         * @param context Contexto da aplicação.
         * @return Nova instância de AppDatabase.
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
