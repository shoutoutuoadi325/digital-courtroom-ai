<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>👨‍⚖️ HITL Review Panel</h1>
        <p>Human-in-the-Loop: Legal professionals review and correct AI-generated analysis to continuously improve accuracy.</p>
      </div>
    </div>
    <div class="container">
      <div class="review-stats card">
        <div class="stat">
          <div class="stat-value">{{ pendingReviews.length }}</div>
          <div class="stat-label">Pending Reviews</div>
        </div>
        <div class="stat">
          <div class="stat-value">{{ approvedCount }}</div>
          <div class="stat-label">Approved</div>
        </div>
        <div class="stat">
          <div class="stat-value">{{ correctedCount }}</div>
          <div class="stat-label">Corrected & Learned</div>
        </div>
        <div class="stat">
          <div class="stat-value">{{ allReviews.length }}</div>
          <div class="stat-label">Total Reviews</div>
        </div>
      </div>

      <div class="review-tabs">
        <button class="tab-btn" :class="{active: activeTab === 'pending'}" @click="activeTab = 'pending'">
          ⏳ Pending ({{ pendingReviews.length }})
        </button>
        <button class="tab-btn" :class="{active: activeTab === 'all'}" @click="activeTab = 'all'">
          📋 All Reviews ({{ allReviews.length }})
        </button>
      </div>

      <div v-if="loading" class="loading"><div class="spinner"></div> Loading reviews...</div>

      <div v-else-if="activeTab === 'pending'">
        <div v-if="pendingReviews.length === 0" class="empty-state card">
          <div class="icon">✅</div>
          <h3>All Caught Up!</h3>
          <p>No pending reviews at this time.</p>
        </div>
        <div v-else v-for="review in pendingReviews" :key="review.id" class="review-card card">
          <div class="review-header">
            <span class="badge badge-pending">⏳ Pending Review</span>
            <span class="review-id">Query #{{ review.query?.id }}</span>
            <span class="review-time">{{ formatDate(review.createdAt) }}</span>
          </div>
          <div class="review-body">
            <div class="review-section">
              <label>Original Query:</label>
              <p class="query-text">{{ review.query?.queryText }}</p>
            </div>
            <div class="review-section">
              <label>AI-Generated Response:</label>
              <div class="response-preview">{{ review.originalResponse }}</div>
            </div>
          </div>

          <div v-if="activeReviewId === review.id" class="review-form">
            <div class="form-group">
              <label class="form-label">Reviewer Name *</label>
              <input v-model="reviewForm.reviewerName" class="form-control" placeholder="Your name / ID" />
            </div>
            <div class="form-group">
              <label class="form-label">Action</label>
              <div class="radio-group">
                <label class="radio-option">
                  <input type="radio" v-model="reviewForm.status" value="APPROVED" />
                  <span class="radio-label approve">✅ Approve — Response is accurate</span>
                </label>
                <label class="radio-option">
                  <input type="radio" v-model="reviewForm.status" value="CORRECTED" />
                  <span class="radio-label correct">✏️ Correct — Provide improved response</span>
                </label>
              </div>
            </div>
            <div v-if="reviewForm.status === 'CORRECTED'" class="form-group">
              <label class="form-label">Corrected Response *</label>
              <textarea v-model="reviewForm.correctedResponse" class="form-control" rows="6"
                placeholder="Provide the corrected, accurate legal analysis..."></textarea>
            </div>
            <div class="form-group">
              <label class="form-label">Review Notes (optional)</label>
              <textarea v-model="reviewForm.reviewerNotes" class="form-control" rows="3"
                placeholder="Add notes about this review for future reference..."></textarea>
            </div>
            <div class="form-actions">
              <button class="btn btn-success" @click="submitReview(review.id)" :disabled="submitting">
                {{ submitting ? 'Submitting...' : '✅ Submit Review' }}
              </button>
              <button class="btn btn-outline" @click="activeReviewId = null">Cancel</button>
            </div>
            <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>
            <div v-if="reviewError" class="alert alert-error">{{ reviewError }}</div>
          </div>
          <div v-else class="review-actions">
            <button class="btn btn-primary btn-sm" @click="openReview(review)">📝 Review This</button>
          </div>
        </div>
      </div>

      <div v-else>
        <div v-if="allReviews.length === 0" class="empty-state card">
          <div class="icon">📋</div>
          <h3>No Reviews Yet</h3>
          <p>Reviews will appear here as queries are processed.</p>
        </div>
        <div v-else v-for="review in allReviews" :key="review.id" class="review-card card">
          <div class="review-header">
            <span class="badge" :class="'badge-' + (review.status || '').toLowerCase()">
              {{ review.status === 'APPROVED' ? '✅ Approved' : review.status === 'CORRECTED' ? '✏️ Corrected' : '⏳ Pending' }}
            </span>
            <span class="review-id">Query #{{ review.query?.id }}</span>
            <span v-if="review.reviewerName" class="reviewer">by {{ review.reviewerName }}</span>
            <span class="review-time">{{ formatDate(review.reviewedAt || review.createdAt) }}</span>
          </div>
          <p class="query-text-sm">{{ truncate(review.query?.queryText, 120) }}</p>
          <div v-if="review.correctedResponse" class="correction-note">
            <strong>Correction Applied:</strong> AI response has been updated with expert knowledge, expanding the legal corpus.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const pendingReviews = ref([])
