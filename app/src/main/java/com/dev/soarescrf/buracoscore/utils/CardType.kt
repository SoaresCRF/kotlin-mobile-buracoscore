package com.dev.soarescrf.buracoscore.utils

/**
 * Representa os diferentes tipos de cartas e canastras, incluindo suas pontuações e limites máximos permitidos.
 *
 * @property points Pontos atribuídos a este tipo de carta/canastra.
 * @property maxLimit Quantidade máxima permitida para este tipo.
 */
enum class CardType(val points: Int, val maxLimit: Int) {
    /** Carta que vale 5 pontos, com limite máximo de 40 unidades. */
    CARD_5(5, 40),

    /** Carta que vale 10 pontos, com limite máximo de 48 unidades. */
    CARD_10(10, 48),

    /** Carta que vale 15 pontos, com limite máximo de 8 unidades. */
    CARD_15(15, 8),

    /** Carta que vale 20 pontos, com limite máximo de 12 unidades. */
    CARD_20(20, 12),

    /** Canastra fechada que vale 100 pontos, limite máximo de 40 unidades. */
    CLOSED_CANASTA(100, 40),

    /** Canastra limpa que vale 200 pontos, limite máximo de 32 unidades. */
    CLEAN_CANASTA(200, 32),

    /** Canastra real que vale 500 pontos, limite máximo de 8 unidades. */
    ROYAL_CANASTA(500, 8),

    /** Canastra de ás que vale 1000 pontos, limite máximo de 8 unidades. */
    AS_CANASTA(1000, 8),

    /** Carta especial "Batida" que vale 100 pontos, sem limite aplicável. */
    GOING_OUT(100, 0),

    /** Carta especial "Morto" que vale 100 pontos, sem limite aplicável. */
    SECOND_HAND(100, 0);

    companion object {
        /**
         * Retorna o [CardType] correspondente ao índice fornecido, ou null se o índice for inválido.
         *
         * @param index Índice do tipo de carta no enum.
         * @return Instância de [CardType] correspondente, ou null se não existir.
         */
        fun fromIndex(index: Int): CardType? = entries.getOrNull(index)

        /**
         * Retorna uma lista dos tipos de carta, excluindo as canastras e cartas especiais.
         *
         * Útil para operações que envolvem apenas as cartas comuns.
         *
         * @return Lista contendo apenas os tipos de carta [CARD_5], [CARD_10], [CARD_15] e [CARD_20].
         */
        fun cardTypesForCardsOnly(): List<CardType> {
            return listOf(CARD_5, CARD_10, CARD_15, CARD_20)
        }
    }
}
