/**
 * InnovaGAB — Firebase Seed Script
 *
 * Popula o Firestore com dados de demonstração completos.
 * Limpa os dados existentes antes de inserir novos.
 *
 * COMO USAR:
 *   1. Gere a chave de serviço no Firebase Console:
 *      → Project Settings → Service Accounts → Generate new private key
 *      → Salve como: scripts/serviceAccountKey.json
 *
 *   2. Crie o Firestore Database no Firebase Console em modo de teste
 *
 *   3. Execute:
 *      cd scripts
 *      npm install
 *      node seed.js
 */

const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const auth = admin.auth();

// ─── Limpar coleção ───────────────────────────────────────────────────────────

async function clearCollection(name) {
  const snap = await db.collection(name).get();
  if (snap.empty) return;
  const batches = [];
  let batch = db.batch();
  let count = 0;
  for (const doc of snap.docs) {
    batch.delete(doc.ref);
    count++;
    if (count === 400) {
      batches.push(batch.commit());
      batch = db.batch();
      count = 0;
    }
  }
  if (count > 0) batches.push(batch.commit());
  await Promise.all(batches);
  console.log(`   🗑  Limpou '${name}' (${snap.size} docs)`);
}

// ─── Usuários ────────────────────────────────────────────────────────────────

const USERS = [
  {
    email: "operador@innovagab.com",
    password: "senha123",
    name: "Carlos Operador",
    role: "operador",
  },
  {
    email: "gestor@innovagab.com",
    password: "senha123",
    name: "Ana Gestora",
    role: "gestor",
  },
  {
    email: "lideranca@innovagab.com",
    password: "senha123",
    name: "Roberto Liderança",
    role: "lideranca",
  },
];

// ─── Helpers ─────────────────────────────────────────────────────────────────

const now = Date.now();
const day = 86_400_000;
const ago = (d) => now - d * day;

function ideasFor(authorId, list) {
  return list.map((idea) => ({ ...idea, authorId }));
}

// ─── IDEAS ────────────────────────────────────────────────────────────────────
// Distribuição de pontos:
//  operador  → 2×completed(60) + 2×in_progress(40) + 3×approved(30) + 2×pending(5) + 1×rejected(2) = 312 pts 🥇
//  gestor    → 1×completed(60) + 1×in_progress(40) + 2×approved(30) + 2×pending(5) + 1×rejected(2) = 172 pts 🥈
//  lideranca → 1×in_progress(40) + 1×approved(30) + 2×pending(5) = 80 pts 🥉

