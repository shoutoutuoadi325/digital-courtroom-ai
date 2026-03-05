<template>
  <div class="trial-room">
    <div class="trial-header">
      <div class="container">
        <div class="trial-nav">
          <router-link to="/trial" class="back-link">← All Trials</router-link>
          <div class="trial-info">
            <h2>{{ trial?.title }}</h2>
            <div class="trial-meta-bar">
              <span class="stage-badge" :class="'stage-' + (trial?.currentStage || '').toLowerCase()">
                {{ stageIcon(trial?.currentStage) }} {{ trial?.currentStage }}
              </span>
              <span class="role-badge">Your Role: {{ roleIcon(trial?.userRole) }} {{ trial?.userRole }}</span>
              <span class="status-badge badge" :class="'badge-' + (trial?.status || '').toLowerCase()">{{ trial?.status }}</span>
            </div>
          </div>
          <button class="btn btn-outline btn-sm" @click="advanceStage" :disabled="trial?.status === 'COMPLETED'">
            Next Stage →
          </button>
        </div>
      </div>
    </div>

    <div class="container trial-body">
      <div class="courtroom-grid">
        <!-- Agents Panel -->
        <div class="agents-panel">
          <h3>Courtroom Participants</h3>
          <div v-for="agent in courtAgents" :key="agent.role" class="agent-card" :class="{user: agent.isUser}">
            <div class="agent-icon">{{ agent.icon }}</div>
            <div class="agent-info">
              <div class="agent-name">{{ agent.name }}</div>
              <div class="agent-role">{{ agent.isUser ? '(You)' : 'AI Agent' }}</div>
            </div>
            <div v-if="agent.isUser" class="user-badge">YOU</div>
          </div>

          <div class="stage-progress">
            <h4>Trial Progress</h4>
            <div v-for="stage in trialStages" :key="stage" class="stage-item"
              :class="{active: trial?.currentStage === stage, completed: isCompleted(stage)}">
              <div class="stage-dot"></div>
              <span>{{ stage }}</span>
            </div>
          </div>
        </div>

        <!-- Chat/Messages Area -->
        <div class="messages-panel">
          <div class="messages-area" ref="messagesEl">
            <div v-if="loadingMessages" class="loading"><div class="spinner"></div></div>
            <div v-else>
              <div v-for="msg in messages" :key="msg.id"
                class="message" :class="getMessageClass(msg.senderRole)">
                <div class="msg-header">
                  <span class="msg-sender">{{ getSenderDisplay(msg.senderRole) }}</span>
                  <span class="msg-type badge" :class="'badge-' + (msg.messageType || '').toLowerCase()">
                    {{ msg.messageType }}
                  </span>
                  <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
                </div>
                <div class="msg-content">{{ msg.content }}</div>
              </div>
              <div v-if="messages.length === 0" class="empty-messages">
                <p>The trial is about to begin. The court awaits your opening statement...</p>
              </div>
            </div>
          </div>

          <!-- Input Area -->
          <div class="message-input-area" v-if="trial?.status !== 'COMPLETED'">
            <div class="input-row">
              <select v-model="messageType" class="type-select">
                <option value="STATEMENT">📢 Statement</option>
                <option value="QUESTION">❓ Question</option>
                <option value="OBJECTION">🚫 Objection</option>
                <option value="EVIDENCE">📋 Evidence</option>
                <option value="RULING" v-if="trial?.userRole === 'JUDGE'">⚖️ Ruling</option>
              </select>
              <input v-model="newMessage" class="message-input" @keyup.enter="sendMessage"
                :placeholder="'Speak as ' + (trial?.userRole || 'yourself') + '...'" />
              <button class="send-btn" @click="sendMessage" :disabled="sending">
                {{ sending ? '⏳' : '▶ Send' }}
              </button>
            </div>
          </div>
          <div v-else class="verdict-banner">
            <h3>⚖️ Trial Concluded</h3>
            <p>The verdict has been delivered. Review the transcript above.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const trial = ref(null)
const messages = ref([])
const loadingMessages = ref(true)
const newMessage = ref('')
const messageType = ref('STATEMENT')
const sending = ref(false)
const messagesEl = ref(null)

const trialStages = ['OPENING', 'EVIDENCE', 'CROSS_EXAMINATION', 'CLOSING', 'VERDICT']

