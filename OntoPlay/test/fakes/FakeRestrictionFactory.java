package fakes;

import ontoplay.models.ConfigurationException;
import ontoplay.models.owlGeneration.RestrictionFactory;
import ontoplay.models.propertyConditions.DatatypePropertyCondition;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import java.util.List;

public class FakeRestrictionFactory implements RestrictionFactory<DatatypePropertyCondition> {

    private DatatypePropertyCondition receivedCondition;

    public DatatypePropertyCondition getReceivedCondition() {
        return receivedCondition;
    }

    @Override
    public OWLClassExpression createRestriction(DatatypePropertyCondition condition) throws ConfigurationException {
        receivedCondition = condition;
        return null;
    }

    @Override
    public List<OWLAxiom> createIndividualValue(DatatypePropertyCondition condition, OWLIndividual individual) throws ConfigurationException {
        receivedCondition = condition;
        return null;
    }

}
