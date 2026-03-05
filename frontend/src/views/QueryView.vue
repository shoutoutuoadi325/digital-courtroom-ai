<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>⚖️ Legal Query Engine</h1>
        <p>Submit your legal question via text, voice, or document upload. Our hybrid AI engine will analyze and generate a visual mind map.</p>
      </div>
    </div>
    <div class="container">
      <div class="grid-2">
        <div>
          <div class="card">
            <h2 class="section-title">Submit Your Query</h2>

            <div class="input-tabs">
              <button v-for="tab in tabs" :key="tab.id"
                class="tab-btn" :class="{active: activeTab === tab.id}"
                @click="activeTab = tab.id">
                {{ tab.icon }} {{ tab.label }}
              </button>
            </div>

            <!-- Text Input -->
            <div v-if="activeTab === 'text'" class="tab-content">
              <div class="form-group">
                <label class="form-label">Your Legal Question</label>
                <textarea v-model="queryText" class="form-control"
                  rows="5"
                  placeholder="e.g. 'What are my rights in a contract breach situation?' or 'How does divorce property division work?'"></textarea>
              </div>
              <div class="form-group">
                <label class="form-label">Category (optional)</label>
                <select v-model="category" class="form-control">
                  <option value="">Auto-detect</option>
                  <option value="CONTRACT">Contract Law</option>
                  <option value="FAMILY">Family Law</option>
                  <option value="CRIMINAL">Criminal Law</option>
                  <option value="LABOR">Labor Law</option>
                  <option value="TORT">Tort Law</option>
                  <option value="PROPERTY">Property Law</option>
                </select>
              </div>
            </div>

            <!-- Voice Input -->
            <div v-if="activeTab === 'voice'" class="tab-content">
              <div class="form-group">
                <label class="form-label">Recognition Language</label>
                <select v-model="voiceLang" class="form-control">
                  <option value="">Auto-detect (Browser Default)</option>
                  <option value="zh-CN">Chinese Mandarin (zh-CN)</option>
                  <option value="zh-TW">Chinese Traditional (zh-TW)</option>
                  <option value="yue-Hant-HK">Cantonese (yue-Hant-HK)</option>
                  <option value="en-US">English US (en-US)</option>
                  <option value="en-GB">English UK (en-GB)</option>
                  <option value="ja-JP">Japanese (ja-JP)</option>
                  <option value="ko-KR">Korean (ko-KR)</option>
                  <option value="fr-FR">French (fr-FR)</option>
                  <option value="de-DE">German (de-DE)</option>
                  <option value="es-ES">Spanish (es-ES)</option>
                  <option value="ar-SA">Arabic (ar-SA)</option>
                </select>
              </div>
              <div class="voice-area">
                <button class="voice-btn" :class="{recording: isRecording}"
                  @click="toggleRecording">
                  {{ isRecording ? '⏹️ Stop Recording' : '🎙️ Start Recording' }}
                </button>
                <p class="voice-hint">{{ isRecording ? 'Recording... Speak your legal question clearly.' : 'Click to start voice recording. Supports multiple languages and regional dialects.' }}</p>
                <div v-if="voiceTranscript" class="voice-transcript">
                  <strong>Transcript:</strong> {{ voiceTranscript }}
                </div>
                <div v-if="!supportsVoice" class="alert alert-info">
                  Voice input requires microphone access. Alternatively, upload an audio file below.
                </div>
              </div>
              <div class="form-group mt-16">
                <label class="form-label">Or upload audio file</label>
                <input type="file" accept="audio/*" @change="handleVoiceFile" class="form-control" />
              </div>
            </div>

            <!-- OCR Input -->
            <div v-if="activeTab === 'ocr'" class="tab-content">
              <div class="upload-area" @dragover.prevent @drop.prevent="handleDrop($event, 'OCR')"
                @click="$refs.ocrInput.click()">
                <div class="upload-icon">🖼️</div>
                <p>Drag & drop an image or click to browse</p>
                <p class="upload-hint">Supports PNG, JPG, TIFF — OCR will extract legal text</p>
                <input ref="ocrInput" type="file" accept="image/*" @change="handleFileSelect($event, 'OCR')" hidden />
              </div>
              <div v-if="selectedFile && activeTab === 'ocr'" class="selected-file">
                📎 {{ selectedFile.name }}
              </div>
            </div>

            <!-- Document Input -->
            <div v-if="activeTab === 'document'" class="tab-content">
              <div class="upload-area" @dragover.prevent @drop.prevent="handleDrop($event, 'DOCUMENT')"
                @click="$refs.docInput.click()">
                <div class="upload-icon">📄</div>
                <p>Drag & drop a document or click to browse</p>
                <p class="upload-hint">Supports PDF, TXT, DOCX — up to 50MB</p>
                <input ref="docInput" type="file" accept=".pdf,.txt,.docx,.doc" @change="handleFileSelect($event, 'DOCUMENT')" hidden />
              </div>
              <div v-if="selectedFile && activeTab === 'document'" class="selected-file">
                📎 {{ selectedFile.name }}
              </div>
            </div>

            <div v-if="error" class="alert alert-error">{{ error }}</div>

            <button class="btn btn-primary full-width" @click="submitQuery" :disabled="loading">
              <span v-if="loading"><span class="spinner-sm"></span> Analyzing...</span>
              <span v-else>🔍 Analyze Legal Query</span>
            </button>
          </div>

          <!-- Query Tips -->
          <div class="card tips-card">
            <h3>💡 Query Tips</h3>
            <ul class="tips-list">
              <li><strong>Rule-Based:</strong> Standard legal matters (contract breach, divorce, inheritance) → fast deterministic answers</li>
              <li><strong>LLM Analysis:</strong> Complex or ambiguous situations → multi-agent deep reasoning</li>
              <li><strong>Mind Maps:</strong> Every query generates an interactive visual diagram</li>
              <li><strong>HITL Review:</strong> Legal professionals review all AI responses for accuracy</li>
            </ul>
          </div>
        </div>

        <!-- Recent Queries -->
        <div>
          <div class="card">
            <h2 class="section-title">Recent Queries</h2>
            <div v-if="loadingHistory" class="loading"><div class="spinner"></div> Loading...</div>
            <div v-else-if="queries.length === 0" class="empty-state">
              <div class="icon">📋</div>
              <h3>No queries yet</h3>
              <p>Submit your first legal query</p>
            </div>
            <div v-else>
              <div v-for="q in queries" :key="q.id" class="query-item"
                @click="$router.push('/query/' + q.id)">
                <div class="query-header">
                  <span class="badge" :class="'badge-' + (q.queryType || 'pending').toLowerCase()">
                    {{ q.queryType === 'RULE_BASED' ? '📋 Rule-Based' : q.queryType === 'LLM_BASED' ? '🤖 LLM' : '⏳ Pending' }}
                  </span>
                  <span class="badge" :class="'badge-' + (q.status || '').toLowerCase()">
                    {{ q.status }}
                  </span>
                </div>
                <p class="query-text">{{ truncate(q.queryText, 100) }}</p>
                <span class="query-time">{{ formatDate(q.createdAt) }}</span>
              </div>
            </div>
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
const activeTab = ref('text')
const queryText = ref('')
const category = ref('')
const isRecording = ref(false)
const voiceTranscript = ref('')
const voiceLang = ref('')
const supportsVoice = ref(true)
const selectedFile = ref(null)
const loading = ref(false)
const loadingHistory = ref(false)
const error = ref('')
const queries = ref([])

