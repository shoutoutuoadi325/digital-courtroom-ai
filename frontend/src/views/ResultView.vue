<template>
  <div>
    <div class="page-header">
      <div class="container">
        <router-link to="/query" class="back-link">← Back to Queries</router-link>
        <h1>📊 Legal Analysis Result</h1>
        <p>AI-powered legal analysis with interactive mind map visualization</p>
      </div>
    </div>
    <div class="container">
      <div v-if="loading" class="loading"><div class="spinner"></div> Loading analysis...</div>
      <div v-else-if="query">
        <div class="result-header card">
          <div class="result-meta">
            <span class="badge" :class="'badge-' + (query.queryType || '').toLowerCase().replace('_','-')">
              {{ query.queryType === 'RULE_BASED' ? '📋 Rule-Based Engine' : '🤖 Multi-Agent LLM' }}
            </span>
            <span class="badge" :class="'badge-' + (query.status || '').toLowerCase()">{{ query.status }}</span>
            <span class="badge" style="background:#f0f4f8;color:#555">
              {{ query.inputType }}
            </span>
            <span class="time-badge">{{ formatDate(query.createdAt) }}</span>
          </div>
          <h2 class="query-display">{{ query.queryText }}</h2>
        </div>

        <div class="result-grid">
          <!-- Analysis Response -->
          <div class="card response-card">
            <h3>📝 Legal Analysis</h3>
            <div class="response-content" v-html="renderMarkdown(query.response)"></div>
          </div>

          <!-- Mind Map -->
          <div class="card mindmap-card">
            <h3>🗺️ Visual Mind Map</h3>
            <div v-if="query.mindMapData" class="mindmap-container">
              <MindMap :data="query.mindMapData" />
            </div>
            <div v-else class="empty-state">
              <div class="icon">🗺️</div>
              <p>Mind map not available</p>
            </div>
          </div>
        </div>

        <div class="card hitl-notice" v-if="query.status === 'UNDER_REVIEW'">
          <div class="notice-icon">👨‍⚖️</div>
          <div>
            <h4>Under Expert Review</h4>
            <p>This analysis has been flagged for Human-in-the-Loop (HITL) review by a legal professional. The response may be updated after expert validation.</p>
          </div>
          <router-link to="/review" class="btn btn-outline btn-sm">View Review Panel →</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import MindMap from '../components/MindMap.vue'

const route = useRoute()
const query = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await axios.get('/api/queries/' + route.params.id)
    query.value = res.data
  } catch (e) {
    console.error('Failed to load query', e)
  } finally {
    loading.value = false
  }
})

function renderMarkdown(text) {
  if (!text) return ''
  return text
    .replace(/^### (.+)$/gm, '<h4>$1</h4>')
    .replace(/^## (.+)$/gm, '<h3>$1</h3>')
    .replace(/^# (.+)$/gm, '<h2>$1</h2>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
    .replace(/\n\n/g, '</p><p>')
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>

<style scoped>
.back-link { color: rgba(255,255,255,0.8); font-size: 14px; display: inline-block; margin-bottom: 12px; }
.back-link:hover { color: white; }
.result-header { margin-bottom: 20px; }
.result-meta { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; margin-bottom: 12px; }
.time-badge { font-size: 12px; color: var(--text-muted); }
.query-display { font-size: 1.2rem; color: var(--primary); line-height: 1.4; }

.result-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }

.response-card h3, .mindmap-card h3 { font-size: 1rem; color: var(--primary); margin-bottom: 16px; padding-bottom: 10px; border-bottom: 1px solid var(--border); }
.response-content { font-size: 14px; line-height: 1.7; color: var(--text); }
.response-content :deep(h2), .response-content :deep(h3), .response-content :deep(h4) {
  color: var(--primary);
  margin: 16px 0 8px;
  font-size: 1rem;
}
.response-content :deep(p) { margin-bottom: 10px; }
.response-content :deep(strong) { color: var(--primary); }
.response-content :deep(ol), .response-content :deep(ul) { padding-left: 20px; margin-bottom: 10px; }
.response-content :deep(li) { margin-bottom: 4px; }
.response-content :deep(em) { color: var(--text-muted); }

.mindmap-container { min-height: 300px; }

.hitl-notice {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff8e1;
  border: 1px solid #ffe082;
}
.notice-icon { font-size: 2rem; flex-shrink: 0; }
.hitl-notice h4 { font-size: 1rem; color: var(--warning); margin-bottom: 4px; }
.hitl-notice p { font-size: 13px; color: var(--text-muted); }

@media (max-width: 768px) {
  .result-grid { grid-template-columns: 1fr; }
}
</style>