const allReviews = ref([])
const loading = ref(true)
const activeTab = ref('pending')
const activeReviewId = ref(null)
const submitting = ref(false)
const successMsg = ref('')
const reviewError = ref('')
const reviewForm = ref({ status: 'APPROVED', correctedResponse: '', reviewerNotes: '', reviewerName: '' })

const approvedCount = computed(() => allReviews.value.filter(r => r.status === 'APPROVED').length)
const correctedCount = computed(() => allReviews.value.filter(r => r.status === 'CORRECTED').length)

onMounted(async () => {
  await Promise.all([loadPendingReviews(), loadAllReviews()])
  loading.value = false
})

async function loadPendingReviews() {
  try {
    const res = await axios.get('/api/reviews/pending')
    pendingReviews.value = res.data
  } catch (e) { console.error(e) }
}

async function loadAllReviews() {
  try {
    const res = await axios.get('/api/reviews')
    allReviews.value = res.data
  } catch (e) { console.error(e) }
}

function openReview(review) {
  activeReviewId.value = review.id
  reviewForm.value = {
    status: 'APPROVED',
    correctedResponse: review.originalResponse,
    reviewerNotes: '',
    reviewerName: ''
  }
  successMsg.value = ''
  reviewError.value = ''
}

async function submitReview(reviewId) {
  reviewError.value = ''
  successMsg.value = ''

  if (!reviewForm.value.reviewerName) {
    reviewError.value = 'Please enter your name.'
    return
  }
  if (reviewForm.value.status === 'CORRECTED' && !reviewForm.value.correctedResponse) {
    reviewError.value = 'Please provide the corrected response.'
    return
  }

  const review = pendingReviews.value.find(r => r.id === reviewId)
  if (!review) return

  submitting.value = true
  try {
    await axios.post('/api/reviews', {
      queryId: review.query.id,
      correctedResponse: reviewForm.value.correctedResponse,
      reviewerNotes: reviewForm.value.reviewerNotes,
      reviewerName: reviewForm.value.reviewerName,
      status: reviewForm.value.status
    })
    successMsg.value = reviewForm.value.status === 'CORRECTED'
      ? '✅ Review submitted! The corrected response has been added to the legal corpus.'
      : '✅ Response approved and marked as verified.'
    await Promise.all([loadPendingReviews(), loadAllReviews()])
    setTimeout(() => { activeReviewId.value = null }, 2000)
  } catch (e) {
    reviewError.value = 'Failed to submit review. Please try again.'
  } finally {
    submitting.value = false
  }
}

function truncate(text, max) {
  if (!text) return ''
  return text.length > max ? text.substring(0, max) + '...' : text
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>

<style scoped>
.review-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  text-align: center;
  margin-bottom: 24px;
}
.stat-value { font-size: 2rem; font-weight: 800; color: var(--primary); }
.stat-label { font-size: 13px; color: var(--text-muted); margin-top: 4px; }

.review-tabs { display: flex; gap: 8px; margin-bottom: 20px; }
.tab-btn {
  padding: 10px 20px;
  border: 2px solid var(--border);
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s;
}
.tab-btn.active { background: var(--primary); color: white; border-color: var(--primary); }

.review-card { border: 1px solid var(--border); }
.review-header { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; flex-wrap: wrap; }
.review-id, .review-time { font-size: 12px; color: var(--text-muted); }
.reviewer { font-size: 12px; color: var(--info); font-weight: 600; }

.review-body { display: flex; gap: 16px; margin-bottom: 16px; }
.review-section { flex: 1; }
.review-section label { font-size: 12px; font-weight: 700; color: var(--text-muted); text-transform: uppercase; display: block; margin-bottom: 6px; }
.query-text { font-size: 14px; color: var(--text); line-height: 1.4; }
.query-text-sm { font-size: 13px; color: var(--text-muted); }
.response-preview {
  font-size: 12px;
  color: var(--text-muted);
  background: #f8f9fa;
  border-radius: 6px;
  padding: 8px 10px;
  max-height: 80px;
  overflow: hidden;
  line-height: 1.4;
  white-space: pre-wrap;
}

.review-form { border-top: 1px solid var(--border); padding-top: 16px; margin-top: 4px; }
.radio-group { display: flex; flex-direction: column; gap: 8px; }
.radio-option { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.radio-option input { width: 16px; height: 16px; }
.radio-label { font-size: 14px; font-weight: 600; }
.radio-label.approve { color: var(--success); }
.radio-label.correct { color: var(--warning); }

.form-actions { display: flex; gap: 10px; margin-top: 4px; }
.review-actions { border-top: 1px solid var(--border); padding-top: 14px; margin-top: 4px; }

.correction-note {
  font-size: 12px;
  color: var(--success);
  background: #e8f5e9;
  padding: 6px 10px;
  border-radius: 6px;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .review-stats { grid-template-columns: repeat(2, 1fr); }
  .review-body { flex-direction: column; }
}
</style>
