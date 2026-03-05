package com.courtroom.config;

import com.courtroom.entity.LegalCorpus;
import com.courtroom.repository.LegalCorpusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private LegalCorpusRepository corpusRepository;

    @Override
    public void run(String... args) throws Exception {
        if (corpusRepository.count() == 0) {
            // Seed initial legal corpus data
            addCorpusEntry("Contract Law - Breach", "CONTRACT",
                "contract,breach,agreement,obligation,performance,damages",
                "Under Contract Law Article 107, a party who breaches a contract shall bear liability for breach of contract, including continuing to perform obligations, taking remedial measures, or compensating for losses.",
                "{\"type\":\"RULE_BASED\",\"elements\":[\"valid_contract\",\"breach\",\"damages\"]}");

            addCorpusEntry("Marriage Law - Divorce", "FAMILY",
                "divorce,marriage,separation,custody,property,alimony",
                "Civil Code Articles 1076-1081 govern divorce procedures. Divorce may be obtained through civil affairs bureau registration (consensual) or court proceedings (contested).",
                "{\"type\":\"RULE_BASED\",\"elements\":[\"valid_marriage\",\"grounds_for_divorce\"]}");

            addCorpusEntry("Inheritance Law", "FAMILY",
                "inheritance,will,estate,beneficiary,succession,heir",
                "Civil Code Chapter VI governs succession. First-order heirs are spouse, children, and parents. Second-order heirs are siblings and grandparents.",
                "{\"type\":\"RULE_BASED\",\"elements\":[\"deceased\",\"valid_will_or_intestacy\"]}");

            addCorpusEntry("Traffic Accident Liability", "TORT",
                "traffic,accident,liability,injury,compensation,insurance",
                "Road Traffic Safety Law and Tort Liability Law govern traffic accidents. Liability is primarily determined by traffic police reports.",
                "{\"type\":\"RULE_BASED\",\"elements\":[\"accident\",\"liability_determination\",\"damages\"]}");

            addCorpusEntry("Labor Dispute Resolution", "LABOR",
                "labor,employment,termination,wages,arbitration,workplace",
                "Labor Law and Labor Contract Law govern employment relationships. Disputes must first go through mandatory arbitration before court proceedings.",
                "{\"type\":\"RULE_BASED\",\"elements\":[\"employment_relationship\",\"dispute_type\"]}");
        }
    }

    private void addCorpusEntry(String title, String category, String keywords,
                                 String content, String structuredRules) {
        LegalCorpus corpus = new LegalCorpus();
        corpus.setTitle(title);
        corpus.setCategory(category);
        corpus.setKeywords(keywords);
        corpus.setContent(content);
        corpus.setStructuredRules(structuredRules);
        corpus.setVerified(true);
        corpusRepository.save(corpus);
    }
}
