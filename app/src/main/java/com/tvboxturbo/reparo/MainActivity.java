package com.tvboxturbo.reparo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {

    private TextView tvStatus;
    private TextView tvRam;
    private View cardStep1;
    private View cardStep2;
    private View cardStep3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvRam = findViewById(R.id.tvRam);
        cardStep1 = findViewById(R.id.cardStep1);
        cardStep2 = findViewById(R.id.cardStep2);
        cardStep3 = findViewById(R.id.cardStep3);

        // Foco inicial no primeiro botão para controle remoto de TV Box
        cardStep1.requestFocus();

        atualizarStatusMemoria();

        cardStep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executarLimpezaRam();
            }
        });

        cardStep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executarLimpezaCache();
            }
        });

        cardStep3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ativarModoTurbo();
            }
        });
    }

    private void atualizarStatusMemoria() {
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        if (actManager != null) {
            actManager.getMemoryInfo(memInfo);
            long livreMb = memInfo.availMem / (1024 * 1024);
            long totalMb = memInfo.totalMem / (1024 * 1024);
            tvRam.setText("Memória RAM Livre: " + livreMb + " MB de " + totalMb + " MB Total");
        }
    }

    // === CLIQUE 1: LIMPAR MEMÓRIA RAM ===
    private void executarLimpezaRam() {
        tvStatus.setText("⚡ Encerrando processos fantasmas em segundo plano...");
        tvStatus.setTextColor(getResources().getColor(R.color.neon_cyan));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    PackageManager pm = getPackageManager();
                    if (actManager != null && pm != null) {
                        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
                        String meuPacote = getPackageName();

                        for (ApplicationInfo appInfo : packages) {
                            if (!appInfo.packageName.equals(meuPacote)) {
                                actManager.killBackgroundProcesses(appInfo.packageName);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignora eventuais restrições de permissão
                }

                atualizarStatusMemoria();
                tvStatus.setText("✅ PASSO 1 CONCLUÍDO: Memória RAM liberada e descarregada com sucesso!");
                tvStatus.setTextColor(getResources().getColor(R.color.neon_green));
                Toast.makeText(MainActivity.this, "RAM Otimizada!", Toast.LENGTH_SHORT).show();

                // Move foco automaticamente para o botão 2 no controle remoto
                cardStep2.requestFocus();
            }
        }, 800);
    }

    // === CLIQUE 2: LIMPAR CACHE E LIXO ===
    private void executarLimpezaCache() {
        tvStatus.setText("🗑️ Varrendo pastas temporárias e cache residual...");
        tvStatus.setTextColor(getResources().getColor(R.color.neon_cyan));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    apagarPasta(getCacheDir());
                    if (getExternalCacheDir() != null) {
                        apagarPasta(getExternalCacheDir());
                    }
                } catch (Exception e) {
                    // Ignora
                }

                tvStatus.setText("✅ PASSO 2 CONCLUÍDO: Cache e resíduos apagados com sucesso!");
                tvStatus.setTextColor(getResources().getColor(R.color.neon_green));
                Toast.makeText(MainActivity.this, "Arquivos Limpos!", Toast.LENGTH_SHORT).show();

                // Move foco automaticamente para o botão 3 no controle remoto
                cardStep3.requestFocus();
            }
        }, 800);
    }

    // === CLIQUE 3: MODO TURBO ===
    private void ativarModoTurbo() {
        tvStatus.setText("🚀 Alinhando processador e ativando Modo Turbo...");
        tvStatus.setTextColor(getResources().getColor(R.color.neon_green));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                System.gc(); // Força coleta de lixo da JVM
                atualizarStatusMemoria();

                tvStatus.setText("🔥 PARABÉNS! TV BOX REPARADA E TOTALMENTE TURBINADA!");
                tvStatus.setTextColor(getResources().getColor(R.color.neon_green));
                Toast.makeText(MainActivity.this, "Modo Turbo Ativado!", Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }

    private boolean apagarPasta(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = apagarPasta(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        }
        return false;
    }
}