function buildIdeas(uids) {
  const op = uids["operador@innovagab.com"];
  const ge = uids["gestor@innovagab.com"];
  const li = uids["lideranca@innovagab.com"];

  return [
    // ── Operador (10 ideias) ──────────────────────────────────────────────
    ...ideasFor(op, [
      {
        title: "Automação do processo de onboarding",
        description: "Criar fluxo automatizado para integração de novos colaboradores, reduzindo o tempo de processo de 5 dias para 1 dia útil.",
        category: "Processo", impact: "Alto", status: "completed", priority: "high",
        createdAt: ago(60), comment: "Concluído com sucesso. NPS interno subiu 22 pontos.",
      },
      {
        title: "Otimização da cadeia de suprimentos",
        description: "Revisão dos contratos com fornecedores e implementação de sistema de previsão de demanda para reduzir estoque parado em 30%.",
        category: "Processo", impact: "Alto", status: "completed", priority: "high",
        createdAt: ago(90), comment: "Estoque parado reduzido em 31%. Meta superada.",
      },
      {
        title: "Dashboard de métricas em tempo real",
        description: "Desenvolver painel centralizado com KPIs operacionais atualizados em tempo real para tomada de decisão mais rápida.",
        category: "Tecnologia", impact: "Alto", status: "in_progress", priority: "high",
        createdAt: ago(30), comment: "Em desenvolvimento pelo time de TI. 65% concluído.",
      },
      {
        title: "App mobile para coleta de dados de campo",
        description: "Aplicativo para operadores registrarem ocorrências e leituras diretamente do campo, eliminando planilhas manuais.",
        category: "Tecnologia", impact: "Alto", status: "in_progress", priority: "medium",
        createdAt: ago(20), comment: "MVP em testes com equipe piloto.",
      },
      {
        title: "Padronização de procedimentos operacionais",
        description: "Documentar e padronizar os 20 principais procedimentos operacionais em formato digital interativo.",
        category: "Processo", impact: "Médio", status: "approved", priority: "high",
        createdAt: ago(15), comment: "Aprovado. Início previsto para semana que vem.",
      },
      {
        title: "Sistema de manutenção preditiva",
        description: "Implementar sensores IoT nas máquinas principais para prever falhas antes que aconteçam.",
        category: "Tecnologia", impact: "Alto", status: "approved", priority: "high",
        createdAt: ago(25), comment: "Aprovado pela gestão. Cotações em andamento.",
      },
      {
        title: "Programa de redução de desperdício",
        description: "Mapear e eliminar os 10 principais pontos de desperdício identificados nas auditorias internas.",
        category: "Processo", impact: "Médio", status: "approved", priority: "medium",
        createdAt: ago(10), comment: "Aprovado. Cronograma definido.",
      },
      {
        title: "Gamificação do controle de qualidade",
        description: "Sistema de pontos e rankings para engajar operadores no processo de controle de qualidade.",
        category: "Pessoas", impact: "Médio", status: "pending", priority: "low",
        createdAt: ago(5), comment: "",
      },
      {
        title: "Integração com ERP via API",
        description: "Conectar o sistema de apontamento de produção diretamente ao ERP para eliminar dupla entrada de dados.",
        category: "Tecnologia", impact: "Alto", status: "pending", priority: "medium",
        createdAt: ago(3), comment: "",
      },
      {
        title: "Reaproveitamento de resíduos sólidos",
        description: "Parceria com cooperativas para coleta e reaproveitamento dos resíduos gerados na produção.",
        category: "Outro", impact: "Baixo", status: "rejected", priority: "low",
        createdAt: ago(120), comment: "Rejeitada. Custo logístico inviabiliza no momento.",
      },
    ]),

    // ── Gestor (7 ideias) ─────────────────────────────────────────────────
    ...ideasFor(ge, [
      {
        title: "Metodologia ágil nas equipes de projeto",
        description: "Implantar Scrum e Kanban em todos os projetos internos para aumentar velocidade de entrega e visibilidade.",
        category: "Processo", impact: "Alto", status: "completed", priority: "high",
        createdAt: ago(80), comment: "Velocidade de entrega aumentou 40%. Sucesso total.",
      },
      {
        title: "Plataforma de gestão de fornecedores",
        description: "Portal centralizado para avaliação, homologação e comunicação com fornecedores estratégicos.",
        category: "Tecnologia", impact: "Alto", status: "in_progress", priority: "high",
        createdAt: ago(35), comment: "60% implementado. Go-live previsto para próximo mês.",
      },
      {
        title: "Programa de desenvolvimento de líderes",
        description: "Trilha de capacitação de 6 meses para coordenadores com foco em gestão ágil e liderança servidora.",
        category: "Pessoas", impact: "Alto", status: "approved", priority: "high",
        createdAt: ago(18), comment: "Aprovado com orçamento de R$150k.",
      },
      {
        title: "Indicadores de performance por equipe",
        description: "Criar painel de KPIs individualizados por equipe para facilitar gestão por resultados.",
        category: "Processo", impact: "Médio", status: "approved", priority: "medium",
        createdAt: ago(22), comment: "Aprovado. Em fase de definição de métricas.",
      },
      {
        title: "Revisão da estrutura de reuniões",
        description: "Reduzir tempo de reuniões em 50% com adoção de formatos mais eficientes (daily, one-on-one, retrospectiva).",
        category: "Processo", impact: "Médio", status: "pending", priority: "medium",
        createdAt: ago(7), comment: "",
      },
      {
        title: "Automação de relatórios executivos",
        description: "Scripts e dashboards que gerem automaticamente os relatórios mensais enviados à liderança.",
        category: "Tecnologia", impact: "Médio", status: "pending", priority: "low",
        createdAt: ago(2), comment: "",
      },
      {
        title: "Avaliação 360° digital",
        description: "Plataforma de avaliação 360 graus integrada ao sistema de RH para ciclos semestrais.",
        category: "Pessoas", impact: "Baixo", status: "rejected", priority: "low",
        createdAt: ago(100), comment: "Módulo já disponível no HRIS atual. Duplicidade.",
      },
    ]),

    // ── Liderança (4 ideias) ──────────────────────────────────────────────
    ...ideasFor(li, [
      {
        title: "Expansão para mercados do Nordeste",
        description: "Estudo de viabilidade e plano de entrada nos mercados de Fortaleza, Recife e Salvador até Q3 2025.",
        category: "Outro", impact: "Alto", status: "in_progress", priority: "high",
        createdAt: ago(40), comment: "Estudo de mercado 80% concluído. Resultados promissores.",
      },
      {
        title: "Programa de inovação aberta com startups",
        description: "Criar laboratório de inovação para co-desenvolver soluções com startups selecionadas por edital.",
        category: "Tecnologia", impact: "Alto", status: "approved", priority: "high",
        createdAt: ago(14), comment: "Aprovado pelo conselho. Edital publicado em breve.",
      },
      {
        title: "Política de trabalho híbrido permanente",
        description: "Formalizar política de trabalho híbrido (3×2) como benefício permanente para colaboradores elegíveis.",
        category: "Pessoas", impact: "Médio", status: "pending", priority: "medium",
        createdAt: ago(6), comment: "",
      },
      {
        title: "Certificação ISO 9001:2015",
        description: "Iniciar jornada de certificação da qualidade para fortalecer posicionamento no mercado B2B.",
        category: "Processo", impact: "Alto", status: "pending", priority: "high",
        createdAt: ago(1), comment: "",
      },
    ]),
  ];
}