let recognition = null

const tabs = [
  { id: 'text', icon: '✍️', label: 'Text' },
  { id: 'voice', icon: '🎙️', label: 'Voice' },
  { id: 'ocr', icon: '🖼️', label: 'OCR' },
  { id: 'document', icon: '📄', label: 'Document' },
]

onMounted(async () => {
  await loadQueries()
  if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
    supportsVoice.value = false
  }
})

async function loadQueries() {
  loadingHistory.value = true
  try {
    const res = await axios.get('/api/queries')
    queries.value = res.data
  } catch (e) {
    console.error('Failed to load queries', e)
  } finally {
    loadingHistory.value = false
  }
}

function toggleRecording() {
  if (isRecording.value) {
    stopRecording()
  } else {
    startRecording()
  }
}

function startRecording() {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  if (!SpeechRecognition) {
    error.value = 'Speech recognition not supported in this browser. Please use Chrome.'
    return
  }
  recognition = new SpeechRecognition()
  recognition.lang = voiceLang.value || navigator.language || 'zh-CN'
  recognition.interimResults = true
  recognition.continuous = true

  recognition.onresult = (event) => {
    let transcript = ''
    for (let i = 0; i < event.results.length; i++) {
      transcript += event.results[i][0].transcript
    }
    voiceTranscript.value = transcript
    queryText.value = transcript
  }

  recognition.onerror = (e) => {
    error.value = 'Speech recognition error: ' + e.error
    isRecording.value = false
  }

  recognition.start()
  isRecording.value = true
}

