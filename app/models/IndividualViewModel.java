package models;

import ontoplay.models.ontologyReading.OntologyReader;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.ArrayList;
import java.util.HashMap;

public class IndividualViewModel {

		private Individual individual;
		private OntClass ontClass;
		private String imageUrl;
		
		private String name;
		private String description;
	private OntologyReader ontologyReader;

	public String getClassUri()
		{
			return ontClass.getURI();
		}
		
		public String getDescription()
		{
			return description;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getUri()
		{
			return individual.getURI();
		}
		
		public String getImage()
		{
			return imageUrl;
		}
		
		public String getLocalName()
		{
			return individual.getLocalName();
		}
		
		public HashMap<String,Resource> functionalObjectValues = new HashMap<String, Resource>();
		public HashMap<String,ArrayList<Resource>> nonFunctionalObjectValues = new HashMap<String, ArrayList<Resource>>();
		public HashMap<String,Literal> functionalDataValues = new HashMap<String, Literal>();
		public HashMap<String,ArrayList<Literal>> nonFunctionalDataValues= new HashMap<String, ArrayList<Literal>>();
		
		public HashMap<String,Literal> getFunctionalData()
		{
			return functionalDataValues;
		}
		
		
		public IndividualViewModel(Individual individual, OntologyReader ontologyReader)
		{
			this.ontologyReader = ontologyReader;
			if(individual == null)
				throw new IllegalArgumentException("inidividual");
			this.individual = individual;
			this.ontClass = individual.getOntClass(true);
			Initialize();
			FixImageProperty();
			FixThingProperties();
		}
		
		private void FixThingProperties() {
			// TODO Auto-generated method stub
			if(functionalDataValues.containsKey("Name")){
				name = functionalDataValues.get("Name").getString();
				functionalDataValues.remove("Name");
			}
			else{
				name = individual.getLocalName();
			}
			
			if(functionalDataValues.containsKey("Description")){
				description = functionalDataValues.get("Description").getString();
				functionalDataValues.remove("Description");
			}
			else{
				description = "";
			}
		}

		private void FixImageProperty() {
			// TODO Auto-generated method stub
			if(functionalDataValues.containsKey("Image")){
				imageUrl = functionalDataValues.get("Image").getString();
				functionalDataValues.remove("Image");
			}
			else{
				imageUrl = FixImageUrl();
			}
		}

		private String FixImageUrl() {
			switch(ontClass.getLocalName())
			{
				case"Organization":
					return "images/Org.jpg";
				case"Offer":
					return "images/Org.jpg";
				case"Category":
					return "images/Org.jpg";
				case"Interest":
					return "images/Org.jpg";
				case"Product":
					return "images/Org.jpg";
				case"Consumer":
					return "images/Org.jpg";
				case"ConsumerAudience":
					return "images/Org.jpg";
				case"Gender":
					return "images/Org.jpg";
				case"Target":
					return "images/target-icon.gif";
				case"Place":
				case"Country":
				case"State":
				case"City":
				case"Street":
					return "images/Org.jpg";
				default:
					return "";
			}
			
			
		}

		public boolean getHasFunctionalDataProperties(){
			return functionalDataValues.size() > 0;
		}
		
		public boolean getHasNonFunctionalDataProperties(){
			return nonFunctionalObjectValues.size() > 0;
		}
		
		public boolean getHasFunctionalObjectProperties(){
			return functionalObjectValues.size() > 0;
		}
		
		public boolean getHasNonFunctionalObjectProperties(){
			return nonFunctionalDataValues.size() > 0;
		}
		
		private void Initialize(){
			
			ArrayList<OntProperty> functionalDataProperties = new ArrayList<OntProperty>();
			ArrayList<OntProperty> nonFunctionalDataProperties = new ArrayList<OntProperty>();
			ArrayList<OntProperty> functionalObjectProperties = new ArrayList<OntProperty>();
			ArrayList<OntProperty> nonFunctionalObjectProperties = new ArrayList<OntProperty>();
			
			ExtendedIterator<OntProperty> properties = ontClass.listDeclaredProperties(true);
			
			while (properties.hasNext()) {
				OntProperty property = properties.next();
				//Logger.info(getPropertyName(property));
				
				if(property.isDatatypeProperty())
				{
					if(property.isFunctionalProperty())
						functionalDataProperties.add(property);
					else
						nonFunctionalDataProperties.add(property);
				}
				else
				{
					if(property.isFunctionalProperty())
						functionalObjectProperties.add(property);
					else
						nonFunctionalObjectProperties.add(property);
				}
			}
			
			IntializeFunctionalDataProperties(functionalDataProperties);
			IntializeNonFunctionalDataProperties(nonFunctionalDataProperties);
			IntializeFunctionalObjectProperties(functionalObjectProperties);
			IntializeNonFunctionalObjectProperties(nonFunctionalObjectProperties);
		}

		private void IntializeNonFunctionalObjectProperties(
				ArrayList<OntProperty> nonFunctionalObjectProperties) {
			for (OntProperty property : nonFunctionalObjectProperties){
					NodeIterator it = individual.listPropertyValues(property);
					String name = getPropertyName(property);
					while (it.hasNext()) {
							RDFNode node = it.next();
							Resource res = null;
							if(node.isURIResource())
								res = getResourceFromURI(node);
							else
								res = node.asResource();
							if(!nonFunctionalObjectValues.containsKey(name))
								nonFunctionalObjectValues.put(name, new ArrayList<Resource>());
							nonFunctionalObjectValues.get(name).add(res);
					}
			    }
			
		}

		private void IntializeFunctionalObjectProperties(
				ArrayList<OntProperty> functionalObjectProperties) {
			for (OntProperty property : functionalObjectProperties){
				NodeIterator it = individual.listPropertyValues(property);
				String name = getPropertyName(property);
				while (it.hasNext()) {
						RDFNode node = it.next();
						Resource res = null;
						if(node.isURIResource())
							res = getResourceFromURI(node);
						else
							res = node.asResource();
						functionalObjectValues.put(name,res);
				}
		    }
			
		}

		private void IntializeNonFunctionalDataProperties(
				ArrayList<OntProperty> nonFunctionalDataProperties) {
			
			for (OntProperty property : nonFunctionalDataProperties){
				NodeIterator it = individual.listPropertyValues(property);
				String name = getPropertyName(property);
				while (it.hasNext()) {
						if(!nonFunctionalDataValues.containsKey(name))
							nonFunctionalDataValues.put(name, new ArrayList<Literal>());
						nonFunctionalDataValues.get(name).add(it.next().asLiteral());						
				}
		    }			
		}

		private void IntializeFunctionalDataProperties(
				ArrayList<OntProperty> functionalDataProperties) {
			
			for (OntProperty property : functionalDataProperties){
				RDFNode node = individual.getPropertyValue(property);
				if(node == null)
					continue;
				String name = getPropertyName(property);
				functionalDataValues.put(name,node.asLiteral());
		    }
			
		}
		
		private Resource getResourceFromURI(RDFNode node) {
			return ontologyReader.getIndividual(node.asResource().getURI()).getIndividual();
		}
		
		private String getPropertyName(OntProperty prop){
			String name = null;
			name = prop.getLabel("");
			if(name != null)
				return name;
			return prop.getLocalName();
		}
}
