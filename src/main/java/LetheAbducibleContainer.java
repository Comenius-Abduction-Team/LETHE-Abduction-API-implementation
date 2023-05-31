import abduction_api.abducible.SymbolAbducibleContainer;
import abduction_api.exception.SymbolAbducibleException;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LetheAbducibleContainer implements SymbolAbducibleContainer {

    Set<OWLEntity> symbols = new HashSet<>();

    public LetheAbducibleContainer(){}

    public LetheAbducibleContainer(Collection<OWLEntity> symbols) throws SymbolAbducibleException {
        addSymbols(symbols);
    }

    @Override
    public void setSymbols(Collection<OWLEntity> symbols) throws SymbolAbducibleException {
        this.symbols = new HashSet<>(symbols);
    }

    @Override
    public Set<OWLEntity> getSymbols() {
        return symbols;
    }

    @Override
    public void addSymbol(OWLEntity symbol) throws SymbolAbducibleException {
        symbols.add(symbol);
    }

    @Override
    public void addSymbols(Collection<OWLEntity> symbols) throws SymbolAbducibleException {
        symbols.forEach(this::addSymbol);
    }

    @Override
    public boolean isEmpty(){
        return symbols.isEmpty();
    }

    @Override
    public void clear() {
        symbols.clear();
    }
}
