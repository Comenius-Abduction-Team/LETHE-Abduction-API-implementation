import abduction_api.abducible.AbducibleContainer;
import abduction_api.abducible.AxiomAbducibleContainer;
import abduction_api.abducible.ExplanationConfigurator;
import abduction_api.abducible.SymbolAbducibleContainer;
import abduction_api.exception.*;
import abduction_api.manager.ExplanationWrapper;
import abduction_api.manager.MultiObservationManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.man.cs.lethe.abduction.OWLAbducer;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class LetheAbductionManager implements MultiObservationManager {

    private LetheAbducibleContainer abducibles;
    private OWLOntology knowledgeBase;
    private Set<OWLAxiom> observations;
    private String message;
    private String logs;
    private Set<ExplanationWrapper> explanations = new HashSet<>();
    private boolean optimized = true;
    private boolean checkingEntailment = true;

    public LetheAbductionManager(){}

    public LetheAbductionManager(OWLOntology backgroundKnowledge, OWLAxiom observation){
        setBackgroundKnowledge(backgroundKnowledge);
        setObservation(observation);
    }

    public LetheAbductionManager(OWLOntology backgroundKnowledge, Collection<OWLAxiom> observation){
        setBackgroundKnowledge(backgroundKnowledge);
        setMultiAxiomObservation(new HashSet<>(observation));
    }

    @Override
    public void setBackgroundKnowledge(OWLOntology knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    @Override
    public OWLOntology getBackgroundKnowledge() {
        return knowledgeBase;
    }

    @Override
    public void setObservation(OWLAxiom observation) throws MultiObservationException, InvalidObservationException {
        this.observations = Collections.singleton(observation);
    }

    @Override
    public OWLAxiom getObservation() throws MultiObservationException {
        if (observations.size() > 1)
            throw new MultiObservationException("There are multiple axioms in the observation.");
        return this.observations.stream().findFirst().orElse(null);
    }

    @Override
    public void solveAbduction() {

        resetResults();

        if (knowledgeBase == null || knowledgeBase.isEmpty())
            throw new CommonException("The background knowledge is empty or missing!");

        if (observations == null || observations.isEmpty())
            throw new CommonException("The observation is empty or missing!");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try{
            PrintStream output = new PrintStream(stream);
            System.setOut(output);

            OWLAbducer abducer;
            abducer = setupAbducer();
            String representation = abducer.formatAbductionResult(observations);

            if (representation.equals("⊥")) {
                message = "Observation cannot be explained!";
                return;
            }

            ExplanationWrapper wrapper = new ExplanationWrapper(representation);
            explanations = Collections.singleton(wrapper);
        } catch (Exception e){
            message = e.getMessage();
        } finally {
            logs = stream.toString();
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        }

    }

    private OWLAbducer setupAbducer() {

        OWLAbducer abducer = new OWLAbducer();

        abducer.optimizeUsingModules_$eq(optimized);
        abducer.setCheckEntailed(checkingEntailment);

        abducer.setBackgroundOntology(knowledgeBase);
        setValidAbducibles(abducer);

        return abducer;
    }

    private void resetResults() {
        explanations = new HashSet<>();
        message = "";
        logs = "";
    }

    @Override
    public Set<ExplanationWrapper> getExplanations() {
        return explanations;
    }

    @Override
    public String getOutputMessage() {
        return message;
    }

    @Override
    public String getFullLog() {
        return logs;
    }

    private void setValidAbducibles(OWLAbducer abducer){
        if (abducibles == null || abducibles.isEmpty()){
            abducer.setAbducibles(knowledgeBase.getSignature());
        }
        else abducer.setAbducibles(abducibles.symbols);
    }

    @Override
    public void setAbducibleContainer(AbducibleContainer abducibles) {

        if (abducibles instanceof LetheAbducibleContainer)
            this.abducibles = (LetheAbducibleContainer) abducibles;

        else if (abducibles instanceof AxiomAbducibleContainer){
            throw new CommonException("Abducible container type not compatible with abduction manager!");
        }

        else if (abducibles instanceof SymbolAbducibleContainer){
            SymbolAbducibleContainer symbolAbducibles = (SymbolAbducibleContainer) abducibles;
            this.abducibles = new LetheAbducibleContainer(symbolAbducibles.getSymbols());
        }

        else
            throw new CommonException("Abducible container type not compatible with abduction manager!");

    }

    @Override
    public AbducibleContainer getAbducibleContainer() {
        return abducibles;
    }

    @Override
    public void setMultiAxiomObservation(Set<OWLAxiom> observations) throws InvalidObservationException {
        this.observations = observations;
    }

    @Override
    public Set<OWLAxiom> getMultiAxiomObservation() {
        return observations;
    }

    @Override
    public void setSolverSpecificParameters(String s) {
        String[] arguments = s.split(" ");
        for (int i = 0; i < arguments.length; i++){
                switch (arguments[i]) {
                    case "-setCheckEntailment":
                        boolean checkingEntailment = Boolean.parseBoolean(arguments[i + 1]);
                        setCheckingEntailment(checkingEntailment);
                        i++;
                        continue;
                    case "-optimizeUsingModules":
                        boolean optimized = Boolean.parseBoolean(arguments[i + 1]);
                        setOptimized(optimized);
                        i++;
                        continue;
                    default:
                        throw new InvalidSolverParameterException(arguments[i], "Invalid solver parameter");
                }
        }
    }

    @Override
    public void resetSolverSpecificParameters() {
        setCheckingEntailment(true);
        setOptimized(true);
    }

    public boolean isOptimized() {
        return optimized;
    }

    public void setOptimized(boolean optimized) {
        this.optimized = optimized;
    }

    public boolean isCheckingEntailment() {
        return checkingEntailment;
    }

    public void setCheckingEntailment(boolean checkingEntailment) {
        this.checkingEntailment = checkingEntailment;
    }

    public ExplanationConfigurator getExplanationConfigurator(){
        return null;
    }

    @Override
    public void setExplanationConfigurator(ExplanationConfigurator explanationConfigurator) {
        throw new NotSupportedException("configurating explanations");
    }
}
