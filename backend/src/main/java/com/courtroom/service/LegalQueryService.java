package com.courtroom.service;

import com.courtroom.dto.QueryRequest;
import com.courtroom.dto.QueryResponse;
import com.courtroom.entity.HitlReview;
import com.courtroom.entity.LegalQuery;
import com.courtroom.exception.EntityNotFoundException;
import com.courtroom.repository.HitlReviewRepository;
import com.courtroom.repository.LegalQueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LegalQueryService {

    @Autowired
    private LegalQueryRepository queryRepository;

    @Autowired
    private HitlReviewRepository hitlReviewRepository;

    @Autowired
    private QueryRoutingService routingService;

    @Autowired
    private ObjectMapper objectMapper;

    public QueryResponse processQuery(QueryRequest request) throws Exception {
        LegalQuery query = new LegalQuery();
        query.setQueryText(request.getQueryText());
        query.setInputType(LegalQuery.InputType.valueOf(request.getInputType() != null ? request.getInputType() : "TEXT"));
        query.setStatus(LegalQuery.QueryStatus.PROCESSING);
        query = queryRepository.save(query);

        // Route query using hybrid reasoning engine
        LegalQuery.QueryType queryType = routingService.routeQuery(request.getQueryText());
        query.setQueryType(queryType);

        // Process based on routing decision
        String response;
        if (queryType == LegalQuery.QueryType.RULE_BASED) {
            response = routingService.processRuleBasedQuery(request.getQueryText());
        } else {
            response = routingService.processLlmQuery(request.getQueryText());
        }
        query.setResponse(response);

        // Generate mind map data
        Map<String, Object> mindMapData = routingService.generateMindMapData(
            request.getQueryText(), response, queryType);
        query.setMindMapData(objectMapper.writeValueAsString(mindMapData));

        // Mark as under review and create HITL review entry
        query.setStatus(LegalQuery.QueryStatus.UNDER_REVIEW);

        HitlReview review = new HitlReview();
        review.setQuery(query);
        review.setOriginalResponse(response);
        review.setStatus(HitlReview.ReviewStatus.PENDING);
        query = queryRepository.save(query);
        hitlReviewRepository.save(review);

        return toQueryResponse(query, mindMapData);
    }

    public List<QueryResponse> getAllQueries() {
        return queryRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(q -> toQueryResponse(q, null))
            .collect(Collectors.toList());
    }

    public QueryResponse getQueryById(Long id) throws Exception {
        LegalQuery query = queryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Query not found: " + id));
        Map<String, Object> mindMapData = null;
        if (query.getMindMapData() != null) {
            mindMapData = objectMapper.readValue(query.getMindMapData(), Map.class);
        }
        return toQueryResponse(query, mindMapData);
    }

    private QueryResponse toQueryResponse(LegalQuery query, Map<String, Object> mindMapData) {
        QueryResponse resp = new QueryResponse();
        resp.setId(query.getId());
        resp.setQueryText(query.getQueryText());
        resp.setQueryType(query.getQueryType() != null ? query.getQueryType().name() : null);
        resp.setInputType(query.getInputType() != null ? query.getInputType().name() : null);
        resp.setResponse(query.getResponse());
        resp.setMindMapData(mindMapData);
        resp.setStatus(query.getStatus() != null ? query.getStatus().name() : null);
        resp.setCreatedAt(query.getCreatedAt());
        return resp;
    }
}
