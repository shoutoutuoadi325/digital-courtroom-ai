<template>
  <div>
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">AI Legal Assistant Platform</h1>
          <p class="hero-subtitle">
            Powered by Hybrid Reasoning Engine · Multi-Agent Analysis · Human-in-the-Loop Review
          </p>
          <div class="hero-actions">
            <router-link to="/query" class="btn btn-accent btn-lg">
              🔍 Submit Legal Query
            </router-link>
            <router-link to="/trial" class="btn btn-outline-white btn-lg">
              ⚖️ Mock Trial Adventure
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <div class="container">
      <div class="features-grid">
        <div class="feature-card" v-for="feature in features" :key="feature.title">
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.desc }}</p>
        </div>
      </div>

      <div class="architecture-section card">
        <h2>System Architecture</h2>
        <div class="arch-flow">
          <div class="arch-node input">
            <div class="arch-icon">📥</div>
            <div class="arch-label">Multimodal Input</div>
            <div class="arch-sub">Text · Voice · OCR · Documents</div>
          </div>
          <div class="arch-arrow">→</div>
          <div class="arch-node routing">
            <div class="arch-icon">🔀</div>
            <div class="arch-label">Hybrid Router</div>
            <div class="arch-sub">Structured vs Complex</div>
          </div>
          <div class="arch-branches">
            <div class="arch-branch">
              <div class="arch-arrow-down">↓</div>
              <div class="arch-node rule">
                <div class="arch-icon">📋</div>
                <div class="arch-label">Rule Engine</div>
                <div class="arch-sub">Deterministic</div>
              </div>
            </div>
            <div class="arch-branch">
              <div class="arch-arrow-down">↓</div>
              <div class="arch-node llm">
                <div class="arch-icon">🤖</div>
                <div class="arch-label">Multi-Agent LLM</div>
                <div class="arch-sub">Debate & Verify</div>
              </div>
            </div>
          </div>
          <div class="arch-arrow">→</div>
          <div class="arch-node hitl">
            <div class="arch-icon">👨‍⚖️</div>
            <div class="arch-label">HITL Review</div>
            <div class="arch-sub">Expert Validation</div>
          </div>
          <div class="arch-arrow">→</div>
          <div class="arch-node output">
            <div class="arch-icon">🗺️</div>
            <div class="arch-label">Visual Output</div>
            <div class="arch-sub">Mind Maps & Diagrams</div>
          </div>
        </div>
      </div>

      <div class="cta-grid">
        <router-link to="/query" class="cta-card">
          <span class="cta-icon">⚖️</span>
          <h3>Legal Query</h3>
          <p>Submit legal questions via text, voice, or document upload. Get AI-powered analysis with visual mind maps.</p>
          <span class="cta-btn">Start Now →</span>
        </router-link>
        <router-link to="/review" class="cta-card">
          <span class="cta-icon">🔬</span>
          <h3>HITL Review Panel</h3>
          <p>Legal professionals review and correct AI analysis to build a continuously improving legal corpus.</p>
          <span class="cta-btn">Review →</span>
        </router-link>
        <router-link to="/trial" class="cta-card">
          <span class="cta-icon">🎭</span>
          <h3>Mock Trial Adventure</h3>
          <p>Select your courtroom role and interact with AI agents in immersive simulated court proceedings.</p>
          <span class="cta-btn">Play →</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