// ─── STRATEGIES ───────────────────────────────────────────────────────────────

const STRATEGIES_SEED = [
  {
    title: "Expansão para mercados regionais",
    description: "Ampliar presença em 5 novos estados do Brasil até o final do ciclo, com foco nas regiões Norte e Nordeste.",
    pillar: "growth", status: "active", priority: "high",
    cycle: "2025-S1", deadline: "2025-12-31", owner: "Roberto Liderança", createdAt: ago(90),
  },
  {
    title: "Digitalização de processos operacionais",
    description: "Migrar 80% dos processos manuais para plataformas digitais, reduzindo tempo de execução e erros humanos.",
    pillar: "innovation", status: "active", priority: "high",
    cycle: "2025-S1", deadline: "2025-12-31", owner: "Ana Gestora", createdAt: ago(60),
  },
  {
    title: "Programa de desenvolvimento de líderes",
    description: "Capacitar 50 líderes de nível médio em gestão ágil, pensamento estratégico e liderança inclusiva.",
    pillar: "people", status: "active", priority: "medium",
    cycle: "2025-S1", deadline: "2025-09-30", owner: "Ana Gestora", createdAt: ago(45),
  },
  {
    title: "Redução de emissões de carbono",
    description: "Implementar iniciativas para reduzir emissões em 25% até 2026, incluindo energia renovável e mobilidade elétrica.",
    pillar: "sustainability", status: "draft", priority: "medium",
    cycle: "2025-S2", deadline: "2026-12-31", owner: "Roberto Liderança", createdAt: ago(20),
  },
  {
    title: "Excelência no atendimento ao cliente",
    description: "Atingir NPS de 80+ e tempo médio de resolução abaixo de 4 horas em todos os canais.",
    pillar: "customer", status: "active", priority: "high",
    cycle: "2025-S1", deadline: "2025-12-31", owner: "Ana Gestora", createdAt: ago(30),
  },
  {
    title: "Otimização de custos operacionais (2024)",
    description: "Reduzir custos em 15% através de automação e renegociação de contratos.",
    pillar: "efficiency", status: "archived", priority: "low",
    cycle: "2024-S2", deadline: "2024-12-31", owner: "Carlos Operador", createdAt: ago(180),
  },
  {
    title: "Inovação em modelos de negócio",
    description: "Explorar novas linhas de receita via serviços digitais e modelos de assinatura.",
    pillar: "innovation", status: "draft", priority: "high",
    cycle: "2025-S2", deadline: "2026-06-30", owner: "Roberto Liderança", createdAt: ago(8),
  },
  {
    title: "Cultura de alta performance",
    description: "Implementar OKRs em toda a empresa e criar ciclos trimestrais de avaliação e reconhecimento.",
    pillar: "people", status: "active", priority: "high",
    cycle: "2025-S1", deadline: "2025-12-31", owner: "Roberto Liderança", createdAt: ago(50),
  },
];

