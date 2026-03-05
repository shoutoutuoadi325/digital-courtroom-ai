package com.courtroom.service;

import com.courtroom.entity.MockTrial;
import com.courtroom.entity.TrialMessage;
import com.courtroom.repository.MockTrialRepository;
import com.courtroom.repository.TrialMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MockTrialService {

    @Autowired
    private MockTrialRepository trialRepository;

    @Autowired
    private TrialMessageRepository messageRepository;

    // Multi-agent responses based on role and stage
    public List<TrialMessage> processUserMessage(MockTrial trial, String userContent, String messageType) {
        List<TrialMessage> agentResponses = new ArrayList<>();
        MockTrial.UserRole userRole = trial.getUserRole();
        String stage = trial.getCurrentStage();

        // Generate responses from agents that are NOT the user's role
        if (userRole != MockTrial.UserRole.JUDGE) {
            agentResponses.add(generateJudgeResponse(trial, userContent, stage));
        }
        if (userRole != MockTrial.UserRole.PLAINTIFF) {
            agentResponses.add(generatePlaintiffResponse(trial, userContent, stage));
        }
        if (userRole != MockTrial.UserRole.DEFENDANT) {
            agentResponses.add(generateDefendantResponse(trial, userContent, stage));
        }
        if (userRole != MockTrial.UserRole.LAWYER) {
            agentResponses.add(generateLawyerResponse(trial, userContent, stage));
        }

        // Advance stage if appropriate
        advanceStageIfNeeded(trial, stage);

        return agentResponses;
    }

    private TrialMessage generateJudgeResponse(MockTrial trial, String userContent, String stage) {
        TrialMessage msg = new TrialMessage();
        msg.setTrial(trial);
        msg.setSenderRole("JUDGE_AGENT");
        msg.setMessageType(TrialMessage.MessageType.RULING);

        String response = switch (stage) {
            case "OPENING" -> "The court will come to order. We are here today to adjudicate the matter of " +
                trial.getTitle() + ". Both parties shall present their opening statements. " +
                "The plaintiff will proceed first, followed by the defense. " +
                "All parties are reminded to address the court respectfully and present only relevant evidence.";
            case "EVIDENCE" -> "The court acknowledges the submission of evidence. " +
                "All evidence must be properly authenticated and relevant to the matter at hand. " +
                "Objections to evidence admissibility should be raised at this time. " +
                "The court will review the submitted materials and rule on their admissibility.";
            case "CROSS_EXAMINATION" -> "Cross-examination is now permitted. " +
                "Questions must be limited to the scope of direct examination. " +
                "Leading questions are permitted during cross-examination. " +
                "Counsel may proceed with cross-examination of the witness.";
            case "CLOSING" -> "The court will now hear closing arguments. " +
                "Each party will have adequate time to summarize their case. " +
                "Counsel should focus on the evidence presented and applicable law. " +
                "The court will deliberate after hearing all closing statements.";
            case "VERDICT" -> generateVerdict(trial);
            default -> "Please proceed with your statement.";
        };

        msg.setContent(response);
        return messageRepository.save(msg);
    }

    private TrialMessage generatePlaintiffResponse(MockTrial trial, String userContent, String stage) {
        TrialMessage msg = new TrialMessage();
        msg.setTrial(trial);
        msg.setSenderRole("PLAINTIFF_AGENT");
        msg.setMessageType(TrialMessage.MessageType.STATEMENT);

        String response = switch (stage) {
            case "OPENING" -> "Your Honor, members of the court, " +
                "the plaintiff respectfully submits that the defendant's actions have caused significant harm. " +
                "We will present clear and convincing evidence demonstrating the defendant's liability. " +
                "The facts of this case are straightforward: the defendant breached their legal obligations, " +
                "causing direct and measurable damages to my client. We seek full compensation and justice.";
            case "EVIDENCE" -> "The plaintiff presents Exhibit A: documentary evidence supporting our claims. " +
                "This evidence clearly establishes the timeline of events and the defendant's culpability. " +
                "We also submit expert testimony confirming the extent of damages suffered by the plaintiff.";
            case "CROSS_EXAMINATION" -> "I object, Your Honor. The defense's line of questioning is irrelevant " +
                "and designed to confuse the witness. The question calls for speculation and is beyond " +
                "the scope of direct examination. The plaintiff maintains that the facts speak for themselves.";
            case "CLOSING" -> "Your Honor, the evidence presented clearly demonstrates the defendant's liability. " +
                "The plaintiff has suffered real and documented harm. Justice requires that the defendant " +
                "be held accountable and that my client receive appropriate compensation. " +
                "We respectfully request a judgment in favor of the plaintiff.";
            default -> "The plaintiff notes the proceedings and reserves the right to respond.";
        };

        msg.setContent(response);
        return messageRepository.save(msg);
    }

    private TrialMessage generateDefendantResponse(MockTrial trial, String userContent, String stage) {
        TrialMessage msg = new TrialMessage();
        msg.setTrial(trial);
        msg.setSenderRole("DEFENDANT_AGENT");
        msg.setMessageType(TrialMessage.MessageType.STATEMENT);

        String response = switch (stage) {
            case "OPENING" -> "Your Honor, the defense categorically denies the plaintiff's allegations. " +
                "The defendant acted in good faith and in accordance with all legal obligations. " +
                "The plaintiff's claims are without merit and lack sufficient evidence. " +
                "We will demonstrate that any harm claimed by the plaintiff was caused by their own actions " +
                "and that the defendant bears no liability in this matter.";
            case "EVIDENCE" -> "The defense submits counter-evidence demonstrating the plaintiff's contributory negligence. " +
                "Exhibit B shows that the plaintiff failed to mitigate their damages. " +
                "Furthermore, the timeline presented by the plaintiff is factually inaccurate, " +
                "as our evidence clearly demonstrates.";
            case "CROSS_EXAMINATION" -> "Objection, Your Honor! The question is leading and prejudicial. " +
                "The defense moves to strike the witness's last statement as hearsay. " +
                "Furthermore, the plaintiff's counsel is badgering the witness with repetitive questions. " +
                "The defendant maintains complete innocence regarding all allegations.";
            case "CLOSING" -> "Your Honor, the plaintiff has failed to meet their burden of proof. " +
                "The evidence presented is circumstantial at best. The defendant acted reasonably " +
                "and lawfully throughout this matter. We respectfully request that the court find " +
                "in favor of the defendant and dismiss all claims.";
            default -> "The defendant reserves all rights and defenses.";
        };

        msg.setContent(response);
        return messageRepository.save(msg);
    }

    private TrialMessage generateLawyerResponse(MockTrial trial, String userContent, String stage) {
        TrialMessage msg = new TrialMessage();
        msg.setTrial(trial);
        msg.setSenderRole("LAWYER_AGENT");
        msg.setMessageType(TrialMessage.MessageType.QUESTION);

        String response = switch (stage) {
            case "OPENING" -> "As counsel of record, I wish to note several important legal precedents " +
                "that are directly applicable to this case. Under established case law, the standard of care " +
                "required in this situation is well-defined. The applicable statutes clearly delineate " +
                "the rights and obligations of each party. We will rely on these legal standards throughout " +
                "these proceedings to ensure a fair and just outcome.";
            case "EVIDENCE" -> "Counsel requests permission to cross-examine regarding the chain of custody " +
                "of the submitted evidence. Additionally, counsel moves to introduce supplementary expert testimony " +
                "on the technical aspects of this case. The legal standards for admissibility require that " +
                "all evidence be properly authenticated before the court may consider it.";
            case "CROSS_EXAMINATION" -> "Permission to approach the witness, Your Honor? " +
                "I direct the witness's attention to the contract dated three months prior. " +
                "Is it not true that you were aware of the legal requirements at the time of signing? " +
                "And yet, you chose to proceed without proper legal counsel, correct? " +
                "No further questions at this time.";
            case "CLOSING" -> "Your Honor, the law is clear on this matter. Established precedents support " +
                "the position that parties have a duty to act in good faith and exercise reasonable care. " +
                "The evidence presented satisfies all elements required by statute. " +
                "Counsel respectfully submits that the law supports a finding in our client's favor.";
            default -> "Counsel notes the proceedings and will respond as appropriate.";
        };

        msg.setContent(response);
        return messageRepository.save(msg);
    }

    private String generateVerdict(MockTrial trial) {
        Random random = new Random();
        boolean plaintiffWins = random.nextBoolean();

        if (plaintiffWins) {
            return "VERDICT: After careful deliberation, the court finds in FAVOR OF THE PLAINTIFF. " +
                "The evidence presented clearly establishes the defendant's liability. " +
                "The defendant is hereby ordered to compensate the plaintiff for all documented damages. " +
                "Judgment is entered for the plaintiff in the amount to be determined by further proceedings. " +
                "Court is adjourned.";
        } else {
            return "VERDICT: After careful deliberation, the court finds in FAVOR OF THE DEFENDANT. " +
                "The plaintiff has failed to prove their case by the required standard of proof. " +
                "All claims against the defendant are hereby dismissed with prejudice. " +
                "The plaintiff is ordered to pay court costs. Court is adjourned.";
        }
    }

    private void advanceStageIfNeeded(MockTrial trial, String currentStage) {
        String nextStage = switch (currentStage) {
            case "OPENING" -> "EVIDENCE";
            case "EVIDENCE" -> "CROSS_EXAMINATION";
            case "CROSS_EXAMINATION" -> "CLOSING";
            case "CLOSING" -> "VERDICT";
            default -> currentStage;
        };

        // Advance stage randomly to simulate progression (in production, track message count)
        Random random = new Random();
        if (random.nextInt(3) == 0) { // 33% chance to advance
            trial.setCurrentStage(nextStage);
            if ("VERDICT".equals(nextStage)) {
                trial.setStatus(MockTrial.TrialStatus.COMPLETED);
            }
            trialRepository.save(trial);
        }
    }
}