const features = [
  { icon: '🔀', title: 'Hybrid Reasoning Engine', desc: 'Routes structured queries to deterministic rule engine; complex queries to multi-agent LLM for deep semantic analysis.' },
  { icon: '🤖', title: 'Multi-Agent Framework', desc: 'Multiple specialized AI agents (Legal Analyst, Devil\'s Advocate, Precedent Researcher) cross-debate to self-correct and improve accuracy.' },
  { icon: '👨‍⚖️', title: 'Human-in-the-Loop', desc: 'Legal professionals review and correct AI outputs. Corrections feed back into the system to continuously expand the legal corpus.' },
  { icon: '��️', title: 'Multimodal Input', desc: 'Supports text, voice (optimized for regional dialects), OCR for images, and long-text document ingestion.' },
  { icon: '🗺️', title: 'Visual Mind Maps', desc: 'AI parses complex legal relationships and renders them as interactive mind maps for easy comprehension.' },
  { icon: '⚖️', title: 'Mock Trial Adventure', desc: 'Gamified courtroom simulation where users choose roles and interact with AI agents in real-time adversarial proceedings.' },
]
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #1a2b4a 0%, #2d4a7a 50%, #1a3a5c 100%);
  color: white;
  padding: 80px 0;
  margin-bottom: 40px;
}
.hero-content { max-width: 700px; }
.hero-title { font-size: 2.8rem; font-weight: 800; margin-bottom: 16px; line-height: 1.2; }
.hero-subtitle { font-size: 1.1rem; opacity: 0.85; margin-bottom: 32px; line-height: 1.6; }
.hero-actions { display: flex; gap: 16px; flex-wrap: wrap; }
.btn-lg { padding: 14px 28px; font-size: 16px; }
.btn-outline-white { background: transparent; border: 2px solid rgba(255,255,255,0.7); color: white; }
.btn-outline-white:hover { background: rgba(255,255,255,0.1); }

.features-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 32px; }
.feature-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: var(--shadow);
  transition: transform 0.2s;
}
.feature-card:hover { transform: translateY(-3px); }
.feature-icon { font-size: 2rem; margin-bottom: 12px; }
.feature-card h3 { font-size: 1rem; margin-bottom: 8px; color: var(--primary); }
.feature-card p { font-size: 0.875rem; color: var(--text-muted); line-height: 1.5; }

.architecture-section h2 { font-size: 1.4rem; margin-bottom: 24px; color: var(--primary); }
.arch-flow {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow-x: auto;
  padding: 8px 0;
  flex-wrap: wrap;
}
.arch-node {
  background: #f0f4f8;
  border-radius: 10px;
  padding: 14px 18px;
  text-align: center;
  min-width: 110px;
  border: 2px solid transparent;
}
.arch-node.input { border-color: #bbdefb; background: #e3f2fd; }
.arch-node.routing { border-color: #c8e6c9; background: #e8f5e9; }
.arch-node.rule { border-color: #fff9c4; background: #fffde7; }
.arch-node.llm { border-color: #e1bee7; background: #f3e5f5; }
.arch-node.hitl { border-color: #ffccbc; background: #fbe9e7; }
.arch-node.output { border-color: #b2dfdb; background: #e0f2f1; }
.arch-icon { font-size: 1.5rem; margin-bottom: 4px; }
.arch-label { font-weight: 700; font-size: 0.8rem; }
.arch-sub { font-size: 0.7rem; color: var(--text-muted); margin-top: 2px; }
.arch-arrow { font-size: 1.5rem; color: var(--text-muted); }
.arch-branches { display: flex; gap: 12px; }
.arch-branch { display: flex; flex-direction: column; align-items: center; }
.arch-arrow-down { color: var(--text-muted); font-size: 1.2rem; }

.cta-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-top: 32px; }
.cta-card {
  background: white;
  border-radius: 12px;
  padding: 28px 24px;
  box-shadow: var(--shadow);
  border: 2px solid transparent;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.cta-card:hover { border-color: var(--accent); transform: translateY(-3px); }
.cta-icon { font-size: 2.5rem; }
.cta-card h3 { font-size: 1.1rem; color: var(--primary); margin-top: 4px; }
.cta-card p { font-size: 0.875rem; color: var(--text-muted); line-height: 1.5; flex: 1; }
.cta-btn { color: var(--accent); font-weight: 700; font-size: 0.9rem; }

@media (max-width: 768px) {
  .features-grid, .cta-grid { grid-template-columns: 1fr; }
  .hero-title { font-size: 1.8rem; }
  .arch-flow { justify-content: center; }
}
</style>