// ─── PROJECTS ─────────────────────────────────────────────────────────────────

const PROJECTS_SEED = [
  {
    title: "Sistema de Automação de Onboarding",
    description: "Desenvolvimento e implantação do fluxo automatizado de integração de novos colaboradores.",
    status: "active", progress: 0.65, roi: 125.5, investment: 85000,
    deadline: "2025-08-15", createdAt: ago(25),
  },
  {
    title: "Dashboard de KPIs em Tempo Real",
    description: "Painel centralizado com métricas operacionais integradas a todos os sistemas da empresa.",
    status: "active", progress: 0.40, roi: 0, investment: 120000,
    deadline: "2025-10-30", createdAt: ago(18),
  },
  {
    title: "Implantação de Energia Solar",
    description: "Instalação de painéis fotovoltaicos na sede principal com capacidade de 200kW.",
    status: "active", progress: 0.20, roi: 0, investment: 350000,
    deadline: "2025-11-30", createdAt: ago(12),
  },
  {
    title: "Otimização de Cadeia de Suprimentos",
    description: "Revisão contratual com fornecedores e implantação de sistema de previsão de demanda.",
    status: "completed", progress: 1.0, roi: 31.2, investment: 45000,
    deadline: "2025-03-31", createdAt: ago(90),
  },
  {
    title: "App Mobile de Feedback de Clientes",
    description: "Desenvolvimento do aplicativo iOS/Android para coleta de NPS e feedback em tempo real.",
    status: "on_hold", progress: 0.10, roi: 0, investment: 95000,
    deadline: "2025-12-15", createdAt: ago(8),
  },
  {
    title: "Plataforma de Gestão de Fornecedores",
    description: "Portal para homologação, avaliação e comunicação com fornecedores estratégicos.",
    status: "active", progress: 0.60, roi: 0, investment: 72000,
    deadline: "2025-09-30", createdAt: ago(35),
  },
  {
    title: "Programa de Manutenção Preditiva IoT",
    description: "Sensores e painel de monitoramento para predição de falhas nas linhas de produção.",
    status: "active", progress: 0.15, roi: 0, investment: 280000,
    deadline: "2026-03-31", createdAt: ago(5),
  },
  {
    title: "Certificação ISO 9001",
    description: "Jornada completa de certificação da qualidade com consultoria especializada.",
    status: "completed", progress: 1.0, roi: 18.5, investment: 38000,
    deadline: "2024-12-31", createdAt: ago(200),
  },
];

// ─── Funções de seed ──────────────────────────────────────────────────────────

async function seedUsers() {
  console.log("\n👥 Criando/atualizando usuários no Authentication...");
  const uids = {};

  for (const user of USERS) {
    try {
      let userRecord;
      try {
        userRecord = await auth.getUserByEmail(user.email);
        console.log(`   ✓ Já existe: ${user.email}`);
      } catch {
        userRecord = await auth.createUser({
          email: user.email,
          password: user.password,
          displayName: user.name,
        });
        console.log(`   + Criado:    ${user.email}`);
      }
      uids[user.email] = userRecord.uid;
      await db.collection("users").doc(userRecord.uid).set(
        { name: user.name, email: user.email, role: user.role },
        { merge: true }
      );
      console.log(`   ✓ Firestore: ${user.name} (${user.role})`);
    } catch (err) {
      console.error(`   ✗ Erro em ${user.email}:`, err.message);
    }
  }
  return uids;
}

async function seedIdeas(uids) {
  console.log("\n💡 Populando coleção 'ideas'...");
  const ideas = buildIdeas(uids);
  for (const idea of ideas) {
    const docRef = db.collection("ideas").doc();
    await docRef.set({ ...idea, id: docRef.id });
  }
  const byUser = {};
  ideas.forEach((i) => { byUser[i.authorId] = (byUser[i.authorId] || 0) + 1; });
  Object.entries(byUser).forEach(([uid, count]) =>
    console.log(`   + ${count} ideias para uid ${uid.slice(0, 8)}...`)
  );
  console.log(`   Total: ${ideas.length} ideias`);
}

