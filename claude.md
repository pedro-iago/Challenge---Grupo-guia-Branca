# INNOVAGAB — CLAUDE.MD

## CONTEXTO DO PROJETO

O projeto chama-se **InnovaGAB**.

Trata-se de uma plataforma mobile corporativa de inovação integrada desenvolvida para o Challenge Grupo Águia Branca (FIAP ADS).

O objetivo da aplicação é conectar:

* operadores;
* gestores;
* liderança;

em um único ecossistema de inovação corporativa.

A aplicação deve permitir:

* captura de ideias;
* priorização;
* criação de projetos;
* acompanhamento de resultados;
* visualização estratégica via dashboard.

A aplicação NÃO deve parecer um projeto acadêmico simples.

Ela deve parecer:

* um produto corporativo real;
* um software enterprise moderno;
* uma plataforma SaaS interna.

---

# STACK OBRIGATÓRIA

## Linguagem

* Kotlin

## UI

* Jetpack Compose

## Arquitetura

* MVVM simples

## Backend/Banco

* Firebase Authentication
* Firestore

## Navegação

* Navigation Compose

## Estado

* StateFlow
* MutableStateFlow

## Async

* Coroutines

## Dashboard

* MPAndroidChart

---

# OBJETIVO PRINCIPAL

Criar um MVP funcional e visualmente profissional.

Priorizar:

* UX;
* consistência visual;
* fluxo completo;
* legibilidade do código;
* velocidade de desenvolvimento.

NÃO criar:

* arquitetura enterprise complexa;
* abstrações desnecessárias;
* overengineering.

---

# REGRAS IMPORTANTES

## NÃO UTILIZAR

* XML layouts
* Fragments tradicionais
* Clean Architecture complexa
* Dagger/Hilt complexo
* Multi-module
* Offline-first
* Room
* Retrofit
* overengineering

---

# ESTRUTURA DO APP

A estrutura deve seguir:

app/
├── ui/
├── screens/
├── components/
├── navigation/
├── models/
├── viewmodels/
├── repositories/
├── firebase/
├── theme/
└── utils/

---

# DESIGN SYSTEM

## ESTILO VISUAL

O app deve transmitir:

* inovação;
* organização;
* gestão corporativa;
* tecnologia;
* clareza.

Inspirado visualmente em:

* Monday.com
* Linear
* SAP Fiori
* Notion Enterprise
* Jira moderno

---

# PALETA DE CORES

Primary:
#1D3F8E

Secondary:
#142B63

Background:
#F8FAFC

Card:
#FFFFFF

Title:
#111827

Body:
#374151

Accent:
#ED145B

---

# TIPOGRAFIA

## Títulos

Barlow SemiBold

## Corpo

Inter Regular

---

# COMPONENTES

Todos os componentes devem:

* possuir espaçamento consistente;
* cantos arredondados;
* sombras suaves;
* aparência moderna;
* visual minimalista.

Border radius padrão:
16dp

---

# TELAS OBRIGATÓRIAS

## AUTH

### LoginScreen

Campos:

* email
* senha

Perfis:

* operador
* gestor
* liderança

---

# OPERADOR

## HomeScreen

* resumo
* atalhos
* estratégias

## CreateIdeaScreen

Campos:

* título
* descrição
* categoria
* impacto esperado

## MyIdeasScreen

Lista de ideias do usuário

## IdeaDetailsScreen

Visualização detalhada

---

# GESTOR

## IdeasPipelineScreen

Lista de ideias

Funções:

* aprovar
* rejeitar
* priorizar

## ProjectsScreen

Lista de projetos

## EditProjectScreen

Atualização de:

* progresso
* ROI
* investimento
* prazo

---

# LIDERANÇA

## DashboardScreen

Exibir:

* ROI
* produtividade
* redução de custos
* projetos ativos
* ideias aprovadas

Usar:

* cards;
* gráficos;
* KPIs visuais.

## StrategyManagementScreen

CRUD de estratégias corporativas.

---

# FLUXO PRINCIPAL

O fluxo principal da plataforma é:

Operador cria ideia
↓
Gestor aprova ideia
↓
Projeto é criado
↓
Liderança acompanha indicadores

Esse fluxo é o núcleo do projeto.

---

# FIREBASE

## COLLECTIONS

### users

* id
* name
* email
* role

### ideas

* id
* title
* description
* category
* impact
* status
* priority
* authorId
* createdAt

### projects

* id
* name
* progress
* roi
* investment
* deadline
* status

### strategies

* id
* title
* description

---

# STATUS DE IDEIAS

Possíveis status:

* enviada
* em análise
* aprovada
* rejeitada
* virou projeto

---

# UX

A aplicação deve:

* ser extremamente intuitiva;
* parecer rápida;
* ter poucos cliques;
* possuir telas limpas;
* priorizar clareza.

Especialmente para o perfil operador.

---

# GAMIFICAÇÃO

Adicionar:

* badges;
* pontuação;
* reconhecimento;
* ranking simples.

Exemplo:

* Inovador
* Solucionador
* Destaque do mês

---

# QUALIDADE VISUAL

A interface deve parecer:

* moderna;
* premium;
* corporativa;
* minimalista.

Evitar:

* excesso de cores;
* gradients exagerados;
* visual gamer;
* poluição visual.

---

# PADRÕES DE CÓDIGO

* código limpo;
* componentes reutilizáveis;
* evitar duplicação;
* funções pequenas;
* nomes claros;
* Compose idiomático;
* MVVM simples.

---

# COMPONENTIZAÇÃO

Criar componentes reutilizáveis:

* cards;
* buttons;
* KPI cards;
* status chips;
* top bars;
* text fields.

---

# DASHBOARD

O dashboard deve parecer executivo.

Mostrar:

* gráficos;
* indicadores;
* KPIs;
* progresso de projetos.

Visual:

* clean;
* corporate;
* analytics-focused.

---

# PRIORIDADES DE IMPLEMENTAÇÃO

## FASE 1

* setup do projeto
* tema
* navegação
* autenticação

## FASE 2

* CRUD de ideias

## FASE 3

* aprovação e pipeline

## FASE 4

* projetos

## FASE 5

* dashboard

## FASE 6

* refinamento visual

---

# IMPORTANTE

Priorizar:

* experiência;
* consistência;
* funcionamento.

O objetivo NÃO é criar um sistema extremamente complexo.

O objetivo é criar um MVP corporativo convincente.

---

# COMPORTAMENTO ESPERADO DO CLAUDE

Ao gerar código:

* manter consistência visual;
* seguir a arquitetura definida;
* evitar overengineering;
* reutilizar componentes;
* priorizar Compose moderno;
* gerar código limpo e funcional.

Sempre pensar como:

* um engenheiro Android sênior;
* um product designer;
* um arquiteto de software pragmático.

O resultado final deve parecer:
“um software corporativo real pronto para apresentação.”

