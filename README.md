# INNOVAGAB — IMPLEMENTATION NEXT STEPS

## CONTEXTO

O projeto InnovaGAB é uma plataforma corporativa de inovação integrada desenvolvida para o Challenge Grupo Águia Branca (FIAP ADS).

A aplicação está sendo construída com:

* Kotlin
* Jetpack Compose
* Firebase Authentication
* Firestore
* Navigation Compose
* MVVM simples

O objetivo do sistema é conectar:

* operadores;
* gestores;
* liderança;

em um fluxo completo de inovação corporativa:

```text
Ideia → Aprovação → Projeto → Resultado
```

---

# STATUS ATUAL

## Já implementado

* estrutura base do projeto;
* autenticação;
* navegação;
* design system;
* módulo de ideias;
* pipeline inicial;
* dashboard inicial;
* identidade visual corporativa.

---

# PRIORIDADE ATUAL

## FALTAM 3 GRANDES ETAPAS

1. Estratégias Corporativas
2. Gamificação
3. Refinamento Visual Final

---

# 8. ESTRATÉGIAS CORPORATIVAS

## OBJETIVO

Criar um módulo institucional voltado para direcionamentos estratégicos da empresa.

As estratégias devem parecer:

* pilares organizacionais;
* direcionamentos executivos;
* metas corporativas;
* objetivos estratégicos.

---

# TELAS NECESSÁRIAS

## StrategiesScreen

Tela principal contendo:

* lista de estratégias;
* cards corporativos;
* visual institucional;
* layout clean.

### Liderança

Pode:

* criar;
* editar;
* remover.

### Gestor e Operador

Apenas visualizar.

---

## CreateStrategyScreen

Tela de criação/edição.

Campos:

* título;
* descrição;
* categoria;
* prioridade;
* impacto estratégico.

Adicionar:

* validações;
* loading state;
* feedback visual.

---

## StrategyDetailsScreen

Tela detalhada da estratégia.

Exibir:

* descrição completa;
* objetivos;
* impacto esperado;
* alinhamento organizacional.

Visual:

* institucional;
* minimalista;
* executivo.

---

# COMPONENTES NECESSÁRIOS

## StrategyCard

Card moderno contendo:

* título;
* categoria;
* prioridade;
* descrição resumida.

Visual:

* premium;
* corporativo;
* clean.

---

## StrategyCategoryChip

Categorias:

* Sustentabilidade
* Eficiência Operacional
* Inovação
* Experiência do Cliente
* Produtividade

---

# FIRESTORE

## collection: strategies

```json
{
  "id": "",
  "title": "",
  "description": "",
  "category": "",
  "priority": "",
  "impact": "",
  "createdAt": ""
}
```

---

# REQUISITOS VISUAIS

O módulo deve parecer:

* plataforma corporativa;
* painel executivo;
* sistema enterprise.

Inspirar visualmente em:

* SAP Fiori
* Notion Enterprise
* Monday.com
* Jira moderno

---

# 9. GAMIFICAÇÃO

## OBJETIVO

Criar um sistema leve de reconhecimento profissional.

IMPORTANTE:

* NÃO parecer gamer;
* NÃO exagerar em cores;
* manter visual corporativo.

A gamificação deve transmitir:

* reconhecimento;
* engajamento;
* contribuição organizacional.

---

# FUNCIONALIDADES

## Pontuação

Usuários acumulam pontos por:

* ideias criadas;
* ideias aprovadas;
* projetos gerados.

---

# BADGES

## Exemplos

* Inovador
* Solucionador
* Destaque do mês
* Colaborador Ativo
* Visionário

---

# TELAS NECESSÁRIAS

## LeaderboardScreen

Mostrar:

* ranking;
* pontuação;
* top colaboradores.

Visual:

* clean;
* institucional;
* executivo.

---

## AchievementsScreen

Mostrar:

* badges;
* conquistas;
* progresso do usuário.

---

# COMPONENTES NECESSÁRIOS

## BadgeCard

Conteúdo:

* ícone;
* nome;
* descrição;
* status.

---

## LeaderboardItem

Conteúdo:

* posição;
* avatar;
* nome;
* pontuação.

---

# FIRESTORE

## collection: achievements

```json
{
  "userId": "",
  "points": 0,
  "badges": [],
  "rank": 0
}
```

---

# DIREÇÃO VISUAL

Evitar:

* aparência gamer;
* neon;
* excesso de animações;
* poluição visual.

Priorizar:

* reconhecimento profissional;
* elegância;
* minimalismo.

---

# 10. REFINAMENTO VISUAL FINAL

## OBJETIVO

Transformar o MVP em uma aplicação com aparência premium.

---

# MELHORIAS NECESSÁRIAS

## Espaçamentos

Padronizar:

* paddings;
* margins;
* grids;
* alinhamentos.

Sistema:

* 8dp
* 16dp
* 24dp
* 32dp

---

# HIERARQUIA VISUAL

Melhorar:

* títulos;
* subtítulos;
* pesos tipográficos;
* contraste;
* leitura.

---

# SOMBRAS

Adicionar sombras suaves:

```kotlin
shadowElevation = 4.dp
```

Evitar:

* sombras pesadas;
* visual datado.

---

# EMPTY STATES

Criar:

* telas vazias elegantes;
* ilustrações simples;
* mensagens institucionais.

---

# LOADING STATES

Adicionar:

* skeleton loading;
* shimmer;
* progress indicators modernos.

---

# MICRO FEEDBACKS

Adicionar:

* snackbars;
* estados de sucesso;
* estados de erro;
* animações suaves.

---

# CONSISTÊNCIA

Garantir:

* mesma identidade visual;
* mesmos padrões;
* mesmos componentes;
* mesma tipografia.

---

# DESIGN SYSTEM

## CORES

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

# OBJETIVO FINAL

A aplicação deve parecer:

* um produto enterprise real;
* um software interno corporativo premium;
* uma plataforma moderna de inovação corporativa.

O foco principal NÃO é:

* complexidade extrema;
* arquitetura exagerada.

O foco principal é:

* UX;
* consistência;
* visual corporativo;
* fluxo funcional;
* percepção de produto real.