const courtAgents = computed(() => {
  if (!trial.value) return []
  const userRole = trial.value.userRole
  return [
    { role: 'JUDGE', icon: '👩‍⚖️', name: 'Judge', isUser: userRole === 'JUDGE' },
    { role: 'PLAINTIFF', icon: '🧑', name: 'Plaintiff', isUser: userRole === 'PLAINTIFF' },
    { role: 'DEFENDANT', icon: '🛡️', name: 'Defendant', isUser: userRole === 'DEFENDANT' },
    { role: 'LAWYER', icon: '👨‍💼', name: 'Lawyer/Counsel', isUser: userRole === 'LAWYER' },
  ]
})

onMounted(async () => {
  await loadTrial()
})

async function loadTrial() {
  loadingMessages.value = true
  try {
    const res = await axios.get('/api/trials/' + route.params.id)
    trial.value = res.data.trial
    messages.value = res.data.messages || []
    scrollToBottom()
  } catch (e) { console.error(e) }
  finally { loadingMessages.value = false }
}

async function sendMessage() {
  if (!newMessage.value.trim() || sending.value) return
  sending.value = true
  const content = newMessage.value
  newMessage.value = ''
  try {
    const res = await axios.post('/api/trials/' + route.params.id + '/messages', {
      content,
      messageType: messageType.value
    })
    messages.value.push(res.data.userMessage)
    if (res.data.agentResponses) {
      messages.value.push(...res.data.agentResponses)
    }
    trial.value.currentStage = res.data.currentStage
    trial.value.status = res.data.trialStatus
    scrollToBottom()
  } catch (e) { console.error(e) }
  finally { sending.value = false }
}

async function advanceStage() {
  try {
    const res = await axios.put('/api/trials/' + route.params.id + '/stage')
    trial.value = res.data
    await loadTrial()
  } catch (e) { console.error(e) }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesEl.value) {
      messagesEl.value.scrollTop = messagesEl.value.scrollHeight
    }
  })
}

function getMessageClass(senderRole) {
  if (senderRole === 'USER') return 'msg-user'
  if (senderRole === 'JUDGE_AGENT') return 'msg-judge'
  if (senderRole === 'PLAINTIFF_AGENT') return 'msg-plaintiff'
  if (senderRole === 'DEFENDANT_AGENT') return 'msg-defendant'
  if (senderRole === 'LAWYER_AGENT') return 'msg-lawyer'
  return ''
}

function getSenderDisplay(role) {
  const map = {
    USER: '👤 You',
    JUDGE_AGENT: '👩‍⚖️ Judge',
    PLAINTIFF_AGENT: '🧑 Plaintiff',
    DEFENDANT_AGENT: '🛡️ Defendant',
    LAWYER_AGENT: '👨‍💼 Counsel'
  }
  return map[role] || role
}

function stageIcon(stage) {
  const map = { OPENING: '🎙️', EVIDENCE: '📋', CROSS_EXAMINATION: '❓', CLOSING: '📝', VERDICT: '⚖️' }
  return map[stage] || '▪'
}

function roleIcon(role) {
  return { PLAINTIFF: '🧑', DEFENDANT: '🛡️', LAWYER: '👨‍💼', JUDGE: '👩‍⚖️' }[role] || '👤'
}

