<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>🎭 Mock Trial Adventure</h1>
        <p>Choose your role and engage in immersive AI-powered courtroom simulations. Multi-agent AI opponents argue against you in real time.</p>
      </div>
    </div>
    <div class="container">
      <!-- Create New Trial -->
      <div class="card create-trial-card">
        <h2>⚖️ Start New Trial</h2>
        <div class="trial-form">
          <div class="form-group">
            <label class="form-label">Case Title</label>
            <input v-model="form.title" class="form-control" placeholder="e.g. Smith v. Jones — Contract Dispute" />
          </div>
          <div class="form-group">
            <label class="form-label">Case Description</label>
            <textarea v-model="form.caseDescription" class="form-control" rows="3"
              placeholder="Describe the legal case, parties involved, and key facts..."></textarea>
          </div>
          <div class="form-group">
            <label class="form-label">Your Role</label>
            <div class="role-grid">
              <div v-for="role in roles" :key="role.id"
                class="role-card" :class="{selected: form.userRole === role.id}"
                @click="form.userRole = role.id">
                <div class="role-icon">{{ role.icon }}</div>
                <div class="role-name">{{ role.name }}</div>
                <div class="role-desc">{{ role.desc }}</div>
              </div>
            </div>
          </div>
          <div v-if="createError" class="alert alert-error">{{ createError }}</div>
          <button class="btn btn-accent btn-lg" @click="createTrial" :disabled="creating">
            {{ creating ? '⏳ Creating...' : '🎬 Begin Trial' }}
          </button>
        </div>
      </div>

      <!-- Existing Trials -->
      <div class="card">
        <h2>📚 My Trials</h2>
        <div v-if="loadingTrials" class="loading"><div class="spinner"></div></div>
        <div v-else-if="trials.length === 0" class="empty-state">
          <div class="icon">⚖️</div>
          <h3>No trials yet</h3>
          <p>Start your first mock trial above</p>
        </div>
        <div v-else class="trials-list">
          <div v-for="trial in trials" :key="trial.id" class="trial-item"
            @click="$router.push('/trial/' + trial.id)">
            <div class="trial-meta">
              <span class="trial-role">{{ getRoleIcon(trial.userRole) }} {{ trial.userRole }}</span>
              <span class="badge" :class="'badge-' + (trial.status || '').toLowerCase()">{{ trial.status }}</span>
              <span class="trial-stage">Stage: {{ trial.currentStage }}</span>
            </div>
            <h4 class="trial-title">{{ trial.title }}</h4>
            <p class="trial-desc">{{ truncate(trial.caseDescription, 100) }}</p>
            <span class="trial-time">{{ formatDate(trial.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const trials = ref([])
const loadingTrials = ref(true)
const creating = ref(false)
const createError = ref('')
const form = ref({ title: '', caseDescription: '', userRole: 'LAWYER' })

const roles = [
  { id: 'PLAINTIFF', icon: '🧑', name: 'Plaintiff', desc: 'Bring the claim and prove your case' },
  { id: 'DEFENDANT', icon: '🛡️', name: 'Defendant', desc: 'Defend against allegations' },
  { id: 'LAWYER', icon: '👨‍💼', name: 'Lawyer', desc: 'Represent a client with legal arguments' },
  { id: 'JUDGE', icon: '👩‍⚖️', name: 'Judge', desc: 'Preside and deliver verdicts' },
]

onMounted(async () => {
  await loadTrials()
})

async function loadTrials() {
  loadingTrials.value = true
  try {
    const res = await axios.get('/api/trials')
    trials.value = res.data
  } catch (e) { console.error(e) }
  finally { loadingTrials.value = false }
}

async function createTrial() {
  createError.value = ''
  if (!form.value.title) { createError.value = 'Please enter a case title.'; return }
  if (!form.value.caseDescription) { createError.value = 'Please describe the case.'; return }
  if (!form.value.userRole) { createError.value = 'Please select your role.'; return }

  creating.value = true
  try {
    const res = await axios.post('/api/trials', form.value)
    router.push('/trial/' + res.data.id)
  } catch (e) {
    createError.value = 'Failed to create trial. Please ensure the backend is running.'
  } finally {
    creating.value = false
  }
}

function getRoleIcon(role) {
  return { PLAINTIFF: '🧑', DEFENDANT: '🛡️', LAWYER: '👨‍💼', JUDGE: '👩‍⚖️' }[role] || '👤'
}

function truncate(text, max) {
  if (!text) return ''
  return text.length > max ? text.substring(0, max) + '...' : text
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.create-trial-card h2 { font-size: 1.3rem; color: var(--primary); margin-bottom: 20px; }
.trial-form { max-width: 700px; }
.btn-lg { padding: 14px 28px; font-size: 16px; margin-top: 8px; }

.role-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.role-card {
  border: 2px solid var(--border);
  border-radius: 10px;
  padding: 16px 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}
.role-card:hover { border-color: var(--accent); }
.role-card.selected { border-color: var(--primary); background: #e8f0fe; }
.role-icon { font-size: 2rem; margin-bottom: 6px; }
.role-name { font-weight: 700; font-size: 14px; color: var(--primary); margin-bottom: 4px; }
.role-desc { font-size: 11px; color: var(--text-muted); line-height: 1.3; }

.trials-list { display: flex; flex-direction: column; gap: 12px; }
.trial-item {
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.trial-item:hover { border-color: var(--primary); background: #f8f9ff; }
.trial-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; flex-wrap: wrap; }
.trial-role { font-size: 13px; font-weight: 600; color: var(--primary); }
.trial-stage { font-size: 12px; color: var(--text-muted); }
.trial-title { font-size: 15px; font-weight: 700; margin-bottom: 4px; }
.trial-desc { font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }
.trial-time { font-size: 12px; color: var(--text-muted); }

@media (max-width: 768px) {
  .role-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
