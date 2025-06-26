package com.dev.soarescrf.buracoscore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dev.soarescrf.buracoscore.R
import com.dev.soarescrf.buracoscore.data.database.AppDatabase
import com.dev.soarescrf.buracoscore.data.model.Match
import com.dev.soarescrf.buracoscore.data.preferences.PlayerNamePreferenceManager
import com.dev.soarescrf.buracoscore.databinding.ActivityMainBinding
import com.dev.soarescrf.buracoscore.databinding.CustomDialogConfirmationBinding
import com.dev.soarescrf.buracoscore.databinding.CustomDialogNewNameBinding
import com.dev.soarescrf.buracoscore.utils.TextUtils
import com.dev.soarescrf.buracoscore.utils.setLightStatusBarIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Tela principal do aplicativo Buraco Score, responsável por gerenciar o placar
 * dos jogadores, salvar partidas e navegar entre telas.
 */
class MainActivity : AppCompatActivity() {
    /** Binding gerado para acessar as views do layout */
    private lateinit var binding: ActivityMainBinding

    private lateinit var namePrefs: PlayerNamePreferenceManager
    private lateinit var addPointsLauncher: ActivityResultLauncher<Intent>

    /**
     * Inicializa a interface e configura os listeners e preferências ao criar a Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        applyWindowInsets()

        // Altera os ícones da status bar para escuros
        window.setLightStatusBarIcons(true)

        namePrefs = PlayerNamePreferenceManager(this)
        setupActivityResultLauncher()
        setupClickListeners()
        loadPlayerNames()
        handleScoreUpdate(intent)
    }

    /**
     * Trata novas intents recebidas durante a execução da Activity.
     *
     * @param intent A nova intent recebida.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleScoreUpdate(intent)
    }

    /**
     * Aplica padding para os componentes evitando sobreposição com status bar e navigation bar.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Inicializa o launcher responsável por receber o resultado da [AddPointsActivity].
     */
    private fun setupActivityResultLauncher() {
        addPointsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let(::handleScoreUpdate)
                }
            }
    }

    /**
     * Define os listeners para os componentes interativos da tela.
     */
    private fun setupClickListeners() = with(binding) {
        imageStartA.setOnClickListener { openAddPointsActivity(textPlayerA) }
        imageStartB.setOnClickListener { openAddPointsActivity(textPlayerB) }

        imageEditPlayerNameA.setOnClickListener { showChangeNameDialog(textPlayerA) }
        imageEditPlayerNameB.setOnClickListener { showChangeNameDialog(textPlayerB) }

        imageQuestion.setOnClickListener { openRulesActivity() }
        imageMatchHistory.setOnClickListener { openMatchHistoryActivity() }

        buttonSaveScore.setOnClickListener { showSaveMatchConfirmationDialog() }
    }

    /**
     * Salva o novo nome do jogador nas preferências.
     *
     * @param playerTextView O TextView do jogador.
     * @param newName O novo nome a ser salvo.
     */
    private fun savePlayerName(playerTextView: TextView, newName: String) = with(binding) {
        when (playerTextView) {
            textPlayerA -> namePrefs.savePlayerNameA(newName)
            textPlayerB -> namePrefs.savePlayerNameB(newName)
        }
    }

    /**
     * Carrega os nomes dos jogadores salvos nas preferências.
     */
    private fun loadPlayerNames() = with(binding) {
        textPlayerA.text = namePrefs.getPlayerNameA()
        textPlayerB.text = namePrefs.getPlayerNameB()
    }

    /**
     * Atualiza o placar de acordo com os dados recebidos na [Intent].
     *
     * @param intent Intent contendo informações do jogador e pontuação.
     */
    private fun handleScoreUpdate(intent: Intent) = with(binding) {
        val playerPosition = intent.getStringExtra("player_position") ?: return
        val score = intent.getStringExtra("finalScore") ?: return

        when (playerPosition) {
            "A" -> textPointsA.text = score
            "B" -> textPointsB.text = score
        }
    }

    /**
     * Exibe um diálogo para o usuário alterar o nome de um jogador.
     *
     * @param playerTextView O TextView correspondente ao jogador a ser renomeado.
     */
    private fun showChangeNameDialog(playerTextView: TextView) {
        // Infla o layout do diálogo usando View Binding
        val dialogBinding = CustomDialogNewNameBinding.inflate(layoutInflater)

        // Cria o AlertDialog com fundo transparente usando o root do binding
        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogBinding.root)
            .create()

        // Define o título com o nome atual do jogador
        dialogBinding.textDialogTitle.text = playerTextView.text

        // Configura o clique no ícone de salvar
        dialogBinding.imageConfirm.setOnClickListener {
            val newName = dialogBinding.inputNewPlayerName.text.toString()
                .trim()
                .replace("\\s+".toRegex(), " ")

            if (newName.isEmpty()) {
                showToast(getString(R.string.fill_field))
                return@setOnClickListener
            }

            savePlayerName(playerTextView, newName)
            dialog.dismiss()
            loadPlayerNames()
        }

        dialog.show()
    }

    /**
     * Exibe um diálogo de confirmação antes de salvar o placar da partida.
     */
    private fun showSaveMatchConfirmationDialog() {
        if (isBothScoresZero()) {
            showToast(getString(R.string.calculate_score_before_save))
            return
        }

        // Infla o layout do diálogo usando View Binding
        val dialogBinding = CustomDialogConfirmationBinding.inflate(layoutInflater)

        // Cria o AlertDialog com fundo transparente usando o root do binding
        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogBinding.root)
            .create()

        // Configura os textos e listeners via binding
        dialogBinding.textDialogTitle.text = getString(R.string.confirmation)
        dialogBinding.textMessage.text = getString(R.string.confirm_save_match)
        TextUtils.applyJustifyAlignment(dialogBinding.textMessage)

        dialogBinding.imageCheck.setOnClickListener {
            saveMatchScore()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Salva os dados da partida no banco de dados local.
     */
    private fun saveMatchScore() = with(binding) {
        val match = Match.create(
            playerA = textPlayerA.text.toString(),
            pointsA = textPointsA.text.toString(),
            playerB = textPlayerB.text.toString(),
            pointsB = textPointsB.text.toString(),
            date = getCurrentDateFormatted()
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance(applicationContext).matchDao().insertMatch(match)
            }
            showToast(getString(R.string.match_data_saved))
            resetScores()
        }
    }

    /**
     * Verifica se ambos os placares estão zerados.
     *
     * @return `true` se os dois placares forem zero, caso contrário `false`.
     */
    private fun isBothScoresZero(): Boolean = with(binding) {
        return textPointsA.text.toString() == getString(R.string.zero) &&
                textPointsB.text.toString() == "0"
    }

    /**
     * Reseta os placares dos dois jogadores para zero.
     */
    private fun resetScores() = with(binding) {
        textPointsA.text = getString(R.string.zero)
        textPointsB.text = getString(R.string.zero)
    }

    /**
     * Retorna a data atual formatada no padrão "dd/MM/yyyy",
     * considerando o fuso horário do sistema e o locale pt-BR.
     *
     * @return Data atual do sistema formatada como string.
     */
    private fun getCurrentDateFormatted(): String {
        val localeBrazil = Locale.Builder()
            .setLanguage("pt")
            .setRegion("BR")
            .build()

        val sdf = SimpleDateFormat("dd/MM/yyyy", localeBrazil)
        val currentDate = Calendar.getInstance().time
        return sdf.format(currentDate)
    }

    /**
     * Abre a tela de adicionar pontos para o jogador correspondente.
     *
     * @param playerTextView O TextView do jogador que iniciou a ação.
     */
    private fun openAddPointsActivity(playerTextView: TextView) {
        val playerPosition = if (playerTextView == binding.textPlayerA) "A" else "B"
        val playerName = playerTextView.text.toString()
        val intent = Intent(this, AddPointsActivity::class.java).apply {
            putExtra("player_position", playerPosition)
            putExtra("player_name", playerName)
        }
        addPointsLauncher.launch(intent)
    }

    /**
     * Abre a tela de regras do jogo.
     */
    private fun openRulesActivity() {
        startActivity(Intent(this, RulesActivity::class.java))
    }

    /**
     * Abre o histórico de partidas.
     */
    private fun openMatchHistoryActivity() {
        startActivity(Intent(this, MatchHistoryActivity::class.java))
    }

    /**
     * Exibe uma mensagem Toast para o usuário.
     *
     * @param message Mensagem a ser exibida.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
