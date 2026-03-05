package com.courtroom.service;

import com.courtroom.entity.LegalCorpus;
import com.courtroom.entity.LegalQuery;
import com.courtroom.repository.LegalCorpusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class QueryRoutingService {

    @Autowired
    private LegalCorpusRepository corpusRepository;

    // Keywords that indicate structured/standardized queries suitable for rule-based processing
    private static final List<String> RULE_BASED_KEYWORDS = Arrays.asList(
        "contract breach", "合同违约", "divorce", "离婚", "inheritance", "继承",
        "traffic accident", "交通事故", "labor dispute", "劳动争议",
        "property dispute", "房产纠纷", "criminal record", "刑事记录",
        "statute of limitations", "诉讼时效", "court fee", "诉讼费用"
    );

    // Complexity indicators that suggest LLM processing is needed
    private static final List<String> COMPLEX_INDICATORS = Arrays.asList(
        "complex", "ambiguous", "unclear", "multiple", "various", "complicated",
        "what if", "suppose", "hypothetically", "analyze", "evaluate", "compare"
    );

    public LegalQuery.QueryType routeQuery(String queryText) {
        if (queryText == null || queryText.trim().isEmpty()) {
            return LegalQuery.QueryType.LLM_BASED;
        }

        String lowerQuery = queryText.toLowerCase();

        // Check for complex indicators first
        for (String indicator : COMPLEX_INDICATORS) {
            if (lowerQuery.contains(indicator)) {
                return LegalQuery.QueryType.LLM_BASED;
            }
        }

        // Check for standardized rule-based keywords
        for (String keyword : RULE_BASED_KEYWORDS) {
            if (lowerQuery.contains(keyword.toLowerCase())) {
                return LegalQuery.QueryType.RULE_BASED;
            }
        }

        // Check against legal corpus for matching structured rules
        String[] words = queryText.split("\\s+");
        for (String word : words) {
            if (word.length() > 3) {
                List<LegalCorpus> matches = corpusRepository.findByKeyword(word);
                if (!matches.isEmpty() && matches.get(0).getStructuredRules() != null) {
                    return LegalQuery.QueryType.RULE_BASED;
                }
            }
        }

        // Default to LLM for complex/unknown queries
        return LegalQuery.QueryType.LLM_BASED;
    }

    public String processRuleBasedQuery(String queryText) {
        String lowerQuery = queryText.toLowerCase();

        // Contract breach rules
        if (lowerQuery.contains("contract breach") || lowerQuery.contains("合同违约")) {
            return buildRuleBasedResponse("Contract Breach Analysis",
                Arrays.asList(
                    "Legal Basis: Contract Law Article 107 - Breach of contract liability",
                    "Elements Required: Valid contract existence, breach of obligation, causation, damages",
                    "Remedies Available: Specific performance, damages, contract rescission",
                    "Statute of Limitations: 3 years from discovery of breach",
                    "Court Jurisdiction: Civil court with jurisdiction based on contract performance location"
                ),
                Arrays.asList("Contract", "Breach of Contract", "Remedies", "Damages", "Statute of Limitations"));
        }

        // Divorce rules
        if (lowerQuery.contains("divorce") || lowerQuery.contains("离婚")) {
            return buildRuleBasedResponse("Divorce Procedure Analysis",
                Arrays.asList(
                    "Legal Basis: Marriage Law / Civil Code Articles 1076-1081",
                    "Methods: Civil Affairs Bureau registration (consensual) or Court proceedings (contested)",
                    "Property Division: Equal division of jointly acquired marital property",
                    "Child Custody: Best interest of child principle applies",
                    "Cooling-off Period: 30 days mandatory cooling-off for registered divorce"
                ),
                Arrays.asList("Divorce", "Property Division", "Child Custody", "Civil Code", "Court Procedure"));
        }

        // Inheritance rules
        if (lowerQuery.contains("inheritance") || lowerQuery.contains("继承")) {
            return buildRuleBasedResponse("Inheritance Law Analysis",
                Arrays.asList(
                    "Legal Basis: Civil Code Chapter VI - Succession",
                    "Order of Succession: First order: spouse, children, parents; Second order: siblings, grandparents",
                    "Will Requirements: Written, signed, dated, two witnesses (for attested will)",
                    "Acceptance Period: Inheritance must be accepted or renounced within prescribed period",
                    "Estate Taxes: Subject to applicable estate tax regulations"
                ),
                Arrays.asList("Inheritance", "Will", "Succession Order", "Estate", "Beneficiary"));
        }

        // Traffic accident rules
        if (lowerQuery.contains("traffic accident") || lowerQuery.contains("交通事故")) {
            return buildRuleBasedResponse("Traffic Accident Liability Analysis",
                Arrays.asList(
                    "Legal Basis: Road Traffic Safety Law, Tort Liability Law",
                    "Liability Determination: Traffic police accident report is primary evidence",
                    "Insurance Claims: Mandatory third-party liability insurance covers basic damages",
                    "Compensation Items: Medical expenses, lost wages, disability compensation, pain and suffering",
                    "Limitation Period: 3 years from date of accident or discovery of injury"
                ),
                Arrays.asList("Traffic Accident", "Liability", "Insurance", "Compensation", "Personal Injury"));
        }

        // Labor dispute rules
        if (lowerQuery.contains("labor dispute") || lowerQuery.contains("劳动争议")) {
            return buildRuleBasedResponse("Labor Dispute Resolution Analysis",
                Arrays.asList(
                    "Legal Basis: Labor Law, Labor Contract Law",
                    "Procedure: Mediation → Labor Arbitration (mandatory) → Court litigation",
                    "Arbitration Deadline: Must apply within 1 year of dispute arising",
                    "Common Issues: Wrongful termination, unpaid wages, overtime compensation",
                    "Employee Protections: Special protections for pregnant/disabled/older workers"
                ),
                Arrays.asList("Labor Dispute", "Arbitration", "Employment Contract", "Wrongful Termination", "Wages"));
        }

        // Generic legal response for other rule-based matches
        return buildRuleBasedResponse("Legal Analysis",
            Arrays.asList(
                "Query classified as structured legal matter",
                "Relevant laws and regulations identified",
                "Standard procedural requirements apply",
                "Please consult with a licensed attorney for specific advice",
                "Legal aid services available for qualifying individuals"
            ),
            Arrays.asList("Legal Issue", "Applicable Law", "Procedure", "Rights", "Remedies"));
    }

    public String processLlmQuery(String queryText) {
        // Simulated LLM response for complex queries
        // In production, this would call an actual LLM API (GPT-4, Claude, etc.)
        return buildLlmResponse(queryText);
    }

    private String buildRuleBasedResponse(String title, List<String> points, List<String> mindMapNodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("## ").append(title).append("\n\n");
        sb.append("*[Processed by Rule-Based Expert Engine]*\n\n");
        sb.append("### Key Legal Points:\n");
        for (int i = 0; i < points.size(); i++) {
            sb.append(i + 1).append(". ").append(points.get(i)).append("\n");
        }
        sb.append("\n### Recommended Next Steps:\n");
        sb.append("1. Gather all relevant documentation\n");
        sb.append("2. Consult with a qualified legal professional\n");
        sb.append("3. File appropriate legal documents within limitation periods\n");
        sb.append("\n*This analysis is based on standardized legal rules and should not be considered as legal advice.*");
        return sb.toString();
    }

    private String buildLlmResponse(String queryText) {
        // Simulated multi-agent LLM response
        return "## Comprehensive Legal Analysis\n\n" +
            "*[Processed by Multi-Agent LLM Engine - Agents: Legal Analyst, Devil's Advocate, Precedent Researcher]*\n\n" +
            "### Agent 1 - Legal Analyst:\n" +
            "Based on the query: \"" + queryText.substring(0, Math.min(queryText.length(), 100)) + "...\"\n\n" +
            "This appears to be a complex legal matter requiring careful analysis of multiple factors. " +
            "The primary legal considerations involve examining applicable statutory law, relevant case precedents, " +
            "and the specific factual circumstances presented.\n\n" +
            "### Agent 2 - Devil's Advocate:\n" +
            "Counter-arguments to consider: The opposing party may argue that standard legal interpretations " +
            "do not apply given the unique circumstances. Alternative legal theories should be explored, " +
            "including equitable remedies and constitutional protections.\n\n" +
            "### Agent 3 - Precedent Researcher:\n" +
            "Relevant precedents suggest that courts have generally ruled in favor of the party demonstrating " +
            "clear evidence and good faith compliance with legal obligations. Documentation is critical.\n\n" +
            "### Consensus Analysis:\n" +
            "After cross-verification among agents, the recommended approach is:\n" +
            "1. Preserve all evidence and documentation immediately\n" +
            "2. Identify the specific legal theory (tort, contract, statutory)\n" +
            "3. Assess jurisdiction and venue requirements\n" +
            "4. Calculate applicable limitation periods\n" +
            "5. Consider alternative dispute resolution before litigation\n\n" +
            "*This AI-generated analysis should be reviewed by a qualified legal professional before taking action.*";
    }

    public Map<String, Object> generateMindMapData(String queryText, String response, LegalQuery.QueryType queryType) {
        Map<String, Object> mindMap = new LinkedHashMap<>();
        mindMap.put("id", "root");
        mindMap.put("label", "Legal Analysis");
        mindMap.put("type", "root");

        List<Map<String, Object>> children = new ArrayList<>();

        // Add query node
        Map<String, Object> queryNode = new LinkedHashMap<>();
        queryNode.put("id", "query");
        queryNode.put("label", "Your Query");
        queryNode.put("type", "input");
        queryNode.put("content", queryText.substring(0, Math.min(queryText.length(), 80)));
        children.add(queryNode);

        // Add processing method node
        Map<String, Object> methodNode = new LinkedHashMap<>();
        methodNode.put("id", "method");
        methodNode.put("label", queryType == LegalQuery.QueryType.RULE_BASED ? "Rule-Based Engine" : "LLM Analysis");
        methodNode.put("type", "method");

        List<Map<String, Object>> methodChildren = new ArrayList<>();
        if (queryType == LegalQuery.QueryType.RULE_BASED) {
            methodChildren.add(createLeafNode("rule1", "Statutory Law", "law"));
            methodChildren.add(createLeafNode("rule2", "Legal Elements", "element"));
            methodChildren.add(createLeafNode("rule3", "Precedent Rules", "rule"));
        } else {
            methodChildren.add(createLeafNode("agent1", "Legal Analyst Agent", "agent"));
            methodChildren.add(createLeafNode("agent2", "Devil's Advocate Agent", "agent"));
            methodChildren.add(createLeafNode("agent3", "Precedent Researcher Agent", "agent"));
        }
        methodNode.put("children", methodChildren);
        children.add(methodNode);

        // Add legal issues node
        Map<String, Object> issuesNode = new LinkedHashMap<>();
        issuesNode.put("id", "issues");
        issuesNode.put("label", "Legal Issues");
        issuesNode.put("type", "issues");
        List<Map<String, Object>> issueChildren = new ArrayList<>();
        issueChildren.add(createLeafNode("issue1", "Primary Legal Issue", "issue"));
        issueChildren.add(createLeafNode("issue2", "Applicable Laws", "law"));
        issueChildren.add(createLeafNode("issue3", "Rights & Obligations", "right"));
        issuesNode.put("children", issueChildren);
        children.add(issuesNode);

        // Add remedies node
        Map<String, Object> remediesNode = new LinkedHashMap<>();
        remediesNode.put("id", "remedies");
        remediesNode.put("label", "Available Remedies");
        remediesNode.put("type", "remedies");
        List<Map<String, Object>> remedyChildren = new ArrayList<>();
        remedyChildren.add(createLeafNode("remedy1", "Civil Remedies", "remedy"));
        remedyChildren.add(createLeafNode("remedy2", "Administrative Action", "remedy"));
        remedyChildren.add(createLeafNode("remedy3", "Criminal Liability", "remedy"));
        remediesNode.put("children", remedyChildren);
        children.add(remediesNode);

        // Add next steps node
        Map<String, Object> stepsNode = new LinkedHashMap<>();
        stepsNode.put("id", "steps");
        stepsNode.put("label", "Next Steps");
        stepsNode.put("type", "steps");
        List<Map<String, Object>> stepChildren = new ArrayList<>();
        stepChildren.add(createLeafNode("step1", "Gather Evidence", "action"));
        stepChildren.add(createLeafNode("step2", "Consult Lawyer", "action"));
        stepChildren.add(createLeafNode("step3", "File Documents", "action"));
        stepsNode.put("children", stepChildren);
        children.add(stepsNode);

        mindMap.put("children", children);
        return mindMap;
    }

    private Map<String, Object> createLeafNode(String id, String label, String type) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id", id);
        node.put("label", label);
        node.put("type", type);
        return node;
    }
}
