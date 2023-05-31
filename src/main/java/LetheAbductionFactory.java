import abduction_api.abducible.AbducibleContainer;
import abduction_api.abducible.SymbolAbducibleContainer;
import abduction_api.factory.AbductionFactory;
import abduction_api.manager.AbductionManager;
import abduction_api.manager.MultiObservationManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;

public class LetheAbductionFactory implements AbductionFactory {

    private static final LetheAbductionFactory instance = new LetheAbductionFactory();

    private LetheAbductionFactory(){}

    public static LetheAbductionFactory getFactory(){
        return instance;
    }

    @Override
    public AbductionManager getAbductionManager() {
        return new LetheAbductionManager();
    }

    @Override
    public AbductionManager getAbductionManager(OWLOntology backgroundKnowledge, OWLAxiom observation) {
        return new LetheAbductionManager(backgroundKnowledge, observation);
    }

    @Override
    public MultiObservationManager getMultiObservationAbductionManager() {
        return new LetheAbductionManager();
    }

    @Override
    public MultiObservationManager getMultiObservationAbductionManager(OWLOntology backgroundKnowledge, Collection<OWLAxiom> observation) {
        return new LetheAbductionManager(backgroundKnowledge, observation);
    }

    @Override
    public AbducibleContainer getAbducibleContainer() {
        return new LetheAbducibleContainer();
    }

    @Override
    public SymbolAbducibleContainer getSymbolAbducibleContainer() {
        return new LetheAbducibleContainer();
    }

    @Override
    public SymbolAbducibleContainer getSymbolAbducibleContainer(Collection<OWLEntity> symbols) {
        return new LetheAbducibleContainer(symbols);
    }

}