async function seedStrategies() {
  console.log("\n🎯 Populando coleção 'strategies'...");
  for (const s of STRATEGIES_SEED) {
    const docRef = db.collection("strategies").doc();
    await docRef.set({ ...s, id: docRef.id });
    console.log(`   + ${s.title}`);
  }
}

async function seedProjects() {
  console.log("\n🚀 Populando coleção 'projects'...");
  for (const p of PROJECTS_SEED) {
    const docRef = db.collection("projects").doc();
    await docRef.set({ ...p, id: docRef.id, ideaId: "" });
    console.log(`   + ${p.title}`);
  }
}

async function seedRecognitions(uids) {
  console.log("\n🏆 Populando coleção 'recognition'...");
  const op = uids["operador@innovagab.com"];
  const ge = uids["gestor@innovagab.com"];
  const li = uids["lideranca@innovagab.com"];

  const recognitions = [
    {
      userId: op, userName: "Carlos Operador",
      badge: "pioneiro",
      note: "Primeira ideia submetida na plataforma — iniciou a cultura de inovação!",
      grantedBy: "Roberto Liderança", grantedAt: ago(80),
    },
    {
      userId: op, userName: "Carlos Operador",
      badge: "destaque_mes",
      note: "Melhor desempenho em inovação operacional no mês de Março. Parabéns!",
      grantedBy: "Roberto Liderança", grantedAt: ago(45),
    },
    {
      userId: op, userName: "Carlos Operador",
      badge: "agente_mudanca",
      note: "Liderou a transformação do processo de onboarding com resultados excepcionais.",
      grantedBy: "Roberto Liderança", grantedAt: ago(15),
    },
    {
      userId: ge, userName: "Ana Gestora",
      badge: "pioneiro",
      note: "Primeira gestora a submeter ideias estratégicas na plataforma.",
      grantedBy: "Roberto Liderança", grantedAt: ago(75),
    },
    {
      userId: ge, userName: "Ana Gestora",
      badge: "lider_inovacao",
      note: "Consistência e excelência em contribuições estratégicas durante todo o semestre.",
      grantedBy: "Roberto Liderança", grantedAt: ago(10),
    },
    {
      userId: ge, userName: "Ana Gestora",
      badge: "destaque_mes",
      note: "Maior contribuidora de ideias aprovadas no mês de Abril.",
      grantedBy: "Roberto Liderança", grantedAt: ago(5),
    },
    {
      userId: li, userName: "Roberto Liderança",
      badge: "agente_mudanca",
      note: "Criou a visão estratégica que guia toda a transformação digital da empresa.",
      grantedBy: "Ana Gestora", grantedAt: ago(30),
    },
  ];

  for (const rec of recognitions) {
    const docRef = db.collection("recognition").doc();
    await docRef.set({ ...rec, id: docRef.id });
    console.log(`   + ${rec.badge.padEnd(15)} → ${rec.userName}`);
  }
}

// ─── Main ─────────────────────────────────────────────────────────────────────

async function main() {
  console.log("🔥 InnovaGAB — Firebase Seed");
  console.log("================================");

  console.log("\n🗑  Limpando dados existentes...");
  await clearCollection("ideas");
  await clearCollection("strategies");
  await clearCollection("projects");
  await clearCollection("recognition");

  const uids = await seedUsers();
  await seedIdeas(uids);
  await seedStrategies();
  await seedProjects();
  await seedRecognitions(uids);

  console.log("\n✅ Seed completo! Firebase pronto para demo.");
  console.log("\n   Ranking estimado do leaderboard:");
  console.log("   🥇 Carlos Operador  — ~312 pts (10 ideias + 3 badges manuais)");
  console.log("   🥈 Ana Gestora      — ~172 pts (7 ideias + 3 badges manuais)");
  console.log("   🥉 Roberto Liderança —  ~80 pts (4 ideias + 1 badge manual)");
  console.log("\n   Credenciais:");
  console.log("   👷 operador@innovagab.com  / senha123");
  console.log("   📋 gestor@innovagab.com    / senha123");
  console.log("   👑 lideranca@innovagab.com / senha123");
  process.exit(0);
}

main().catch((err) => {
  console.error("\n❌ Erro no seed:", err.message);
  process.exit(1);
});