function stopRecording() {
  if (recognition) recognition.stop()
  isRecording.value = false
}

function handleVoiceFile(event) {
  selectedFile.value = event.target.files[0]
}

function handleFileSelect(event) {
  selectedFile.value = event.target.files[0]
}

function handleDrop(event) {
  selectedFile.value = event.dataTransfer.files[0]
}

async function submitQuery() {
  error.value = ''

  if (activeTab.value === 'text') {
    if (!queryText.value.trim()) {
      error.value = 'Please enter a legal query.'
      return
    }
    loading.value = true
    try {
      const res = await axios.post('/api/queries', {
        queryText: queryText.value,
        inputType: 'TEXT'
      })
      await loadQueries()
      router.push('/query/' + res.data.id)
    } catch (e) {
      error.value = 'Failed to process query. Please ensure the backend is running.'
    } finally {
      loading.value = false
    }
  } else if (activeTab.value === 'voice' && voiceTranscript.value && !selectedFile.value) {
    loading.value = true
    try {
      const res = await axios.post('/api/queries', {
        queryText: voiceTranscript.value,
        inputType: 'VOICE'
      })
      await loadQueries()
      router.push('/query/' + res.data.id)
    } catch (e) {
      error.value = 'Failed to process voice query.'
    } finally {
      loading.value = false
    }
  } else {
    if (!selectedFile.value) {
      error.value = 'Please select a file or record voice input.'
      return
    }
    loading.value = true
    try {
      const formData = new FormData()
      formData.append('file', selectedFile.value)
      formData.append('inputType', activeTab.value.toUpperCase())
      const res = await axios.post('/api/queries/upload', formData)
      await loadQueries()
      router.push('/query/' + res.data.id)
    } catch (e) {
      error.value = 'Failed to process file. Please try again.'
    } finally {
      loading.value = false
    }
  }
}

function truncate(text, max) {
  if (!text) return ''
  return text.length > max ? text.substring(0, max) + '...' : text
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'})
}
</script>

<style scoped>
.section-title { font-size: 1.2rem; color: var(--primary); margin-bottom: 20px; font-weight: 700; }
.input-tabs { display: flex; gap: 4px; margin-bottom: 20px; background: #f0f4f8; padding: 4px; border-radius: 10px; }
.tab-btn {
  flex: 1;
  padding: 8px 4px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  transition: all 0.2s;
}
.tab-btn.active { background: white; color: var(--primary); box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.tab-content { animation: fadeIn 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(4px); } to { opacity: 1; transform: none; } }

.voice-area { text-align: center; padding: 24px; background: #f8f9fa; border-radius: 10px; margin-bottom: 16px; }
.voice-btn {
  padding: 14px 28px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 12px;
}
.voice-btn.recording {
  background: var(--danger);
  animation: pulse 1s infinite;
}
@keyframes pulse { 0%,100% { transform: scale(1); } 50% { transform: scale(1.05); } }
.voice-hint { font-size: 13px; color: var(--text-muted); margin-bottom: 12px; }
.voice-transcript {
  background: white;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 12px;
  font-size: 13px;
  text-align: left;
  margin-top: 12px;
}

.upload-area {
  border: 2px dashed var(--border);
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafafa;
}
.upload-area:hover { border-color: var(--primary); background: #f0f4f8; }
.upload-icon { font-size: 3rem; margin-bottom: 8px; }
.upload-hint { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
.selected-file {
  margin-top: 12px;
  padding: 10px 14px;
  background: #e8f5e9;
  border-radius: 8px;
  font-size: 13px;
  color: var(--success);
}

.full-width { width: 100%; justify-content: center; margin-top: 4px; }
.spinner-sm {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  display: inline-block;
}

.mt-16 { margin-top: 16px; }

.tips-card h3 { font-size: 1rem; color: var(--primary); margin-bottom: 12px; }
.tips-list { list-style: none; display: flex; flex-direction: column; gap: 10px; }
.tips-list li { font-size: 13px; color: var(--text-muted); padding-left: 20px; position: relative; line-height: 1.5; }
.tips-list li::before { content: '•'; position: absolute; left: 4px; color: var(--accent); }

.query-item {
  padding: 14px;
  border: 1px solid var(--border);
  border-radius: 10px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.query-item:hover { border-color: var(--primary); background: #f8f9ff; }
.query-header { display: flex; gap: 6px; margin-bottom: 8px; flex-wrap: wrap; }
.query-text { font-size: 13px; color: var(--text); line-height: 1.4; margin-bottom: 6px; }
.query-time { font-size: 12px; color: var(--text-muted); }
</style>