function isCompleted(stage) {
  const idx = trialStages.indexOf(stage)
  const current = trialStages.indexOf(trial.value?.currentStage)
  return idx < current
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.trial-room { background: #0d1b2a; min-height: 100vh; color: white; }
.trial-header {
  background: linear-gradient(90deg, #1a2b4a, #2d4a7a);
  padding: 16px 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.trial-nav { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.back-link { color: rgba(255,255,255,0.7); font-size: 14px; white-space: nowrap; }
.back-link:hover { color: white; }
.trial-info { flex: 1; }
.trial-info h2 { font-size: 1.1rem; margin-bottom: 6px; }
.trial-meta-bar { display: flex; gap: 8px; flex-wrap: wrap; }
.stage-badge { padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.stage-opening { background: #1565c0; }
.stage-evidence { background: #2e7d32; }
.stage-cross_examination { background: #e65100; }
.stage-closing { background: #6a1b9a; }
.stage-verdict { background: #c62828; }
.role-badge { font-size: 12px; color: rgba(255,255,255,0.8); padding: 4px 10px; background: rgba(255,255,255,0.1); border-radius: 20px; }
.status-badge { font-size: 12px; }

.trial-body { padding-top: 24px; padding-bottom: 24px; }
.courtroom-grid { display: grid; grid-template-columns: 240px 1fr; gap: 20px; height: calc(100vh - 180px); }

.agents-panel {
  background: rgba(255,255,255,0.05);
  border-radius: 12px;
  padding: 16px;
  overflow-y: auto;
}
.agents-panel h3 { font-size: 0.85rem; text-transform: uppercase; letter-spacing: 1px; color: rgba(255,255,255,0.5); margin-bottom: 12px; }
.agent-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 8px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  position: relative;
}
.agent-card.user { background: rgba(201,168,76,0.15); border-color: rgba(201,168,76,0.4); }
.agent-icon { font-size: 1.4rem; }
.agent-name { font-size: 13px; font-weight: 600; }
.agent-role { font-size: 11px; color: rgba(255,255,255,0.5); }
.user-badge {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: var(--accent);
  color: var(--primary);
  font-size: 10px;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 4px;
}

.stage-progress { margin-top: 20px; }
.stage-progress h4 { font-size: 0.75rem; text-transform: uppercase; color: rgba(255,255,255,0.5); margin-bottom: 10px; }
.stage-item { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; font-size: 12px; color: rgba(255,255,255,0.4); }
.stage-item.active { color: var(--accent-light); font-weight: 700; }
.stage-item.completed { color: rgba(255,255,255,0.6); }
.stage-dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(255,255,255,0.2); flex-shrink: 0; }
.stage-item.active .stage-dot { background: var(--accent); }
.stage-item.completed .stage-dot { background: rgba(255,255,255,0.5); }

.messages-panel {
  background: rgba(255,255,255,0.03);
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.messages-area { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 12px; }
.empty-messages { text-align: center; padding: 40px; color: rgba(255,255,255,0.4); font-style: italic; }

.message {
  background: rgba(255,255,255,0.06);
  border-radius: 10px;
  padding: 12px 14px;
  border-left: 3px solid transparent;
}
.msg-user { border-left-color: var(--accent); background: rgba(201,168,76,0.08); }
.msg-judge { border-left-color: #ef5350; background: rgba(239,83,80,0.06); }
.msg-plaintiff { border-left-color: #42a5f5; background: rgba(66,165,245,0.06); }
.msg-defendant { border-left-color: #66bb6a; background: rgba(102,187,106,0.06); }
.msg-lawyer { border-left-color: #ab47bc; background: rgba(171,71,188,0.06); }

.msg-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; flex-wrap: wrap; }
.msg-sender { font-weight: 700; font-size: 13px; }
.msg-type { font-size: 11px; padding: 2px 8px; }
.msg-time { font-size: 11px; color: rgba(255,255,255,0.4); margin-left: auto; }
.msg-content { font-size: 14px; line-height: 1.6; color: rgba(255,255,255,0.85); }

.message-input-area {
  border-top: 1px solid rgba(255,255,255,0.1);
  padding: 12px;
}
.input-row { display: flex; gap: 8px; align-items: center; }
.type-select {
  padding: 10px 12px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 8px;
  color: white;
  font-size: 13px;
  min-width: 150px;
}
.type-select option { background: #1a2b4a; }
.message-input {
  flex: 1;
  padding: 10px 14px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 8px;
  color: white;
  font-size: 14px;
}
.message-input::placeholder { color: rgba(255,255,255,0.35); }
.message-input:focus { outline: none; border-color: var(--accent); }
.send-btn {
  padding: 10px 18px;
  background: var(--accent);
  color: var(--primary);
  border: none;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}
.send-btn:hover { opacity: 0.9; }
.send-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.verdict-banner {
  border-top: 1px solid rgba(255,255,255,0.1);
  padding: 20px;
  text-align: center;
  background: rgba(201,168,76,0.1);
}
.verdict-banner h3 { color: var(--accent-light); margin-bottom: 4px; }
.verdict-banner p { font-size: 13px; color: rgba(255,255,255,0.6); }

@media (max-width: 768px) {
  .courtroom-grid { grid-template-columns: 1fr; height: auto; }
  .agents-panel { display: none; }
  .messages-panel { height: 70vh; }
}
</style>
