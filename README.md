# InnovaGAB

Plataforma mobile corporativa de inovação desenvolvida para o **Challenge Grupo Águia Branca — FIAP ADS**.

Conecta **Operadores**, **Gestores** e **Liderança** em um único ecossistema de inovação, permitindo captura de ideias, priorização, criação de projetos e acompanhamento de resultados via dashboard executivo.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM |
| Backend | Firebase Authentication + Firestore |
| Navegação | Navigation Compose |
| Estado | StateFlow / MutableStateFlow |
| Async | Coroutines |
| Gráficos | MPAndroidChart |

---

## Pré-requisitos

- [Android Studio](https://developer.android.com/studio) (inclui JDK, Kotlin e SDK)
- Android SDK instalado (feito automaticamente pelo Android Studio)
- Java 17+ (já incluso no Android Studio)

> O arquivo `google-services.json` já está configurado no repositório, apontando para o Firebase do projeto. Não é necessário criar um novo projeto Firebase para compilar e rodar.

---

## Como compilar e rodar

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/Challenge---Grupo-guia-Branca.git
cd Challenge---Grupo-guia-Branca
```

### 2. Compile o APK

```bash
# Linux/macOS
JAVA_HOME=/usr/local/android-studio/android-studio/jbr ./gradlew assembleDebug

# Windows (PowerShell)
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat assembleDebug
```

> O APK gerado estará em: `app/build/outputs/apk/debug/app-debug.apk`

### 3. Instalar em dispositivo físico ou emulador

```bash
# Ver dispositivos conectados
adb devices

# Instalar o APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Abrir o app
adb shell am start -n com.innovagab.app/.MainActivity
```

### Atalho: compilar + instalar + abrir de uma vez

```bash
JAVA_HOME=/usr/local/android-studio/android-studio/jbr ./gradlew assembleDebug && \
adb install -r app/build/outputs/apk/debug/app-debug.apk && \
adb shell am start -n com.innovagab.app/.MainActivity
```

---

## Abrindo pelo Android Studio

1. Abra o Android Studio
2. `File → Open` → selecione a pasta do projeto
3. Aguarde o Gradle sincronizar
4. Clique em ▶ **Run** (ou `Shift + F10`)

---

## Credenciais de acesso (demo)

A tela de login exibe botões de **acesso rápido** para cada perfil (apenas em builds de debug):

| Botão | E-mail | Senha | Perfil |
|---|---|---|---|
| 👷 Operador | `operador@innovagab.com` | `senha123` | Operador |
| 📋 Gestor | `gestor@innovagab.com` | `senha123` | Gestor |
| 👑 Liderança | `lideranca@innovagab.com` | `senha123` | Liderança |

> Também é possível digitar as credenciais manualmente nos campos de e-mail e senha.

---

## Dados de demonstração

O banco já está populado com dados de demonstração:

| Coleção | Quantidade |
|---|---|
| Usuários | 3 (operador, gestor, liderança) |
| Ideias | 21 (em vários status) |
| Projetos | 8 (ativos, concluídos, em pausa) |
| Estratégias | 8 (nos 6 pilares corporativos) |
| Reconhecimentos | 7 badges atribuídos |

---

## Estrutura do projeto

```
app/src/main/java/com/innovagab/app/
├── data/
│   ├── auth/          # AuthRepository, UserProfile, UserRole
│   ├── ideas/         # Idea model + IdeaRepository
│   ├── projects/      # Project model + ProjectRepository
│   ├── strategies/    # Strategy model + StrategyRepository
│   └── gamification/  # Badges, pontuação, ranking
├── features/
│   ├── auth/          # LoginScreen + AuthViewModel
│   ├── operador/      # Criar ideia, minhas ideias, detalhes
│   ├── gestor/        # Pipeline, projetos, editar projeto
│   ├── lideranca/     # Dashboard KPI, estratégias
│   └── recognition/   # Leaderboard, conquistas, badges
├── navigation/        # AppNavigation, rotas por perfil
└── ui/
    ├── components/    # Componentes reutilizáveis
    └── theme/         # Cores, tipografia, tema Material 3
```

---

## Fluxo principal

```
Operador cria ideia
       ↓
Gestor aprova/rejeita no pipeline
       ↓
Projeto é criado a partir da ideia aprovada
       ↓
Liderança acompanha KPIs, ROI e dashboard executivo
```

---

## Perfis de usuário

| Perfil | `role` no Firestore | Acesso |
|---|---|---|
| Operador | `operador` | Criar ideias, acompanhar status, ver reconhecimentos |
| Gestor | `gestor` | Pipeline de aprovação, gerenciar projetos |
| Liderança | `lideranca` | Dashboard executivo, estratégias, conceder reconhecimentos |

---

## Repopular o banco de dados (opcional)

Caso queira resetar os dados de demonstração, há um script de seed em `scripts/`:

```bash
# Necessário: gerar chave de serviço em Firebase Console
# Project Settings → Service Accounts → Generate new private key
# Salvar como scripts/serviceAccountKey.json

cd scripts
npm install
node seed.js
```

---

## Firebase

O projeto utiliza o Firebase `fiap-aguia-branca-70c25`.

Serviços ativos:
- **Authentication** — login com e-mail e senha
- **Firestore** — banco de dados em tempo real
